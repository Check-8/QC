package it.uniroma3.marco.weka;

import java.util.HashSet;
import java.util.Set;

import it.uniroma3.marco.Vettore;

public class WekaWhWordPosSet extends WekaAbstract {

	public WekaWhWordPosSet() {
		super("WhWord, PoS tag");
	}

	@Override
	public Set<String> elementiPerVettore(Vettore v) {
		Set<String> setElementi = new HashSet<>();
		setElementi.addAll(v.getSetPosTag());
		return setElementi;
	}

}
