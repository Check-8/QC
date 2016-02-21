package it.uniroma3.marco.weka;

import java.util.HashSet;
import java.util.Set;

import it.uniroma3.marco.Vettore;

public class WekaWhWordParoleConTag extends WekaAbstract {

	public WekaWhWordParoleConTag() {
		super("WhWord, Parole con PoS tag");
	}

	@Override
	public Set<String> elementiPerVettore(Vettore v) {
		Set<String> setElementi = new HashSet<>();
		setElementi.addAll(v.getSetParoleConTag());
		return setElementi;
	}

}
