package model;

import java.util.ArrayList;

public class EscopoClasse extends Escopo {
	ArrayList<Simbol> simbols;
	Escopo escopoPai;
	
	public EscopoClasse() {
		type = "classe";
		simbols = new ArrayList<Simbol>();
	}
	
	/**
	 * 
	 * @param simbol
	 * @return operation result
	 */
	public int addSimbol(String simbolName, String simbolType) {
		Simbol simbol = new Simbol();
		simbol.name = simbolName;
		simbol.type = simbolType;
		if (!simbols.contains(simbol)) {
			simbols.add(simbol);
		}
		return 0;
	}
	
	public void showSimbols() {
		System.out.println("SIMBOLOS ESCOPO CLASSE");
		for (Simbol s : simbols) {
			System.out.println(s.name + " - " + s.value + " - " + s.type + " - " + s.isConstant);
		}
	}
}
