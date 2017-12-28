package model;

import java.util.ArrayList;

public class EscopoClasse extends Escopo {
	ArrayList<Symbol> symbols;
	Escopo escopoPai;
	
	public EscopoClasse() {
		type = "classe";
		symbols = new ArrayList<Symbol>();
	}
	
	/**
	 * 
	 * @param symbol
	 * @return operation result
	 */
	public int addSimbol(Symbol symbol) {
		if (!symbols.contains(symbol)) {
			symbols.add(symbol);
			return 1;
		}
		return 0;
	}
	
	public void showSimbols() {
		System.out.println("SIMBOLOS ESCOPO CLASSE");
		for (Symbol s : symbols) {
			System.out.println(s.name + " - " + s.value + " - " + s.type + " - " + s.isConstant);
		}
	}
}
