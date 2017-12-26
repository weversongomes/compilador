package model;

import java.util.ArrayList;

public class EscopoGlobal extends Escopo {
	ArrayList<Simbol> simbols;
	
	public EscopoGlobal() {
		type = "global";
		simbols = new ArrayList<Simbol>();
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
	
	public void showSimbols() {
		System.out.println("PRINTING ALL SIMBOLS");
		for (Simbol s : simbols) {
			System.out.println(s.name + " - " + s.value + " - " + s.type + " - " + s.isConstant);
		}
	}
}
