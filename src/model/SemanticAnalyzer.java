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
	
	public static String checkMainMethod(String value, Escopo globalScope, Escopo classScope) {
		Symbol main = new Symbol();
		main.name = value;
		for (Symbol s : globalScope.getSimbols()) {
			if (s.type.equals("class")) { // verifica em todas as classes se ja tem um metodo main
				// como obter cada EscopoClasse a partir do Symbol?
			}
		}
		
		return "ok";
	}
	
	public static boolean isBool(String value) {
		return value.equals("true") || value.equals("false");
	}
	
	public static boolean isInt(String value) {
		return value.matches("^-?\\d+$");
	}
	
	public static boolean isFloat(String value) {
		return value.matches("^-?\\d*\\.?\\d*");
	}
	
	public static boolean isString(String value) { // eh necessario verificar se a string esta formada corretamente, com as aspas corretas, por ex 
		return true;
	}
	
	public static String validateMethodParams(String pParam, EscopoMetodo meuEscopo, EscopoMetodo outroEscopo, int indexParam, int line) {
		try {
			String methodParam = outroEscopo.getParams().get(indexParam);
			Symbol symbol = new Symbol();
			symbol.name = methodParam;
			String methodParamType = outroEscopo.getSimbols().get(outroEscopo.getSimbols().indexOf(symbol)).type;
			if (methodParamType.equals("bool")) {
				Symbol auxsymbol = new Symbol();
				auxsymbol.name = pParam;
				String paramType;
				if (meuEscopo.getSimbols().indexOf(auxsymbol) == -1) {//verifica no escopo de metodo
					if (outroEscopo.escopoPai.getSimbols().indexOf(auxsymbol) == -1) {////verifica no escopo de classe
						if (outroEscopo.escopoPai.escopoPai.getSimbols().indexOf(auxsymbol) == -1) {//verifica no escopo global
							return "ERRO SEMANTICO: O parametro passado na linha " + line + " nao foi declarado";
						} else {
							paramType = outroEscopo.escopoPai.escopoPai.getSimbols().get(outroEscopo.escopoPai.escopoPai.getSimbols().indexOf(auxsymbol)).type;
						}
					} else {
						paramType = outroEscopo.escopoPai.getSimbols().get(outroEscopo.escopoPai.getSimbols().indexOf(auxsymbol)).type;
					}
				} else {
					paramType = meuEscopo.getSimbols().get(meuEscopo.getSimbols().indexOf(auxsymbol)).type;
				}
				
				if (!SemanticAnalyzer.isBool(pParam) && !paramType.equals("bool")) {
					return "ERRO SEMANTICO: O parametro passado nao e' do tipo bool na linha " + line;
				}
			} else if (methodParamType.equals("int")) {
				Symbol auxsymbol = new Symbol();
				auxsymbol.name = pParam;
				String paramType;
				if (meuEscopo.getSimbols().indexOf(auxsymbol) == -1) {//verifica no escopo de metodo
					if (outroEscopo.escopoPai.getSimbols().indexOf(auxsymbol) == -1) {////verifica no escopo de classe
						if (outroEscopo.escopoPai.escopoPai.getSimbols().indexOf(auxsymbol) == -1) {//verifica no escopo global
							return "ERRO SEMANTICO: O parametro passado na linha " + line + " nao foi declarado";
						} else {
							paramType = outroEscopo.escopoPai.escopoPai.getSimbols().get(outroEscopo.escopoPai.escopoPai.getSimbols().indexOf(auxsymbol)).type;
						}
					} else {
						paramType = outroEscopo.escopoPai.getSimbols().get(outroEscopo.escopoPai.getSimbols().indexOf(auxsymbol)).type;
					}
				} else {
					paramType = meuEscopo.getSimbols().get(meuEscopo.getSimbols().indexOf(auxsymbol)).type;
				}
				
				if (!SemanticAnalyzer.isInt(pParam) && !paramType.equals("int")) {
					return "ERRO SEMANTICO: O parametro passado nao e' do tipo int na linha " + line;
				}
			} else if (methodParamType.equals("float")) {
				Symbol auxsymbol = new Symbol();
				auxsymbol.name = pParam;
				String paramType;
				if (meuEscopo.getSimbols().indexOf(auxsymbol) == -1) {//verifica no escopo de metodo
					if (outroEscopo.escopoPai.getSimbols().indexOf(auxsymbol) == -1) {////verifica no escopo de classe
						if (outroEscopo.escopoPai.escopoPai.getSimbols().indexOf(auxsymbol) == -1) {//verifica no escopo global
							return "ERRO SEMANTICO: O parametro passado na linha " + line + " nao foi declarado";
						} else {
							paramType = outroEscopo.escopoPai.escopoPai.getSimbols().get(outroEscopo.escopoPai.escopoPai.getSimbols().indexOf(auxsymbol)).type;
						}
					} else {
						paramType = outroEscopo.escopoPai.getSimbols().get(outroEscopo.escopoPai.getSimbols().indexOf(auxsymbol)).type;
					}
				} else {
					paramType = meuEscopo.getSimbols().get(meuEscopo.getSimbols().indexOf(auxsymbol)).type;
				}
				
				if (!SemanticAnalyzer.isFloat(pParam) && !paramType.equals("float")) {
					return "ERRO SEMANTICO: O parametro passado nao e' do tipo float na linha " + line;
				}
			} else if (methodParamType.equals("string")) {
				Symbol auxsymbol = new Symbol();
				auxsymbol.name = pParam;
				String paramType;
				if (meuEscopo.getSimbols().indexOf(auxsymbol) == -1) {//verifica no escopo de metodo
					if (outroEscopo.escopoPai.getSimbols().indexOf(auxsymbol) == -1) {////verifica no escopo de classe
						if (outroEscopo.escopoPai.escopoPai.getSimbols().indexOf(auxsymbol) == -1) {//verifica no escopo global
							return "ERRO SEMANTICO: O parametro passado na linha " + line + " nao foi declarado";
						} else {
							paramType = outroEscopo.escopoPai.escopoPai.getSimbols().get(outroEscopo.escopoPai.escopoPai.getSimbols().indexOf(auxsymbol)).type;
						}
					} else {
						paramType = outroEscopo.escopoPai.getSimbols().get(outroEscopo.escopoPai.getSimbols().indexOf(auxsymbol)).type;
					}
				} else {
					paramType = meuEscopo.getSimbols().get(meuEscopo.getSimbols().indexOf(auxsymbol)).type;
				}
				
				if (!SemanticAnalyzer.isString(pParam) && !paramType.equals("string")) {
					return "ERRO SEMANTICO: O parametro passado nao e' do tipo string na linha " + line;
				}
			}
		} catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return "ERRO SEMANTICO: Chamada imcompativel de metodo na linha " + line;
		}
		return "Chamada de metodo semanticamente correta na linha " + line;
	}
}
