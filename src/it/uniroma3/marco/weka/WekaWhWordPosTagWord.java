package it.uniroma3.marco.weka;

import java.util.HashSet;
import java.util.Set;

import it.uniroma3.marco.Vettore;

public class WekaWhWordPosTagWord extends WekaAbstract {

	public WekaWhWordPosTagWord() {
		super("WhWord, PoS tag, Parole");
	}

	@Override
	public Set<String> elementiPerVettore(Vettore v) {
		Set<String> setElementi = new HashSet<>();
		setElementi.addAll(v.getSetPosTag());
		setElementi.addAll(v.getVettore().keySet());
		return setElementi;
	}

}
