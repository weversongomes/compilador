package model;

import java.util.ArrayList;

public class Escopo {
	public String type;
	private ArrayList<Symbol> symbols;
	
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
	 * SOBRESCREVER NA CLASSE HERDEIRA
	 */
	public void showSimbols() {}
}
