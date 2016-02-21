package it.uniroma3.marco.nlp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POSTagger {
	/**
	 * 
	 * @param pathSource
	 * @param pathDest
	 */
	public void taggaFile(String pathSource, String pathDest) {
		File f = new File(pathSource);
		MaxentTagger tagger = new MaxentTagger("tagger/english-bidirectional-distsim.tagger");
		if (!f.exists() || !f.canRead())
			return;
		Scanner scan = null;
		try (FileReader fr = new FileReader(f); FileWriter fw = new FileWriter(pathDest)) {
			scan = new Scanner(fr);
			while (scan.hasNextLine()) {
				String s = scan.nextLine();
				String head = soloTesta(s);
				String noHead = eliminaTesta(s);
				if (noHead != null) {
					String tagged = tagger.tagString(noHead);
					fw.write(head + " " + tagged + "\n");
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (scan != null)
				scan.close();
		}
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
}
