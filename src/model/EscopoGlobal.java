package model;

import java.util.ArrayList;

public class EscopoGlobal {
	ArrayList<Simbol> simbols;
	
	/**
	 * 
	 * @param simbol
	 * @return operation result
	 */
	public int addSimbol(String simbolName) {
		Simbol simbol = new Simbol();
		simbol.name = simbolName;
		if (!simbols.contains(simbol)) {
			simbols.add(simbol);
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
		if (simbols.contains(simbolName)) {
			Simbol simbol = simbols.get(simbols.indexOf(simbols.contains(simbolName)));
			simbol.value = value;
			simbol.type = "const";
		} else {
			
		}
		return 0;
	}
}
