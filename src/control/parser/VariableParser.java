package control.parser;

import model.Escopo;
import model.Symbol;

/**
 * 
 * Classe responsavel por reconhecer declaracao e inicializacao de variaveis
 *
 */
public class VariableParser {
	
	private FileParser fileParser;
	
	public VariableParser(FileParser parser) {
		this.fileParser = parser;
	}
	
	// reconhece a estrutura sintatica de declaracao de uma variavel global, atributo ou local
	public boolean recognizeVariableDeclaration(String varType, Escopo escopo, boolean isConstant) {
		boolean isFirstVariable = true;
		while (fileParser.tokensToRead() && !fileParser.getTokensList().get(fileParser.index).lexeme.equals(";")) {
			if (isFirstVariable) { // se for a primeira variavel, nao tem virgula antes
				if (fileParser.getTokensList().get(fileParser.index).type.equals("ID")) { // verifica se o nome da variavel eh valido
					Symbol symbol = new Symbol();
					symbol.name = fileParser.getTokensList().get(fileParser.index).lexeme;
					symbol.type = varType;
					symbol.isConstant = isConstant;
					if (escopo.addSimbol(symbol) == 0) {
						System.out.println("ERRO SEMANTICO: Identificador duplicado na linha " + fileParser.getTokensList().get(fileParser.index).line);
					}
					fileParser.index = fileParser.index + 1;
					isFirstVariable = false;
				} else {
					return false;
				}
			} else {
				if (fileParser.getTokensList().get(fileParser.index).lexeme.equals(",")) {
					fileParser.index = fileParser.index + 1;
				} else {
					return false;
				}
				if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).type.equals("ID")) { // verifica se o nome da variavel eh valido
					Symbol symbol = new Symbol();
					symbol.name = fileParser.getTokensList().get(fileParser.index).lexeme;
					symbol.type = varType;
					symbol.isConstant = isConstant;
					escopo.addSimbol(symbol);
					fileParser.index = fileParser.index + 1;
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	// reconhece a a estrutura sintatica de inicializacao de uma variavel global, atributo ou local
	public boolean recognizeInitialization(boolean isConstant, String varType, Escopo escopo) {
		if (fileParser.getTokensList().get(fileParser.index).type.equals("ID")) {
			String simbolName = fileParser.getTokensList().get(fileParser.index).lexeme;
			fileParser.index = fileParser.index + 1;
			if (recognizeVector() && !isConstant) {
				fileParser.index = fileParser.index + 1;
			}
			if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).lexeme.equals("=")) {
				fileParser.index = fileParser.index + 1;
				if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).type.equals("ID") || fileParser.getTokensList().get(fileParser.index).type.equals("NUM") || fileParser.getTokensList().get(fileParser.index).type.equals("STR") || 
						fileParser.getTokensList().get(fileParser.index).lexeme.equals("true") || fileParser.getTokensList().get(fileParser.index).lexeme.equals("false")) {
					if (isConstant) {
						Symbol symbol = new Symbol();
						symbol.name = simbolName;
						symbol.type = varType;
						symbol.isConstant = true;
						symbol.value = fileParser.getTokensList().get(fileParser.index).lexeme;
						escopo.addSimbol(symbol);
						//String simbolValue = fileParser.getTokensList().get(fileParser.index).lexeme;
						//fileParser.eg.setSimbolValue(simbolName, simbolValue);
					}
					return true;
				}
			}
		}
		return false;
	}
	
	// reconhece a estrutura sintatica de um vetor
	public boolean recognizeVector() {
		if (fileParser.getTokensList().get(fileParser.index).lexeme.equals("[")) {
			fileParser.index = fileParser.index + 1;
			if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).type.equals("NUM")) {
				fileParser.index = fileParser.index + 1;
				if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).lexeme.equals("]")) {
/*					if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index+1).lexeme.equals("[")) {
						fileParser.index = fileParser.index + 2;
						if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).type.equals("NUM")) {
							fileParser.index = fileParser.index + 1;
							if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).lexeme.equals("]")) {
								return true; // eh uma matriz (2 dimensoes)
							}
						}
					} else {
						return true; // eh um vetor (1 dimensao)
					}*/
					if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index+1).lexeme.equals("[")) {
						fileParser.index = fileParser.index + 1;
						return recognizeVector();
					} else {
						return true;
					}
				}
			}
		}
		return false;
	}

}
