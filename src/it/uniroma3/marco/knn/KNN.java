package it.uniroma3.marco.knn;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import headliner.treedistance.ComparisonZhangShasha;
import headliner.treedistance.CreateTreeHelper;
import headliner.treedistance.OpsZhangShasha;
import headliner.treedistance.TreeDefinition;
import it.uniroma3.marco.util.ConvertiTree;

public class KNN {
	private Map<String, String> trainingSet;

	public KNN() {
		trainingSet = new HashMap<>();
	}

	public void training(String trainsetPath) {
		File f = new File(trainsetPath);
		if (!f.exists() || !f.canRead()) {
			return;
		}
		ConvertiTree ct = new ConvertiTree();
		try (FileReader fr = new FileReader(f); Scanner scan = new Scanner(fr)) {
			while (scan.hasNextLine()) {
				String sentence = scan.nextLine();
				String head = soloTesta(sentence);
				int index = head.lastIndexOf(":");
				if (index > 0)
					head = head.substring(0, index);
				String noHead = eliminaTesta(sentence);
				noHead = ct.convertiDaNLP(noHead);
				if (noHead != null) {
					trainingSet.put(noHead, head);
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String determinaClasse(int k, String daValutare) {
		ConvertiTree ct = new ConvertiTree();
		daValutare = ct.convertiDaNLP(daValutare);
		TreeDefinition td = CreateTreeHelper.makeTree(daValutare);
		ComparisonZhangShasha czs = new ComparisonZhangShasha();
		OpsZhangShasha ops = new OpsZhangShasha();
		List<CostClasse> costClasse = new ArrayList<>();
		for (String t : trainingSet.keySet()) {
			TreeDefinition tree = CreateTreeHelper.makeTree(t);
			double cost = czs.findDistance(td, tree, ops).getCost();
			costClasse.add(new CostClasse(cost, trainingSet.get(t)));
		}
		Collections.sort(costClasse);
		int max = 0;
		String maxClasse = "";
		Map<String, Integer> classi = new HashMap<>();
		for (int i = 0; i < k; i++) {
			String classe = costClasse.get(i).getClasse();
			Integer cont = classi.get(classe);
			if (cont == null)
				cont = 0;
			cont++;
			if (cont > max) {
				max = cont;
				maxClasse = classe;
			}
			classi.put(classe, cont);
		}
		return maxClasse;
	}

	private String soloTesta(String frase) {
		String head = null;
		int index = frase.indexOf(" ");
		if (index > 0) {
			head = frase.substring(0, index);
		}
		return head;
	}

	private String eliminaTesta(String frase) {
		String noHead = null;
		int index = frase.indexOf(" ");
		if (index > 0) {
			noHead = frase.substring(index + 1);
		}
		return noHead;
	}

	private class CostClasse implements Comparable<CostClasse> {
		private double cost;
		private String classe;

		public CostClasse(double cost, String classe) {
			super();
			this.cost = cost;
			this.classe = classe;
		}

		public String getClasse() {
			return classe;
		}

		@Override
		public int compareTo(CostClasse o) {
			return Double.compare(cost, o.cost);
		}
	}
}
