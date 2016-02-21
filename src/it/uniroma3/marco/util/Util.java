package it.uniroma3.marco.util;

public class Util {
	public static String soloTesta(String frase) {
		String head = null;
		int index = frase.indexOf(" ");
		if (index > 0) {
			head = frase.substring(0, index);
		}
		return head;
	}

	public static String eliminaTesta(String frase) {
		String noHead = null;
		int index = frase.indexOf(" ");
		if (index > 0) {
			noHead = frase.substring(index + 1);
		}
		return noHead;
	}
}
