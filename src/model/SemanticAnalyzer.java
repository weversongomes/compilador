package model;

public class SemanticAnalyzer {

	
	public static String verificarTipos(String simbolName, String value, Escopo escopo) {
		Simbol simbol = new Simbol();
		simbol.name = simbolName;
		if (escopo.getSimbols().contains(simbol)) {
			simbol = escopo.getSimbols().get(escopo.getSimbols().indexOf(simbol));
		} else {
			return "err1";
		}
		if (value.equals("true") || value.equals("false")) {
			if (simbol.type.equals("bool")) {
				return "ok";
			}
		}
		return "err2";
	}
}
