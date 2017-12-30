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
