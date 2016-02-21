package it.uniroma3.marco.weka;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import it.uniroma3.marco.Vettore;
import it.uniroma3.marco.weka.classificatori.Classificatore;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public abstract class WekaAbstract {
	private static final ForkJoinPool fjPool = new ForkJoinPool();
	public static final String[] CLASS_COARSE = new String[] { "HUM", "NUM", "ABBR", "LOC", "DESC", "ENTY" };
	private String descrizioneAttributi;
	private List<Classificatore> classificatori;

	public WekaAbstract(String descrizioneAttributi) {
		this.descrizioneAttributi = descrizioneAttributi;
		classificatori = new ArrayList<>();
	}

	public abstract Set<String> elementiPerVettore(Vettore v);

	/**
	 * Solo nomi degli attributi, i valori sono sempre False/True
	 */
	public List<String> additionalAttributes(List<Vettore> listaTraining) {
		Set<String> support = new HashSet<>();
		for (Vettore v : listaTraining) {
			Set<String> elementi = elementiPerVettore(v);
			if (elementi != null)
				support.addAll(elementi);
		}
		return new ArrayList<>(support);
	}

	public void classifica(List<Vettore> listaTraining, List<Vettore> listaTest) {
		FastVector attributes = new FastVector();
		attributes.addElement(attributeClassCoarse());
		attributes.addElement(attributeWhWord());
		List<String> addAttributes = additionalAttributes(listaTraining);
		if (addAttributes != null) {
			List<Attribute> attr = attributesTrueFalse(addAttributes);
			for (Attribute a : attr) {
				attributes.addElement(a);
			}
			attr = null;
		}

		Instances trainingSet = new Instances("Train", attributes, 10);
		trainingSet.setClassIndex(0);
		Instances testSet = new Instances("Test", attributes, 10);
		testSet.setClassIndex(0);

		fjPool.invoke(new Worker(listaTraining, 0, listaTraining.size(), attributes, addAttributes, trainingSet));

		fjPool.invoke(new Worker(listaTest, 0, listaTest.size(), attributes, addAttributes, testSet));

		attributes = null;
		addAttributes = null;

		if (classificatori == null || classificatori.isEmpty()) {
			Classificatore classJ48 = new Classificatore("J48", J48.class);
			System.out.println(classJ48.esegui(trainingSet, testSet, descrizioneAttributi));
		} else {
			for (Classificatore c : classificatori) {
				System.out.println(c.esegui(trainingSet, testSet, descrizioneAttributi));
			}
		}
	}

	private Instance setAttributeValueFalse(Instance instance, FastVector attributes) {
		double[] attrValue = instance.toDoubleArray();
		for (int i = 2; i < attrValue.length; i++) {
			attrValue[i] = 0;
		}
		Instance newInstance = new Instance(instance.weight(), attrValue);
		return newInstance;
	}

	public Attribute attributeClassCoarse() {
		FastVector valoriClasse = new FastVector(6);
		valoriClasse.addElement(CLASS_COARSE[0]);
		valoriClasse.addElement(CLASS_COARSE[1]);
		valoriClasse.addElement(CLASS_COARSE[2]);
		valoriClasse.addElement(CLASS_COARSE[3]);
		valoriClasse.addElement(CLASS_COARSE[4]);
		valoriClasse.addElement(CLASS_COARSE[5]);
		Attribute classe = new Attribute("classe", valoriClasse);
		return classe;
	}

	public Attribute attributeWhWord() {
		FastVector valoriWhWords = new FastVector(8);
		valoriWhWords.addElement("who");
		valoriWhWords.addElement("why");
		valoriWhWords.addElement("what");
		valoriWhWords.addElement("when");
		valoriWhWords.addElement("where");
		valoriWhWords.addElement("which");
		valoriWhWords.addElement("how");
		valoriWhWords.addElement("rest");
		Attribute whWord = new Attribute("whWord", valoriWhWords);
		return whWord;
	}

	public List<Attribute> attributesTrueFalse(List<String> attrNames) {
		FastVector valoriPos = new FastVector();
		valoriPos.addElement("false");
		valoriPos.addElement("true");
		List<Attribute> attributes = new LinkedList<>();
		for (String s : attrNames) {
			Attribute pos = new Attribute(s, valoriPos);
			attributes.add(pos);
		}
		return attributes;
	}

	public void addClassificatore(Classificatore c) {
		if (c != null)
			classificatori.add(c);
	}

	public void removeClassificatore(Classificatore c) {
		if (c != null)
			classificatori.remove(c);
	}

	public void addClassificatori(List<Classificatore> listClassificatori) {
		if (listClassificatori != null)
			classificatori.addAll(listClassificatori);
	}

	public void setClassificatori(List<Classificatore> listClassificatori) {
		if (listClassificatori == null)
			throw new NullPointerException("Argomento listClassificatori non può essere nullo");
		classificatori = listClassificatori;
	}

	private class Worker extends RecursiveAction {
		private static final long serialVersionUID = -6405615791537700691L;
		private static final int THRESHOLD = 50;
		private List<Vettore> lista;
		private int min;
		private int max;

		private FastVector attributes;
		private List<String> addAttributes;
		private Instances instanceSet;

		public Worker(List<Vettore> lista, int min, int max, FastVector attributes, List<String> addAttributes,
				Instances instanceSet) {
			super();
			this.lista = lista;
			this.min = min;
			this.max = max;
			this.attributes = attributes;
			this.addAttributes = addAttributes;
			this.instanceSet = instanceSet;
		}

		@Override
		protected void compute() {
			if (max - min <= THRESHOLD) {
				int numAttr = attributes.size();
				for (int i = min; i < max; i++) {
					Vettore v = lista.get(i);
					Instance instance = new Instance(numAttr);
					instance.setValue((Attribute) attributes.elementAt(0), v.getClasseCoarse());
					instance.setValue((Attribute) attributes.elementAt(1), v.getWhWord().toLowerCase());

					instance = setAttributeValueFalse(instance, attributes);
					Set<String> setElementi = elementiPerVettore(v);
					if (setElementi != null) {
						double[] attrValue = instance.toDoubleArray();
						for (String parola : setElementi) {
							int index = addAttributes.indexOf(parola);
							if (index > 0)
								attrValue[index + 2] = 1;
						}
						instance = new Instance(instance.weight(), attrValue);
					}
					synchronized (instanceSet) {
						instanceSet.add(instance);
					}
				}
			} else {
				int mid = min + ((max - min) / 2);
				Worker left = new Worker(lista, min, mid, attributes, addAttributes, instanceSet);
				Worker right = new Worker(lista, mid, max, attributes, addAttributes, instanceSet);
				left.fork();
				right.compute();
			}
		}
	}

}
