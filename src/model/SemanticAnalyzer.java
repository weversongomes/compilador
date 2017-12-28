package model;

public class SemanticAnalyzer {

	
	public static String checkType(String simbolName, String value, Escopo escopo) {
		Symbol symbol = new Symbol();
		symbol.name = simbolName;
		if (escopo.getSimbols().contains(symbol)) {
			symbol = escopo.getSimbols().get(escopo.getSimbols().indexOf(symbol));
		} else {
			return "err1";
		}
		if (isBool(value)) { // verifica se eh um bool atribuido corretamente 
			if (symbol.type.equals("bool")) {
				return "ok";
			}
		} else if (isInt(value)) { // verifica se eh um int atribuido corretamente
			if (symbol.type.equals("int")) {
				return "ok";
			}
		} else if (isFloat(value)) { // verifica se eh um float atribuido corretamente
			if (symbol.type.equals("float")) {
				return "ok";
			}
		}
		
		return "err2";
	}
	
	public static String checkRelationalOperation(String[] values, Escopo scope) {	
		Symbol symbol1 = new Symbol(); // primeiro item de uma operacao relacional: pode ser um id do tipo string, bool, int, float ou objeto
		symbol1.name = values[0];
		if (scope.getSimbols().contains(symbol1)) {
			symbol1 = scope.getSimbols().get(scope.getSimbols().indexOf(symbol1));
		} else {
			return "err1";
		}
		
		String relOp = values[1]; // segundo item de uma operacao relacional: pode ser qualquer operador relacional

		Symbol symbol2 = new Symbol(); // terceiro item de uma operacao relacional: pode ser id do tipo string, bool, int, float ou objeto ou um valor literal (true ou false, por ex)
		symbol2.name = values[2];
		if (scope.getSimbols().contains(symbol2)) {
			symbol2 = scope.getSimbols().get(scope.getSimbols().indexOf(symbol2));
		} else { // caso nao seja um identificador, mas um valor literal
			if (isBool(values[2])) {
				symbol2.type = "bool";
			} else if (isInt(values[2])) {
				symbol2.type = "int";
			} else if (isFloat(values[2])) {
				symbol2.type = "float";
			} 
			// else if (isString(values[2])) { } 
			else {
				return "err1";
			}
		}
		
		if (relOp.equals("!=") || relOp.equals("=")) {
			if (symbol1.type.equals(symbol2.type)) {
				return "ok";
			}
		} else { // operacoes de <, <=, >= e > so podem ser feitas com int ou float
			if (symbol1.type.equals(symbol2.type) && (symbol1.type.equals("int") || symbol1.type.equals("float"))) {
				return "ok";
			}
		}
		
		return "err3";
	}
	
	private static boolean isBool(String value) {
		return value.equals("true") || value.equals("false");
	}
	
	private static boolean isInt(String value) {
		return value.matches("^-?\\d+$");
	}
	
	private static boolean isFloat(String value) {
		return value.matches("^-?\\d*\\.?\\d*");
	}
	
	private static boolean isString(String value) { // eh necessario verificar se a string esta formada corretamente, com as aspas corretas, por ex 
		return true;
	}
}
