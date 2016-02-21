package it.uniroma3.marco;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

import it.uniroma3.marco.knn.EvaluateKNN;
import it.uniroma3.marco.nlp.POSTagger;
import it.uniroma3.marco.nlp.TreeTagger;
import it.uniroma3.marco.weka.WekaAbstract;
import it.uniroma3.marco.weka.WekaWhWord;
import it.uniroma3.marco.weka.WekaWhWordBigrammaParole;
import it.uniroma3.marco.weka.WekaWhWordBigrammaParoleConTag;
import it.uniroma3.marco.weka.WekaWhWordBigrammaPos;
import it.uniroma3.marco.weka.WekaWhWordNomiAggettivi;
import it.uniroma3.marco.weka.WekaWhWordNomiAggettiviVerbi;
import it.uniroma3.marco.weka.WekaWhWordParole;
import it.uniroma3.marco.weka.WekaWhWordParoleConTag;
import it.uniroma3.marco.weka.WekaWhWordPosSet;
import it.uniroma3.marco.weka.WekaWhWordPosSetParoleConTag;
import it.uniroma3.marco.weka.WekaWhWordPosString;
import it.uniroma3.marco.weka.WekaWhWordPosTagWord;
import it.uniroma3.marco.weka.WekaWhWordVerbi;
import it.uniroma3.marco.weka.classificatori.Classificatore;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.SMO;
import weka.classifiers.trees.J48;

public class QCMain {
	private static final String DEFAULT_TRAININGSET_PATH = "train.txt";
	private static final String DEFAULT_TESTSET_PATH = "test.txt";

	private static final String TAGGED_TRAININGSET_PATH = "taggedTrain.txt";
	private static final String TAGGED_TESTSET_PATH = "taggedTest.txt";

	private static final String TREE_TRAININGSET_PATH = "treeTrain.txt";
	private static final String TREE_TESTSET_PATH = "treeTest.txt";

	private String trainpath;
	private String testpath;

	private Scanner scan;

	public QCMain() {
		trainpath = DEFAULT_TRAININGSET_PATH;
		testpath = DEFAULT_TESTSET_PATH;
	}

	private void printMenu() {
		System.out.println("TEST DISPONIBILI:");
		System.out.println("C)Classificatori: J48, Naive Bayes, SMO");
		System.out.println("K)KNN");
		System.out.println("?:");
	}

	private void printMenuTest() {
		System.out.println("TEST DISPONIBILI:");
		System.out.println(" 1)Wh Word");
		System.out.println(" 2)Wh Word, PoS tag in stringa unica");
		System.out.println(" 3)Wh Word, set PoS tag");
		System.out.println(" 4)Wh Word, set Parole");
		System.out.println(" 5)Wh Word, set Parole con PoS tag associato");
		System.out.println(" 6)Wh Word, set bigrammi di PoS tag");
		System.out.println(" 7)Wh Word, set bigrammi di Parole");
		System.out.println(" 8)Wh Word, set bigrammi di Parole con PoS tag associato");
		System.out.println(" 9)Wh Word, set PoS tag, set Parole");
		System.out.println("10)Wh Word, set Pos tag, set Parole con tag associato");
		System.out.println("11)Wh Word, set Nomi, set Aggettivi");
		System.out.println("12)Wh Word, set Verbi");
		System.out.println("13)Wh Word, set Nomi, set Aggettivi, set Verbi");
		System.out.println("Inserire i numeri dei test che si vuole effettuare, separati da uno spazio:");
	}

	public List<WekaAbstract> sceltaTest(List<Classificatore> listClassificatori) {
		printMenuTest();
		List<WekaAbstract> lista = new LinkedList<WekaAbstract>();
		String read = "";
		do {
			try {
				read = scan.nextLine();
			} catch (NoSuchElementException e) {
				read = "";
			}
			read = read.trim();
		} while (read.equals(""));
		int[] indici = readIndici(read);
		for (int i : indici) {
			WekaAbstract wa = getWekaAbstract(i);
			if (wa != null) {
				wa.setClassificatori(listClassificatori);
				lista.add(wa);
			}
		}
		return lista;
	}

