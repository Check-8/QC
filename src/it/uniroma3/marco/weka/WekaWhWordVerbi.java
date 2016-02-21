package it.uniroma3.marco.weka;

import java.util.HashSet;
import java.util.Set;

import it.uniroma3.marco.Vettore;

public class WekaWhWordVerbi extends WekaAbstract {

	public WekaWhWordVerbi() {
		super("WhWord, Verbi");
	}

	@Override
	public Set<String> elementiPerVettore(Vettore v) {
		Set<String> setElementi = new HashSet<>();
		for (String parola : v.getSetParoleConTag()) {
			int index = parola.lastIndexOf("_");
			if (index > 0) {
				String tag = parola.substring(index + 1);
				if (tag.startsWith("VB"))
					setElementi.add(parola);
			}
		}
		return setElementi;
	}

}
