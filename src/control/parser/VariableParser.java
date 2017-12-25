package control.parser;

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
	public boolean recognizeVariableDeclaration() {
		boolean isFirstVariable = true;
		while (fileParser.tokensToRead() && !fileParser.getTokensList().get(fileParser.index).lexeme.equals(";")) {
			if (isFirstVariable) { // se for a primeira variavel, nao tem virgula antes
				if (fileParser.getTokensList().get(fileParser.index).type.equals("ID")) { // verifica se o nome da variavel eh valido
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
					fileParser.index = fileParser.index + 1;
				} else {
					return false;
				}
			}
		}
		return true;
	}
	
	// reconhece a a estrutura sintatica de inicializacao de uma variavel global, atributo ou local
	public boolean recognizeInitialization(boolean isConstant) {
		if (fileParser.getTokensList().get(fileParser.index).type.equals("ID")) {
			String simbol = fileParser.getTokensList().get(fileParser.index).lexeme;
			fileParser.eg.addSimbol(simbol);
			fileParser.index = fileParser.index + 1;
			if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).lexeme.equals("=")) {
				fileParser.index = fileParser.index + 1;
				if (isConstant) {
					String simbolValue = fileParser.getTokensList().get(fileParser.index).lexeme;
					fileParser.eg.setSimbolValue(simbol, simbolValue);
					fileParser.eg.showSimbols();
				}
				if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).type.equals("ID") || fileParser.getTokensList().get(fileParser.index).type.equals("NUM") || fileParser.getTokensList().get(fileParser.index).type.equals("STR") || 
						fileParser.getTokensList().get(fileParser.index).lexeme.equals("true") || fileParser.getTokensList().get(fileParser.index).lexeme.equals("false")) {
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
					if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index+1).lexeme.equals("[")) {
						fileParser.index = fileParser.index + 2;
						if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).type.equals("NUM")) {
							fileParser.index = fileParser.index + 1;
							if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).lexeme.equals("]")) {
								return true; // eh uma matriz (2 dimensoes)
							}
						}
					} else {
						return true; // eh um vetor (1 dimensao)
					}
				}
			}
		}
		return false;
	}

}
