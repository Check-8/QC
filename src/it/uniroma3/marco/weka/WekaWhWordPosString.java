package it.uniroma3.marco.weka;

import java.util.HashSet;
import java.util.Set;

import it.uniroma3.marco.Vettore;

public class WekaWhWordPosString extends WekaAbstract {

	public WekaWhWordPosString() {
		super("WhWord, PoS tag in stringa unica");
	}

	@Override
	public Set<String> elementiPerVettore(Vettore v) {
		Set<String> setElementi = new HashSet<>();
		setElementi.add(v.getTag());
		return setElementi;
	}

}
