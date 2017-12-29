package model;

import java.util.ArrayList;

public class EscopoMetodo extends Escopo {
	public EscopoClasse escopoPai;
	private ArrayList<Symbol> symbols;
	private ArrayList<String> params;

	public EscopoMetodo() {
		type = "metodo";
		symbols = new ArrayList<Symbol>();
		params = new ArrayList<>();
	}

	public EscopoClasse getEscopoPai() {
		return escopoPai;
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
	 * @param symbol
	 * @return operation result
	 */
	public int addParam(Symbol symbol) {
		if (!symbols.contains(symbol)) {
			symbols.add(symbol);
			params.add(symbol.name);
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

	public ArrayList<Symbol> getSimbols() {
		return this.symbols;
	}

	/**
	 * 
	 * @param simbol
	 * @return value
	 */
	public String getSimbolValue(String simbolName) {
		Symbol symbol = new Symbol();
		symbol.name = simbolName;
		if (symbols.contains(symbol)) {
			symbol = symbols.get(symbols.indexOf(symbol));
			return symbol.value;
		}
		return null;
	}

	/**
	 * 
	 * @param simbol
	 * @return type
	 */
	public String getSimbolType(String simbolName) {
		Symbol symbol = new Symbol();
		symbol.name = simbolName;
		if (symbols.contains(symbol)) {
			symbol = symbols.get(symbols.indexOf(symbol));
			return symbol.type;
		}
		return null;
	}
	
	public ArrayList<String> getParams() {
		return params;
	}

	public void showSimbols() {
		System.out.println("PARAMS ESCOPO METODO");
		for (String s : params) {
			System.out.println(s);
		}
		System.out.println("SIMBOLOS ESCOPO METODO");
		for (Symbol s : symbols) {
			System.out.println(s.name + " - " + s.value + " - " + s.type + " - " + s.isConstant);
		}
	}
}
