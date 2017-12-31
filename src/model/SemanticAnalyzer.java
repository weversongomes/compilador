package model;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class SemanticAnalyzer {

	
	public static String[] checkType(String simbolName, String value, Escopo escopo, boolean update, String type) {
		String[] returnMessages = {"", "ok"};
		Symbol symbol = new Symbol();
		//symbol.name = simbolName;
		String[] varName = splitVectorVariable(simbolName);
		String[] varType = {};
		symbol.name = varName[0]; // mesmo que seja um vetor, a posicao zero eh o identificador
		if (escopo.getSimbols().contains(symbol)) {
			symbol = escopo.getSimbols().get(escopo.getSimbols().indexOf(symbol));
			varType = splitVectorVariable(symbol.type);
			if (update) {
				escopo.setSimbolValue(varName[0], value);
			}
		} else {
			try {
				if (escopo.getEscopoPai().getSimbols().contains(symbol)) {
					symbol = escopo.getEscopoPai().getSimbols().get(escopo.getEscopoPai().getSimbols().indexOf(symbol));
					varType = splitVectorVariable(symbol.type);
					if (update) {
						escopo.getEscopoPai().setSimbolValue(varName[0], value);
					}
				} else {
					if (escopo.getEscopoPai().getEscopoPai().getSimbols().contains(symbol)) {
						symbol = escopo.getEscopoPai().getEscopoPai().getSimbols().get(escopo.getEscopoPai().getEscopoPai().getSimbols().indexOf(symbol));
						varType = splitVectorVariable(symbol.type);
						if (update) {
							escopo.getEscopoPai().getEscopoPai().setSimbolValue(varName[0], value);
						}
					} else {
						//return "err1";
						returnMessages[0] = "erro";
					}
				}
			} catch (Exception e) {
				//System.out.println("ESCOPO PAI NAO EXISTE");
				//return "err1";
				returnMessages[0] = "erro";
			}
		}
		if (varType.length > 1 || varName.length > 1) { // caso seja um vetor, verifica se as regras semanticas foram aplicadas
			if (varName.length != varType.length) {
				System.out.println();
				returnMessages[1] = "ERRO SEMANTICO: As dimensoes do vetor na declaracao sao diferentes da atribuicao";
			} else { 
				String vector = checkVector(varName, varType);
				returnMessages[1] = vector;
			}
		}
	    if (varType[0].equals("string")) { 
	        //return "string"; 
	        returnMessages[0] = "string";
	    } else if (isBool(value) || type.equals("bool")) { // verifica se eh um bool atribuido corretamente 
			if (varType[0].equals("bool")) {
				//return "ok";
				returnMessages[0] = "ok";
			}
		} else if (isInt(value) || type.equals("int")) { // verifica se eh um int atribuido corretamente
			if (varType[0].equals("int")) {
				//return "ok";
				returnMessages[0] = "ok";
			}
		} else if (isFloat(value) || type.equals("float")) { // verifica se eh um float atribuido corretamente
			if (varType[0].equals("float")) {
				//return "ok";
				returnMessages[0] = "ok";
			}
		} else if (isString(value) || type.equals("string")) { // verifica se eh uma string atribuido corretamente
			if (symbol.type.equals("string")) {
				//return "ok";
				returnMessages[0] = "ok";
			}
		}
		//return "err2";
	    return returnMessages;
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
	
	public static String checkVector(String[] varName, String[] varType) {
		for (int i = 1; i < varName.length; i++) {
			String varArrayPos = varName[i];
			varArrayPos = varArrayPos.substring(1, varArrayPos.length() - 1); // pega somente o indice posicao do vetor, elimina [ ]
			String typeArrayPos = varType[i];
			typeArrayPos = typeArrayPos.substring(1, typeArrayPos.length() - 1);
			if (!isInt(varArrayPos) || !isInt(typeArrayPos)) { // nao permite que o vetor tenha dimensao float, por ex: vetor[4.5]
				return "ERRO SEMANTICO: O vetor tem dimensoes improprias";
			} else {
				int dimensaoInicializacao = Integer.parseInt(varArrayPos);
				int dimensaoDeclaracao = Integer.parseInt(typeArrayPos);
				if (dimensaoDeclaracao < 1) { // nao permite que o vetor tenha dimensao < 1, por ex: vetor[-1]
					return "ERRO SEMANTICO: O vetor deve conter pelo menos 1 posicao";
				}
				if (dimensaoInicializacao >= dimensaoDeclaracao || dimensaoInicializacao < 0) { // verifica se o indice extrapolou
					return "ERRO SEMANTICO: Indice inexistente no vetor";
				}
			}
		}
		//System.out.println("Vetor ou matriz ok");
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
	
	public static String[] splitVectorVariable(String value) { // divide uma declaracao com vetor em tipo e dimensoes do vetor/matriz
		// para uma declaracao int[3][4], o resultado sera int, [3] e [4]
		return value.split("(?=" + Pattern.quote("[") + ")");
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
		return "ok";
	}
	
	public static String checkArithmeticOperation(ArrayList<String> operators, Escopo scope, int line) {
		String varType = ""; // tipo de variavel do primeiro identificador encontrado na operacao aritmetica
		for (int i = 0; i < operators.size(); i++) { // percorre os tokens de uma operacao aritmetica 
			if (isInt(operators.get(i)) || isFloat(operators.get(i))) { // se for um numero
				String numType = isInt(operators.get(i)) ? "int" : "float";
				if (varType.equals("")) { // se eh a primeira variavel da operacao aritmetica, nao ha tipo a ser comparado ainda
					varType = numType;
				} else {
					if (!numType.equals(varType)) { // compara com o tipo da primeira variavel da operacao aritmetica
						return "ERRO SEMANTICO: Variaveis com tipos diferentes na operacao aritmetica da linha " + line;
					}
				}
			} else { // se for um identificador
				Symbol s = new Symbol();
				s.name = operators.get(i);
				if (scope.getSimbols().contains(s)) { // verifica se existe a variavel
					s = scope.getSimbols().get(scope.getSimbols().indexOf(s));
					if (varType.equals("")) { // se eh a primeira variavel da operacao aritmetica, nao ha tipo a ser comparado ainda
						varType = s.type;
					} else {
						if (!s.type.equals(varType)) { // compara com o tipo da primeira variavel da operacao aritmetica
							return "ERRO SEMANTICO: Variaveis com tipos diferentes na operacao aritmetica da linha " + line;
						}
					}
				} else { // nao existe a variavel da operacao aritmetica
					return "ERRO SEMANTICO: Nao existe uma variavel da operacao aritmetica na linha " + line;
				}
			}
		}
		return "ok";
	}
	
	public static Symbol getSymbol(String simbolName, Escopo escopo) {
		Symbol symbol = new Symbol();
		symbol.name = simbolName;
		if (escopo.getSimbols().contains(symbol)) {
			symbol = escopo.getSimbols().get(escopo.getSimbols().indexOf(symbol));
		} else {
			try {
				if (escopo.getEscopoPai().getSimbols().contains(symbol)) {
					symbol = escopo.getEscopoPai().getSimbols().get(escopo.getEscopoPai().getSimbols().indexOf(symbol));
				} else {
					if (escopo.getEscopoPai().getEscopoPai().getSimbols().contains(symbol)) {
						symbol = escopo.getEscopoPai().getEscopoPai().getSimbols().get(escopo.getEscopoPai().getEscopoPai().getSimbols().indexOf(symbol));
					} else {
						return null;
					}
				}
			} catch (Exception e) {
				return null;
			}
		}
		return symbol;
	}
}
