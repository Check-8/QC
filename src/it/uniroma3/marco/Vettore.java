package it.uniroma3.marco;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Vettore {
	public static final int N_COMPONENTI_NGRAMMA = 3;
	private static long NEXT_ID = 0;
	private long id;

	private Map<String, Integer> vettore;
	private List<String> listaPosTag;
	private List<String> listaParoleConTag;
	private List<String> listaBigrammiParole;
	private List<String> listaBigrammiPosTag;
	private List<String> listaBigrammiParoleConPosTag;
	private String tag;
	private String wh_word;
	private String classeCoarse;
	private String classeFine;

	public Vettore() {
		synchronized (this.getClass()) {
			id = NEXT_ID;
			NEXT_ID++;
		}
		vettore = new HashMap<>();
		wh_word = "rest";
		classeCoarse = null;
		classeFine = null;
		tag = null;
		listaPosTag = new ArrayList<>();
		listaParoleConTag = new ArrayList<>();
		listaBigrammiParole = new ArrayList<>();
		listaBigrammiPosTag = new ArrayList<>();
		listaBigrammiParoleConPosTag = new ArrayList<>();
	}

	public void addParolaConTag(String p) {
		listaParoleConTag.add(p);
	}

	public List<String> getListaParoleConTag() {
		return Collections.unmodifiableList(listaParoleConTag);
	}

	public Set<String> getSetParoleConTag() {
		return new HashSet<>(listaParoleConTag);
	}

	public void addTag(String t) {
		listaPosTag.add(t);
	}

	public List<String> getListaPosTag() {
		return Collections.unmodifiableList(listaPosTag);
	}

	public Set<String> getSetPosTag() {
		return new HashSet<>(listaPosTag);
	}

	public void setTag(String tagPos) {
		tag = tagPos;
	}

	public String getTag() {
		return tag;
	}

	public void setWhWord(String whWord) {
		wh_word = whWord;
	}

	public String getWhWord() {
		return wh_word;
	}

	public void setClasse(String c) {
		int index = c.indexOf(":");
		if (index < 0)
			throw new RuntimeException("Non e' una classe valida");
		classeCoarse = c.substring(0, index);
		classeFine = c.substring(index + 1);
	}

	public String getClasse() {
		return classeCoarse + ":" + classeFine;
	}

	public String getClasseCoarse() {
		return classeCoarse;
	}

	public String getClasseFine() {
		return classeFine;
	}

	public void addValore(String parola, int valore) {
		vettore.put(parola, valore);
	}

	public int getValore(String parola) {
		Integer v = vettore.get(parola);
		int ret;
		if (v == null)
			ret = 0;
		else
			ret = v;
		return ret;
	}

	public int removeValore(String parola) {
		return vettore.remove(parola);
	}

	public Map<String, Integer> getVettore() {
		return Collections.unmodifiableMap(vettore);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vettore other = (Vettore) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void setBigrammi(List<String[]> bigrammi) {
		for (String[] b : bigrammi) {
			listaBigrammiParole.add(b[0]);
			listaBigrammiPosTag.add(b[1]);
			listaBigrammiParoleConPosTag.add(b[2]);
		}
	}

	public List<String> getListaBigrammiParole() {
		return Collections.unmodifiableList(listaBigrammiParole);
	}

	public List<String> getListaBigrammiPosTag() {
		return Collections.unmodifiableList(listaBigrammiPosTag);
	}

	public List<String> getListaBigrammiParoleConPosTag() {
		return Collections.unmodifiableList(listaBigrammiParoleConPosTag);
	}

	public Set<String> getSetBigrammiParole() {
		return new HashSet<>(listaBigrammiParole);
	}

	public Set<String> getSetBigrammiPosTag() {
		return new HashSet<>(listaBigrammiPosTag);
	}

	public Set<String> getSetBigrammiParoleConPosTag() {
		return new HashSet<>(listaBigrammiParoleConPosTag);
	}

}
