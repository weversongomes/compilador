package model;

import java.util.ArrayList;

public class EscopoGlobal extends Escopo {
	ArrayList<Symbol> symbols;
	
	public EscopoGlobal() {
		type = "global";
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

	/**
	 * 
	 * @param simbol
	 * @param value
	 * @return operation result
	 */
	public int setSimbolValue(String simbolName, String value) {
		Symbol symbol = new Symbol();
		symbol.name = simbolName;
		if (symbols.contains(symbol)) {
			symbol = symbols.get(symbols.indexOf(symbol));
			symbol.value = value;
		} else {
			System.out.println("OBJETO NAO ENCONTRADO");
		}
		return 0;
	}
	
	public void showSimbols() {
		System.out.println("SIMBOLOS ESCOPO GLOBAL");
		for (Symbol s : symbols) {
			System.out.println(s.name + " - " + s.value + " - " + s.type + " - " + s.isConstant);
		}
	}
}
