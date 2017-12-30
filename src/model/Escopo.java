package model;

import java.util.ArrayList;

public class Escopo {
	public String type;
	private ArrayList<Symbol> symbols;
	public String name = "";
	Escopo escopoPai;
	
	public Escopo() {
		symbols = new ArrayList<Symbol>();
	}
	
	/**
	 * SOBRESCREVER NA CLASSE HERDEIRA
	 */
	public int addSimbol(Symbol symbol) {
		return 0;
	}

	public ArrayList<Symbol> getSimbols() {
		return this.symbols;
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

	/**
	 * SOBRESCREVER NA CLASSE HERDEIRA
	 */
	public void showSimbols() {}
	
	public boolean equals(Object obj) {
		Escopo escopo = (Escopo) obj;
		String name = escopo.name;
        if (name.equals(this.name))
        	return true;
        return false;
    }
}
