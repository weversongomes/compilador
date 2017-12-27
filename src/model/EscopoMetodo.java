package model;

import java.util.ArrayList;

public class EscopoMetodo extends Escopo {
	public EscopoClasse escopoPai;
	private ArrayList<Simbol> simbols;
	
	public EscopoMetodo() {
		type = "metodo";
		simbols = new ArrayList<Simbol>();
	}

	public EscopoClasse getEscopoPai() {
		return escopoPai;
	}

	/**
	 * 
	 * @param simbol
	 * @return operation result
	 */
	public int addSimbol(Simbol simbol) {
		if (!simbols.contains(simbol)) {
			simbols.add(simbol);
			return 1;
		}
		return 0;
	}

	/**
	 * 
	 * @param simbol
	 * @param value
	 * @return operation result
	 */
	public int setSimbolValue(String simbolName, String value) {
		Simbol simbol = new Simbol();
		simbol.name = simbolName;
		if (simbols.contains(simbol)) {
			simbol = simbols.get(simbols.indexOf(simbol));
			simbol.value = value;
		} else {
			System.out.println("OBJETO NAO ENCONTRADO");
		}
		return 0;
	}

	public ArrayList<Simbol> getSimbols() {
		return this.simbols;
	}

	/**
	 * 
	 * @param simbol
	 * @return value
	 */
	public String getSimbolValue(String simbolName) {
		Simbol simbol = new Simbol();
		simbol.name = simbolName;
		if (simbols.contains(simbol)) {
			simbol = simbols.get(simbols.indexOf(simbol));
			return simbol.value;
		}
		return null;
	}
	
	public void showSimbols() {
		System.out.println("SIMBOLOS ESCOPO METODO");
		for (Simbol s : simbols) {
			System.out.println(s.name + " - " + s.value + " - " + s.type + " - " + s.isConstant);
		}
	}
}
