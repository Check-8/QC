package it.uniroma3.marco;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import it.uniroma3.marco.nlp.POSTagger;
import it.uniroma3.marco.weka.WekaAbstract;
import it.uniroma3.marco.weka.WekaWhWord;
import it.uniroma3.marco.weka.WekaWhWordBigrammaPos;
import it.uniroma3.marco.weka.WekaWhWordNomiAggettivi;
import it.uniroma3.marco.weka.WekaWhWordNomiAggettiviVerbi;
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

public class ElabText {
	// TODO Migliorabile, fare in parallelo
	public List<Vettore> elaboraFile(String path) {
		File f = new File(path);
		if (!f.exists() || !f.canRead())
			return null;
		List<Vettore> lista = new ArrayList<>();
		Scanner scan = null;
		FileReader fr = null;
		try {
			fr = new FileReader(f);
			scan = new Scanner(fr);
			while (scan.hasNextLine()) {
				String s = scan.nextLine();
				Vettore v = scomponi(s);
				lista.add(v);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (scan != null)
				scan.close();
			if (fr != null)
				try {
					fr.close();
				} catch (IOException e) {
				}
		}
		return lista;
	}

	private String getClasse(int index, String s) {
		String parola = null;
		if (index > 0) {
			parola = s.substring(0, index);
			int bla = parola.indexOf("_");
			if (bla > 0)
				parola = parola.substring(0, bla);
		} else {
			parola = s.substring(0);
		}
		return parola;
	}

	private static final int N_COMPONENTI_NGRAMMA = Vettore.N_COMPONENTI_NGRAMMA;

	private List<String[]> getBigrammi(String s) {
		if (s == null)
			return null;
		s = s.trim();
		// Ignora la classe
		int index = s.indexOf(" ");
		if (index < 0 || index + 1 > s.length())
			return null;
		s = s.substring(index + 1);

		List<String[]> list = new ArrayList<>();
		while (true) {
			index = s.indexOf(" ");
			if (index < 0 || index + 1 > s.length())
				break;
			String parola1 = s.substring(0, index);

			s = s.substring(index + 1);

			index = s.indexOf(" ");
			if (index < 0 || index + 1 > s.length())
				break;
			String parola2 = s.substring(0, index);

			String[] pp1 = parola_pos(parola1);
			String[] pp2 = parola_pos(parola2);
			String[] bigrammi = new String[N_COMPONENTI_NGRAMMA];
			for (int i = 0; i < N_COMPONENTI_NGRAMMA; i++) {
				bigrammi[i] = pp1[i] + " " + pp2[i];
			}
			list.add(bigrammi);
			s = s.substring(index + 1);
		}
		return list;
	}

	private String[] parola_pos(String parolaEpos) {
		String parola = null;
		String pos = null;
		int bla = parolaEpos.lastIndexOf("_");
		if (bla > 0) {
			parola = parolaEpos.substring(0, bla);
			pos = parolaEpos.substring(bla + 1);
			if (pos.charAt(0) < 'A' || pos.charAt(0) > 'Z')
				pos = "";
		} else {
			parola = parolaEpos;
			pos = "";
		}
		String[] pep = new String[N_COMPONENTI_NGRAMMA];
		pep[0] = parola;
		pep[1] = pos;
		pep[2] = parolaEpos;
		return pep;
	}

	private List<String> getParolaEPos(int index, String s) {
		List<String> pep = new ArrayList<>(2);
		String parolaEPos = null;
		if (index > 0) {
			parolaEPos = s.substring(0, index);
		} else {
			parolaEPos = s;
		}

		String parola = null;
		String pos = null;
		int bla = parolaEPos.lastIndexOf("_");
		if (bla > 0) {
			parola = parolaEPos.substring(0, bla);
			pos = parolaEPos.substring(bla + 1);
			if (pos.charAt(0) < 'A' || pos.charAt(0) > 'Z')
				pos = "";
		} else {
			parola = parolaEPos;
			pos = "";
		}
		pep.add(parola);
		pep.add(pos);
		pep.add(parolaEPos);
		return pep;
	}

	private Vettore scomponi(String s) {
		Vettore v = new Vettore();
		List<String> pos = new ArrayList<>();
		boolean terminato = false;
		boolean classe = true;
		boolean wh_word = false;
		v.setBigrammi(getBigrammi(s));
		while (!terminato) {
			int index = s.indexOf(" ");
			if (classe) {
				v.setClasse(getClasse(index, s));
				classe = false;
			} else if (!wh_word) {
				List<String> pep = getParolaEPos(index, s);
				if (!pep.get(1).equals("")) {
					pos.add(pep.get(1));
					v.addTag(pep.get(1));
				}
				String parola = pep.get(0).toLowerCase(Locale.ENGLISH);
				if (checkWhWord(parola))
					v.setWhWord(parola);
				v.addValore(parola, 1);
				v.addParolaConTag(pep.get(2));
			}
			if (index > 0)
				s = s.substring(index + 1);
			else
				terminato = true;
		}
		StringBuilder tagPos = new StringBuilder();
		Collections.sort(pos);
		for (String p : pos) {
			tagPos.append(p + " ");
		}
		v.setTag(tagPos.toString());
		return v;
	}

	private static final String[] WH_WORDS = { "who", "what", "where", "when", "which", "why", "how" };

	private boolean checkWhWord(String parola) {
		int index = parola.lastIndexOf("_");
		String p = null;
		if (index >= 0)
			p = parola.substring(0, index);
		else
			p = parola;
		for (String wh_word : WH_WORDS) {
			if (p.equalsIgnoreCase(wh_word))
				return true;
		}
		return false;
	}

	public static void main(String[] args) {
		if (!new File("tagged.txt").exists()) {
			POSTagger tagger = new POSTagger();
			tagger.taggaFile("train.txt", "tagged.txt");
		}
		if (!new File("taggedTest.txt").exists()) {
			POSTagger tagger = new POSTagger();
			tagger.taggaFile("test.txt", "taggedTest.txt");
		}
		ElabText elab = new ElabText();
		List<Vettore> vetTrain = elab.elaboraFile("tagged.txt");
		List<Vettore> vetTest = elab.elaboraFile("taggedTest.txt");

		List<Classificatore> listClassificatori = new ArrayList<>();
		listClassificatori.add(new Classificatore("J48", J48.class));
		listClassificatori.add(new Classificatore("Naive Bayes", NaiveBayes.class));
		listClassificatori.add(new Classificatore("SMO - Sequential minimal optimization", SMO.class));

		// List<WekaAbstract> daEseguire = new LinkedList<>();
		// for (WekaAbstract wa : daEseguire) {
		// wa.classifica(vetTrain, vetTest);
		// }

		WekaAbstract whw = new WekaWhWord();
		whw.setClassificatori(listClassificatori);

		WekaAbstract whwptw = new WekaWhWordPosTagWord();
		whwptw.setClassificatori(listClassificatori);

		WekaAbstract whwpt = new WekaWhWordPosSet();
		whwpt.setClassificatori(listClassificatori);

		WekaAbstract whwps = new WekaWhWordPosString();
		whwps.setClassificatori(listClassificatori);

		WekaAbstract whwpct = new WekaWhWordParoleConTag();
		whwpct.setClassificatori(listClassificatori);

		WekaAbstract whwpspct = new WekaWhWordPosSetParoleConTag();
		whwpspct.setClassificatori(listClassificatori);

		WekaAbstract whwnnjj = new WekaWhWordNomiAggettivi();
		whwnnjj.setClassificatori(listClassificatori);

		WekaAbstract whwvb = new WekaWhWordVerbi();
		whwvb.setClassificatori(listClassificatori);

		WekaAbstract whwnnjjvb = new WekaWhWordNomiAggettiviVerbi();
		whwnnjjvb.setClassificatori(listClassificatori);

		WekaAbstract whwbpt = new WekaWhWordBigrammaPos();
		whwbpt.setClassificatori(listClassificatori);

		whw.classifica(vetTrain, vetTest);
		whw = null;
		// whwps.classifica(vetTrain, vetTest);
		// whwps = null;
		whwpt.classifica(vetTrain, vetTest);
		whwpt = null;
		// whwptw.classifica(vetTrain, vetTest);
		// whwptw = null;
		// whwpct.classifica(vetTrain, vetTest);
		// whwpct = null;
		// whwpspct.classifica(vetTrain, vetTest);
		// whwpspct = null;
		// whwnnjj.classifica(vetTrain, vetTest);
		// whwnnjj = null;
		// whwvb.classifica(vetTrain, vetTest);
		// whwvb = null;
		// whwnnjjvb.classifica(vetTrain, vetTest);
		// whwnnjjvb = null;
		// whwbpt.classifica(vetTrain, vetTest);
		// whwbpt = null;
	}
}
