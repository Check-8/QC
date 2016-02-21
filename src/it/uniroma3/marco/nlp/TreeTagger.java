package it.uniroma3.marco.nlp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;
import it.uniroma3.marco.knn.KNN;
import it.uniroma3.marco.util.Util;

public class TreeTagger {
	public void makeTree(String pathSource, String pathDest) {
		File f = new File(pathSource);
		if (!f.exists() || !f.canRead()) {
			return;
		}
		LexicalizedParser parser = LexicalizedParser.loadModel("tagger/englishPCFG.ser");
		try (FileWriter fw = new FileWriter(pathDest);
				FileReader fr = new FileReader(f);
				Scanner scan = new Scanner(fr)) {
			while (scan.hasNextLine()) {
				String sentence = scan.nextLine();
				String head = Util.soloTesta(sentence);
				String noHead = Util.eliminaTesta(sentence);
				if (noHead != null) {
					Tree senTree = parser.parse(noHead);
					fw.write(head + " " + senTree.toString() + "\n");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TreeTagger tt = new TreeTagger();
		tt.makeTree("test.txt", "treeTest.txt");
		KNN knn = new KNN();
		knn.training("treeTest.txt");
		try (FileReader fr = new FileReader("treeTest.txt"); Scanner scan = new Scanner(fr)) {
			while (scan.hasNextLine()) {
				String sentence = scan.nextLine();
				String daValutare = Util.eliminaTesta(sentence);
				String classe = Util.soloTesta(sentence);
				String classeTrovata = knn.determinaClasse(5, daValutare);
				System.out.println(classe + " " + classeTrovata);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
