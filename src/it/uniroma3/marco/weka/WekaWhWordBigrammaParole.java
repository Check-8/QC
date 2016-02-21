package it.uniroma3.marco.weka;

import java.util.HashSet;
import java.util.Set;

import it.uniroma3.marco.Vettore;

public class WekaWhWordBigrammaParole extends WekaAbstract {

	public WekaWhWordBigrammaParole() {
		super("WhWord, Bigramma Parole");
	}

	@Override
	public Set<String> elementiPerVettore(Vettore v) {
		Set<String> setElementi = new HashSet<>();
		setElementi.addAll(v.getSetBigrammiParole());
		return setElementi;
	}

}
