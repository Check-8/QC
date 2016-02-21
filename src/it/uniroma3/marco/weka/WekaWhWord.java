package it.uniroma3.marco.weka;

import java.util.List;
import java.util.Set;

import it.uniroma3.marco.Vettore;

public class WekaWhWord extends WekaAbstract {

	public WekaWhWord() {
		super("WhWord");
	}

	@Override
	public List<String> additionalAttributes(List<Vettore> listaTraining) {
		return null;
	}

	@Override
	public Set<String> elementiPerVettore(Vettore v) {
		return null;
	}

}
