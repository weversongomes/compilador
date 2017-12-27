package model;

import java.util.ArrayList;

public class Escopo {
	public String type;
	private ArrayList<Simbol> simbols;
	
	public Escopo() {
		simbols = new ArrayList<Simbol>();
	}
	
	/**
	 * SOBRESCREVER NA CLASSE HERDEIRA
	 */
	public int addSimbol(Simbol simbol) {
		return 0;
	}

	public ArrayList<Simbol> getSimbols() {
		return this.simbols;
	}

	/**
	 * SOBRESCREVER NA CLASSE HERDEIRA
	 */
	public void showSimbols() {}
}
