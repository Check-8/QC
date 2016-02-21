package it.uniroma3.marco.weka;

import java.util.HashSet;
import java.util.Set;

import it.uniroma3.marco.Vettore;

public class WekaWhWordBigrammaPos extends WekaAbstract {

	public WekaWhWordBigrammaPos() {
		super("WhWord, Bigramma PoS Tag");
	}

	@Override
	public Set<String> elementiPerVettore(Vettore v) {
		Set<String> setElementi = new HashSet<>();
		setElementi.addAll(v.getSetBigrammiPosTag());
		return setElementi;
	}

}
