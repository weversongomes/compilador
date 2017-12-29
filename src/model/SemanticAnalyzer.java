package model;

import java.util.regex.Pattern;

public class SemanticAnalyzer {

	
	public static String checkType(String simbolName, String value, Escopo escopo) {
		Symbol symbol = new Symbol();
		//symbol.name = simbolName;
		String[] varName = splitVectorVariable(simbolName);
		String[] varType = {};
		symbol.name = varName[0]; // mesmo que seja um vetor, a posicao zero eh o identificador
		if (escopo.getSimbols().contains(symbol)) {
			symbol = escopo.getSimbols().get(escopo.getSimbols().indexOf(symbol));
			varType = splitVectorVariable(symbol.type);
		} else {
			return "err1";
		}
		if (varType.length > 1 || varName.length > 1) { // caso seja um vetor, verifica se as regras semanticas foram aplicadas
			if (varName.length != varType.length) {
				System.out.println("As dimensoes do vetor na declaracao sao diferentes da atribuicao");
			} else { 
				String vector = checkVector(varName, varType);
				if (!vector.equals("ok")) { // caso o vetor nao esteja semanticamente correto, exibe a mensagem de erro
					System.out.println(vector);
				}
			}
		}
		if (isBool(value)) { // verifica se eh um bool atribuido corretamente 
			if (varType[0].equals("bool")) {
				return "ok";
			}
		} else if (isInt(value)) { // verifica se eh um int atribuido corretamente
			if (varType[0].equals("int")) {
				return "ok";
			}
		} else if (isFloat(value)) { // verifica se eh um float atribuido corretamente
			if (varType[0].equals("float")) {
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
	
	public static String checkVector(String[] varName, String[] varType) {
		for (int i = 1; i < varName.length; i++) {
			String varArrayPos = varName[i];
			varArrayPos = varArrayPos.substring(1, varArrayPos.length() - 1); // pega somente o indice posicao do vetor, elimina [ ]
			String typeArrayPos = varType[i];
			typeArrayPos = typeArrayPos.substring(1, typeArrayPos.length() - 1);
			if (!isInt(varArrayPos) || !isInt(typeArrayPos)) { // nao permite que o vetor tenha dimensao float, por ex: vetor[4.5]
				return "O vetor tem dimensoes improprias";
			} else {
				int dimensaoInicializacao = Integer.parseInt(varArrayPos);
				int dimensaoDeclaracao = Integer.parseInt(typeArrayPos);
				if (dimensaoDeclaracao < 1) { // nao permite que o vetor tenha dimensao < 1, por ex: vetor[-1]
					return "O vetor deve conter pelo menos 1 posicao";
				}
				if (dimensaoInicializacao >= dimensaoDeclaracao || dimensaoInicializacao < 0) { // verifica se o indice extrapolou
					return "Indice inexistente no vetor";
				}
			}
		}
		System.out.println("Vetor ou matriz ok");
		return "ok";
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
	
	public static String[] splitVectorVariable(String value) { // divide uma declaracao com vetor em tipo e dimensoes do vetor/matriz
		// para uma declaracao int[3][4], o resultado sera int, [3] e [4]
		return value.split("(?=" + Pattern.quote("[") + ")");
	}

}
