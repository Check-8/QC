package it.uniroma3.marco.weka;

import java.util.HashSet;
import java.util.Set;

import it.uniroma3.marco.Vettore;

public class WekaWhWordBigrammaParoleConTag extends WekaAbstract {

	public WekaWhWordBigrammaParoleConTag() {
		super("WhWord, Bigramma Parole con PoS Tag");
	}

	@Override
	public Set<String> elementiPerVettore(Vettore v) {
		Set<String> setElementi = new HashSet<>();
		setElementi.addAll(v.getSetBigrammiParoleConPosTag());
		return setElementi;
	}

}