	public int sceltaTipoTest() {
		printMenu();
		String read = "";
		do {
			read = scan.nextLine();
			read = read.trim();
		} while (read.equals(""));
		return readTipo(read);
	}

	private int readTipo(String read) {
		char c = read.charAt(0);
		switch (c) {
		case 'C':
		case 'c':
			return 0;
		case 'K':
		case 'k':
			return 1;
		default:
			return 0;
		}
	}

	private WekaAbstract getWekaAbstract(int i) {
		switch (i) {
		case 1:
			return new WekaWhWord();
		case 2:
			return new WekaWhWordPosString();
		case 3:
			return new WekaWhWordPosSet();
		case 4:
			return new WekaWhWordParole();
		case 5:
			return new WekaWhWordParoleConTag();
		case 6:
			return new WekaWhWordBigrammaPos();
		case 7:
			return new WekaWhWordBigrammaParole();
		case 8:
			return new WekaWhWordBigrammaParoleConTag();
		case 9:
			return new WekaWhWordPosTagWord();
		case 10:
			return new WekaWhWordPosSetParoleConTag();
		case 11:
			return new WekaWhWordNomiAggettivi();
		case 12:
			return new WekaWhWordVerbi();
		case 13:
			return new WekaWhWordNomiAggettiviVerbi();

		default:
			return null;
		}
	}

	private int[] readIndici(String read) {
		Set<Integer> setIndici = new HashSet<>();
		boolean terminato = false;
		while (!terminato) {
			int index = read.indexOf(" ");
			String temp = "";
			if (index > 0) {
				temp = read.substring(0, index);
				read = read.substring(index + 1);
			} else {
				temp = read;
				terminato = true;
			}
			Integer num = null;
			try {
				num = Integer.valueOf(temp);
			} catch (NumberFormatException e) {
				num = 0;
			}
			setIndici.add(num);
		}
		int[] indici = new int[setIndici.size()];
		int i = 0;
		for (Integer num : setIndici) {
			indici[i] = num;
			i++;
		}
		Arrays.sort(indici);
		return indici;
	}

	public static void main(String[] args) {
		QCMain qc = new QCMain();
		try {
			qc.scan = new Scanner(System.in);
			int tipo = qc.sceltaTipoTest();
			if (tipo == 0) {
				if (!new File(TAGGED_TRAININGSET_PATH).exists()) {
					POSTagger tagger = new POSTagger();
					tagger.taggaFile(qc.trainpath, TAGGED_TRAININGSET_PATH);
				}
				if (!new File(TAGGED_TESTSET_PATH).exists()) {
					POSTagger tagger = new POSTagger();
					tagger.taggaFile(qc.testpath, TAGGED_TESTSET_PATH);
				}
				ElabText elab = new ElabText();
				List<Vettore> vetTrain = elab.elaboraFile(TAGGED_TRAININGSET_PATH);
				List<Vettore> vetTest = elab.elaboraFile(TAGGED_TESTSET_PATH);

				List<Classificatore> listClassificatori = new ArrayList<>();
				listClassificatori.add(new Classificatore("J48", J48.class));
				listClassificatori.add(new Classificatore("Naive Bayes", NaiveBayes.class));
				listClassificatori.add(new Classificatore("SMO - Sequential minimal optimization", SMO.class));

				List<WekaAbstract> daEseguire = qc.sceltaTest(listClassificatori);
				System.out.println("Esecuzione avviata");
				for (WekaAbstract wa : daEseguire) {
					wa.classifica(vetTrain, vetTest);
				}
			} else if (tipo == 1) {
				if (!new File(TREE_TRAININGSET_PATH).exists()) {
					TreeTagger tagger = new TreeTagger();
					tagger.makeTree(qc.trainpath, TREE_TRAININGSET_PATH);
				}
				if (!new File(TREE_TESTSET_PATH).exists()) {
					TreeTagger tagger = new TreeTagger();
					tagger.makeTree(qc.testpath, TREE_TESTSET_PATH);
				}
				System.out.println("Esecuzione avviata");
				EvaluateKNN eval = new EvaluateKNN();
				String s = eval.valuta(TREE_TRAININGSET_PATH, TREE_TESTSET_PATH);
				System.out.println(s);
			}
		} finally {
			if (qc.scan != null)
				qc.scan.close();
		}
	}
}
