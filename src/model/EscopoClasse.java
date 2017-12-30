package model;

import java.util.ArrayList;

public class EscopoClasse extends Escopo {
	ArrayList<Symbol> symbols;
	Escopo escopoPai;
	
	public EscopoClasse() {
		type = "class";
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
		System.out.println("SIMBOLOS ESCOPO CLASSE " + name);
		for (Symbol s : symbols) {
			System.out.println(s.name + " - " + s.value + " - " + s.type + " - " + s.isConstant);
		}
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
	
	public Escopo getEscopoPai() {
		return escopoPai;
	}
	
	public void setEscopoPai(Escopo escopoPai) {
		this.escopoPai = escopoPai;
	}
	
	public ArrayList<Symbol> getSimbols() {
		return this.symbols;
	}
	
	public boolean equals(Object obj) {
		System.out.println("EQUALS ESCOPO CLASSE");
		EscopoClasse escopo = (EscopoClasse) obj;
		String name = escopo.name;
        if (name.equals(this.name))
        	return true;
        return false;
    }
}
