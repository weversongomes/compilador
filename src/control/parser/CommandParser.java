package control.parser;

import model.Escopo;
import model.EscopoClasse;
import model.EscopoMetodo;
import model.SemanticAnalyzer;
import model.Symbol;

/**
 * 
 * Classe responsavel por reconhecer os comandos da gramatica, como if-else, for, print, scan e acesso
 *
 */
public class CommandParser {
	
	private FileParser parser;
	private String[] forStructure = {"for", "(", "<initialization>", ";", "<condition>", ";", "<increment>", ")", "{", "<commands>", "}"};
	private String[] ifStructure = {"if", "(", "<condition>", ")", "{", "<commands>", "}"};
	private String[] elseStructure = {"else", "{", "<commands>", "}"};
	private String[] printStructure = {"print", "(", "<content>", ")", ";"};
	private String[] scanStructure = {"scan", "(", "<content>", ")", ";"};
	EscopoMetodo escopo;
	
	public CommandParser(FileParser parser, EscopoMetodo escopo) {
		this.parser = parser;
		this.escopo = escopo;
	}
	
	// reconhece a estrutura sintatica de um comando, tal como if-else, for, print, scan e acesso
	public boolean recognizeCommand() {
		if (parser.getTokensList().get(parser.index).lexeme.equals("for")) { // comando for
			if (!recognizeTokenFor()) {
				panicModeFor();
			} else {
				//System.out.println("For correto na linha " + parser.getTokensList().get(parser.index).line);
				return true;
			}
		} else if (parser.getTokensList().get(parser.index).lexeme.equals("if")) { // comando if
			if (!recognizeTokenIf()) {
				panicModeIf();
			} else {
				//System.out.println("If correto na linha " + parser.getTokensList().get(parser.index).line);
				return true;
			}
		} else if (parser.getTokensList().get(parser.index).lexeme.equals("print")) { // comando print
			if (!recognizeTokenPrint()) {
				panicModePrint();
			} else {
				//System.out.println("Print correto na linha " + parser.getTokensList().get(parser.index).line);
				return true;
			}
		} else if (parser.getTokensList().get(parser.index).lexeme.equals("scan")) { // comando scan
			if (!recognizeTokenScan()) {
				panicModeScan();
			} else {
				//System.out.println("Scan correto na linha " + parser.getTokensList().get(parser.index).line);
				return true;
			}
		} else if (parser.isAttributeType() || parser.getTokensList().get(parser.index).type.equals("ID")) { // inicializacao
			String varType = parser.getTokensList().get(parser.index).lexeme;
			String attrVar = varType;
			parser.index = parser.index + 1;
			if (parser.tokensToRead() && new VariableParser(parser).recognizeVector()) { // verifica se eh vetor ou matriz
				parser.index = parser.index + 1;
			}
			if (parser.tokensToRead() && parser.getTokensList().get(parser.index).lexeme.equals("=")) { // inicializacao de variavel local
				String auxVar = ""; 
				while (!parser.getTokensList().get(parser.index).type.equals("ID")) {
					parser.index = parser.index - 1; // para comecar a varredura de inicializacao de variavel pelo id
					auxVar = parser.getTokensList().get(parser.index).lexeme + auxVar; // caso seja vetor ou matriz, adiciona as dimensoes ao id: ID [NUM][NUM]
				}
				attrVar = auxVar;
				if (!new VariableParser(parser).recognizeInitialization(false, varType, escopo)) { // verifica se a atribuicao esta correta
					panicModeLocalVariableInitialization();
				} else {
					parser.index = parser.index + 1;
					if (parser.tokensToRead() && parser.getTokensList().get(parser.index).lexeme.equals(";")) { // inicializacao
						//System.out.println("Atribuicao de variavel local correta na linha " + parser.getTokensList().get(parser.index).line);
						String[] checkType = SemanticAnalyzer.checkType(attrVar, parser.getTokensList().get(parser.index-1).lexeme, escopo, true, "");
						if (checkType[0].equals("ok")) {
							//System.out.println("tipo compativel na linha " + parser.getTokensList().get(parser.index).line);
						} else if (checkType[0].equals("string")) {
							if (parser.getTokensList().get(parser.index-1).type.equals("STR")) {
								//System.out.println("string compativel na linha " + parser.getTokensList().get(parser.index).line);
							} else {
								parser.addSemanticError("ERRO SEMANTICO: Tipo incompativel ou inexistente na linha " + parser.getTokensList().get(parser.index-1).line);
							}
						} else {
							parser.addSemanticError("ERRO SEMANTICO: Tipo incompativel ou inexistente na linha " + parser.getTokensList().get(parser.index-1).line);
						}
						
						if (!checkType[1].equals("ok")) {
							parser.addSemanticError(checkType[1] + " na linha " + parser.getTokensList().get(parser.index-1).line);
						}
						return true;
					} else if (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("ARIOP")) { // inicializacao com operacao aritmetica
						parser.index = parser.index + 1;
						if (parser.tokensToRead() && new OperationParser(parser, escopo).recognizeArithmeticOperation(parser.getTokensList().get(parser.index - 4).lexeme, parser.getTokensList().get(parser.index - 2).lexeme)) {
							//System.out.println("Inicializacao de variavel local com operacao aritmetica correta na linha " + parser.getTokensList().get(parser.index).line);
							return true;
						} else {
							panicModeLocalVariableInitialization();
						}
					} else {
						panicModeLocalVariableInitialization();
					}
				}
			} else if (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("ARIOP")) { // operacao aritmetica
				parser.index = parser.index + 1;
				if (parser.tokensToRead() && new OperationParser(parser, escopo).recognizeArithmeticOperation("", "")) {
					//System.out.println("Operacao aritmetica correta na linha " + parser.getTokensList().get(parser.index).line);
					return true;
				} else {
					new OperationParser(parser, escopo).panicModeArithmeticOperation();
				}
			
			} else if (parser.tokensToRead() && parser.getTokensList().get(parser.index).lexeme.equals(":")) { // acesso a metodos ou atributos de objetos
				if (recognizeAccess()) {
					//System.out.println("Acesso correto na linha " + parser.getTokensList().get(parser.index).line);
					return true;
				} else {
					panicModeAccess();
				} 	
			} else if (parser.tokensToRead() && parser.getTokensList().get(parser.index).lexeme.equals("(")) { // chamada a outro metodo da classe
				if (recognizeMethodCall(attrVar)) {
					//System.out.println("Chamada a metodo correta na linha " + parser.getTokensList().get(parser.index).line);
					return true;
				} else {
					panicModeMethodCall();
				}
			} else { // declaracao de variavel local
				if (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("ID")) {
					varType = parser.getTokensList().get(parser.index - 1).lexeme;
					varType = parser.isVector(varType); // caso seja um vetor ou matriz, o tipo sera <TIPO>[NUM]*
					parser.index = parser.index + 1;
					if (parser.tokensToRead() && (parser.getTokensList().get(parser.index).lexeme.equals(";") || parser.getTokensList().get(parser.index).lexeme.equals(","))) {
						parser.index = parser.index - 1; // para comecar a varredura da estrutura de declaracao de variavel a partir do id
						if (!new VariableParser(parser).recognizeVariableDeclaration(varType, escopo, false)) {
							panicModeLocalVariableDeclaration();
						} else {
							//System.out.println("Declaracao de variavel local correta na linha " + parser.getTokensList().get(parser.index).line);
							return true;
						}
					} else {
						panicModeLocalVariableDeclaration();
					}
				}
			}
		} 
		return false;
	}
	
	// reconhece a estrutura sintatica do for
	public boolean recognizeTokenFor() {
		boolean isCorrect = true;
		int forIndex = 0;
		while (forIndex < forStructure.length) {
			if (parser.tokensToRead()) {
				if (forIndex == 2) { // verifica se a inicializacao do for esta correta
					if (!new VariableParser(parser).recognizeInitialization(false, null, escopo)) {
						isCorrect = false;
					}
					forIndex++;
					parser.index = parser.index + 1;
				} else if (forIndex == 4) { // verifica se a condicao do for esta correta
					if (!new OperationParser(parser, escopo).recognizeRelationalOperation()) {
						isCorrect = false;
					}
					forIndex++;
					parser.index = parser.index + 1;
				} else if (forIndex == 6) { // verifica se o incremento do for esta correto
					if (!recognizeIncrement()) {
						isCorrect = false;
					}
					forIndex++;
					/*parser.index = parser.index + 1;*/
				} else if (forIndex == 9) { // verifica se os comandos estao corretos
					if (parser.getTokensList().get(parser.index).lexeme.equals("}")) { // se for um um "}" eh porque nao ha nenhum comando dentro do for
						forIndex++;
					} else {
						while (parser.tokensToRead() && recognizeCommand()) { // enquanto houver comandos validos dentro do for
							parser.index = parser.index + 1;							
						}
						if (parser.tokensToRead() && (!parser.getTokensList().get(parser.index).lexeme.equals("}"))) { // nao ha mais comandos dentro do for, achou o "}"
							parser.index = parser.index + 1;
						}
						forIndex++;
					}
				} else { // verifica se os demais tokens estao corretos
					if (!parser.getTokensList().get(parser.index).lexeme.equals(forStructure[forIndex])) {
						isCorrect = false;
					}
					if (forIndex < 10) { // se nao for o ultimo token, avanca o indice
						parser.index = parser.index + 1;
					}
					forIndex++;
				}
			}
		}
		return isCorrect;	
	}
	
	// reconhece a estrutura sintatica de incremento do for
	public boolean recognizeIncrement() {
		if (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("ID")) {
			parser.index = parser.index + 1;
			if (parser.tokensToRead() && parser.getTokensList().get(parser.index).lexeme.equals("=")) {
				parser.index = parser.index + 1;
				if (parser.tokensToRead() && new OperationParser(parser, escopo).recognizeArithmeticOperation(parser.getTokensList().get(parser.index - 2).lexeme ,"")) {
					return true;
				}
			}
		}
		return false;
	}
	
	// reconhece a estrutura sintatica do if
	public boolean recognizeTokenIf() {
		boolean isCorrect = true;
		int ifIndex = 0;
		while (ifIndex < ifStructure.length) {
			if (parser.tokensToRead()) {
				if (ifIndex == 2) { // verifica se a condicao do if esta correta
					if (!new OperationParser(parser, escopo).recognizeRelationalOperation()) {
						isCorrect = false;
					}
					ifIndex++;
					parser.index = parser.index + 1;
				} else if (ifIndex == 5) { // verifica se os comandos estao corretos
					if (parser.getTokensList().get(parser.index).lexeme.equals("}")) { // se for um um "}" eh porque nao ha nenhum comando dentro do if
						ifIndex++;
					} else {
						while (parser.tokensToRead() && recognizeCommand()) { // enquanto houver comandos validos dentro do if
							parser.index = parser.index + 1;							
						}
						if (parser.tokensToRead() && (!parser.getTokensList().get(parser.index).lexeme.equals("}"))) { // nao ha mais comandos dentro do if, achou o "}"
							parser.index = parser.index + 1;
						}
						ifIndex++;
					}
				} else { // verifica se os demais tokens estao corretos
					if (!parser.getTokensList().get(parser.index).lexeme.equals(ifStructure[ifIndex])) {
						isCorrect = false;
					}
					if (ifIndex < 6) { // se nao for o ultimo token, avanca o indice
						parser.index = parser.index + 1;
					} else {
						if (parser.getTokensList().get(parser.index + 1).lexeme.equals("else")) { // caso seja um if-else
							parser.index = parser.index + 1;
							if (!recognizeTokenElse()) {
								isCorrect = false;
							}
						}
					}
					ifIndex++;
				}
			}	
		}
		return isCorrect;
	}
	
	// reconhece a estrutura sintatica do else
	public boolean recognizeTokenElse() {
		boolean isCorrect = true;
		int elseIndex = 0;
		while (elseIndex < elseStructure.length) {
			if (parser.tokensToRead()) {
				if (elseIndex == 2) { // verifica se os comandos estao corretos
					if (parser.getTokensList().get(parser.index).lexeme.equals("}")) { // se for um um "}" eh porque nao ha nenhum comando dentro do else
						elseIndex++;
					} else {
						while (parser.tokensToRead() && recognizeCommand()) { // enquanto houver comandos validos dentro do else
							parser.index = parser.index + 1;							
						}
						if (parser.tokensToRead() && (!parser.getTokensList().get(parser.index).lexeme.equals("}"))) { // nao ha mais comandos dentro do else, achou o "}"
							parser.index = parser.index + 1;
						}
						elseIndex++;
					}
				} else { // verifica se os demais tokens estao corretos
					if (!parser.getTokensList().get(parser.index).lexeme.equals(elseStructure[elseIndex])) {
						isCorrect = false;
					}
					if (elseIndex < 3) { // se nao for o ultimo token, avanca o indice
						parser.index = parser.index + 1;
					} 
					elseIndex++;
				}
			}
		}
		return isCorrect;
	}
	
	// reconhece a estrutura sintatica do print
	public boolean recognizeTokenPrint() {
		boolean isCorrect = true;
		int printIndex = 0;
		while (printIndex < printStructure.length) {
			if (parser.tokensToRead()) {
				if (printIndex == 2) { // verifica se o conteudo dentro do print esta correto
					if (!recognizePrintContent()) {
						isCorrect = false;
					}
					printIndex++;
					parser.index = parser.index + 1;
				} else { // verifica se os demais tokens estao corretos
					if (!parser.getTokensList().get(parser.index).lexeme.equals(printStructure[printIndex])) {
						isCorrect = false;
					}
					if (printIndex < 4) { // se nao for o ultimo token, avanca o indice
						parser.index = parser.index + 1;
					}
					printIndex++;
				}
			}
		}
		return isCorrect;
	}
	
	// reconhece o conteudo impresso na estrutura print
	public boolean recognizePrintContent() {
		boolean isFirstContent = true;
		while (parser.tokensToRead() && !parser.getTokensList().get(parser.index).lexeme.equals(")")) {
			if (isFirstContent) { // se for a primeira escrita, nao tem virgula antes
				if (parser.getTokensList().get(parser.index).type.equals("STR") || parser.getTokensList().get(parser.index).type.equals("ID")) {
					isFirstContent = false; // se tiver proxima escrita, nao sera mais a primeira
					parser.index = parser.index + 1;
				} else {
					return false;
				}
			} else {
				if (parser.getTokensList().get(parser.index).lexeme.equals(",")) { // verificar se as impressoes estao separadas por virgula
					parser.index = parser.index + 1;
				} else {
					return false;
				}
				if (parser.getTokensList().get(parser.index).type.equals("STR") || parser.getTokensList().get(parser.index).type.equals("ID")) {
					parser.index = parser.index + 1;
				} else {
					return false;
				}
			}
		}
		parser.index = parser.index - 1; // achou o ")", entao a leitura continua a partir dele
		return true;
	}
	
	// reconhece a estrutura sintatica do scan
	public boolean recognizeTokenScan() {
		boolean isCorrect = true;
		int scanIndex = 0;
		while (scanIndex < scanStructure.length) {
			if (parser.tokensToRead()) {
				if (scanIndex == 2) { // verifica se o conteudo dentro do scan esta correto
					if (!recognizeScanContent()) {
						isCorrect = false;
					}
					scanIndex++;
					parser.index = parser.index + 1;
				} else { // verifica se os demais tokens estao corretos
					if (!parser.getTokensList().get(parser.index).lexeme.equals(scanStructure[scanIndex])) {
						isCorrect = false;
					}
					if (scanIndex < 4) { // se nao for o ultimo token, avanca o indice
						parser.index = parser.index + 1;
					}
					scanIndex++;
				}
			}
		}
		return isCorrect;
	}
	
	// reconhece o conteudo a ser lido no scan
	public boolean recognizeScanContent() {
		boolean isFirstVariable = true;
		while (parser.tokensToRead() && !parser.getTokensList().get(parser.index).lexeme.equals(")")) {
			if (isFirstVariable) { // se for a primeira leitura, nao tem virgula antes
				if (parser.getTokensList().get(parser.index).type.equals("ID")) {
					isFirstVariable = false; // se tiver proxima leitura, nao sera mais a primeira
					parser.index = parser.index + 1;
				} else {
					return false;
				}
			} else {
				if (parser.getTokensList().get(parser.index).lexeme.equals(",")) { // verificar se as leituras estao separadas por virgula
					parser.index = parser.index + 1;
				} else {
					return false;
				}
				if (parser.tokensToRead() && (parser.getTokensList().get(parser.index).type.equals("ID"))) {
					parser.index = parser.index + 1;
				} else {
					return false;
				}
			}
		}
		parser.index = parser.index - 1; // achou o ")", entao a leitura continua a partir dele
		return true;
	}
	
	// reconhece o conteudo a ser lido nos parametros do metodo
	public boolean recognizeParamContent(String methodName) {
		boolean isFirstVariable = true;
		String param;
		int indexParam = 0;
		while (parser.tokensToRead() && !parser.getTokensList().get(parser.index).lexeme.equals(")")) {
			if (isFirstVariable) { // se for a primeira leitura, nao tem virgula antes
				if (parser.getTokensList().get(parser.index).type.equals("ID")) {
					//--------Semantico---------
					param = parser.getTokensList().get(parser.index).lexeme;
					EscopoMetodo escopoMetodoReferenciado = new EscopoMetodo();
					escopoMetodoReferenciado.name = methodName;
					escopoMetodoReferenciado = (EscopoMetodo) parser.escopos.get(parser.escopos.indexOf(escopoMetodoReferenciado));
					String message = SemanticAnalyzer.validateMethodParams(param, escopo, escopoMetodoReferenciado, indexParam, parser.getTokensList().get(parser.index).line);
					if (!message.equals("ok")) {
						parser.addSemanticError(message);
					}
					indexParam++;
					//--------------------------
					isFirstVariable = false; // se tiver proxima leitura, nao sera mais a primeira
					parser.index = parser.index + 1;
				} else {
					return false;
				}
			} else {
				if (parser.getTokensList().get(parser.index).lexeme.equals(",")) { // verificar se as leituras estao separadas por virgula
					parser.index = parser.index + 1;
				} else {
					return false;
				}
				if (parser.tokensToRead() && (parser.getTokensList().get(parser.index).type.equals("ID"))) {
					//--------Semantico---------
					param = parser.getTokensList().get(parser.index).lexeme;
					EscopoMetodo escopoMetodoReferenciado = new EscopoMetodo();
					escopoMetodoReferenciado.name = methodName;
					escopoMetodoReferenciado = (EscopoMetodo) parser.escopos.get(parser.escopos.indexOf(escopoMetodoReferenciado));
					String message = SemanticAnalyzer.validateMethodParams(param, escopo, escopoMetodoReferenciado, indexParam, parser.getTokensList().get(parser.index).line);
					if (!message.equals("ok")) {
						parser.addSemanticError(message);
					}
					indexParam++;
					//--------------------------
					parser.index = parser.index + 1;
				} else {
					return false;
				}
			}
		}
		parser.index = parser.index - 1; // achou o ")", entao a leitura continua a partir dele
		return true;
	}
	
	// reconhece a chamada a metodos dentro de outro metodo
	public boolean recognizeMethodCall(String methodName) {
		if (parser.tokensToRead() && parser.getTokensList().get(parser.index).lexeme.equals("(")) {
			parser.index = parser.index + 1;
			if (parser.tokensToRead() && recognizeParamContent(methodName)) {
				parser.index = parser.index + 1;
				if (parser.tokensToRead() && parser.getTokensList().get(parser.index).lexeme.equals(")")) {
					parser.index = parser.index + 1;
					if (parser.tokensToRead() && parser.getTokensList().get(parser.index).lexeme.equals(";")) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// reconhece o acesso a atributos e metodos de outras classes
	public boolean recognizeAccess() {
		String objName = parser.getTokensList().get(parser.index - 1).lexeme;
		Symbol symbol = SemanticAnalyzer.getSymbol(objName, escopo);
		if (symbol == null) {
			parser.addSemanticError("ERRO SEMANTICO: Acesso a objeto nao declarado na linha " + parser.getTokensList().get(parser.index - 1).line);
		}
		while (parser.tokensToRead() && parser.getTokensList().get(parser.index).lexeme.equals(":")) {
			parser.index = parser.index + 1;
			if (!recognizeObjectAccess(symbol)) {
				return false;
			}
		}
		return true;
	}
	
	// reconhece o conteudo de acesso a atributos e metodos de objetos
	public boolean recognizeObjectAccess(Symbol symbol) {
		if (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("ID")) {
			try {
				Symbol symbolClass = SemanticAnalyzer.getSymbol(symbol.type, parser.eg);
				EscopoClasse auxEscopo1 = null;
				for (Escopo auxEscopo2 : parser.escopos) {
					if (auxEscopo2.name.equals(symbolClass.name)) {
						auxEscopo1 = (EscopoClasse) auxEscopo2;
						break;
					}
				}
				Symbol auxSymbol = new Symbol();
				auxSymbol.name = parser.getTokensList().get(parser.index).lexeme;
				if (!auxEscopo1.getSimbols().contains(auxSymbol)) {
					parser.addSemanticError("ERRO SEMANTICO: Acesso a atributo ou metodo inexistente na linha " + parser.getTokensList().get(parser.index).line);
				}
			} catch (Exception e) {
				parser.addSemanticError("ERRO SEMANTICO: Acesso a objeto de tipo desconhecido na linha " + parser.getTokensList().get(parser.index).line);
				//e.printStackTrace();
			}
			
			parser.index = parser.index + 1;
			if (parser.tokensToRead() && parser.getTokensList().get(parser.index).lexeme.equals("(")) {
				parser.index = parser.index + 1;
				if (parser.tokensToRead() && recognizeScanContent()) {
					parser.index = parser.index + 1;
					if (parser.tokensToRead() && parser.getTokensList().get(parser.index).lexeme.equals(")")) {
						parser.index = parser.index + 1;
						return true;
					}
				}
			} else if (parser.tokensToRead() && (parser.getTokensList().get(parser.index).lexeme.equals(";") || parser.getTokensList().get(parser.index).lexeme.equals(":"))) {
				return true;
			}
		}
		return false;
	}
	
	public void panicModeLocalVariableDeclaration() {
		parser.addError("ERRO: Declaracao de variavel local mal formada na linha " + parser.getTokensList().get(parser.index - 1).line);
	}
	
	public void panicModeLocalVariableInitialization() {
		parser.addError("ERRO: Inicializacao de variavel local mal formada na linha " + parser.getTokensList().get(parser.index - 1).line);
	}
	
	public void panicModeFor() {
		parser.addError("ERRO: Estrutura 'for' mal formada na linha " + parser.getTokensList().get(parser.index - 1).line);
	}
	
	public void panicModeIf() {
		parser.addError("ERRO: Estrutura 'if' mal formada na linha " + parser.getTokensList().get(parser.index - 1).line);
	}
	
	public void panicModePrint() {
		parser.addError("ERRO: Estrutura 'print' mal formada na linha " + parser.getTokensList().get(parser.index - 1).line);
	}
	
	public void panicModeScan() {
		parser.addError("ERRO: Estrutura 'scan' mal formada na linha " + parser.getTokensList().get(parser.index - 1).line);
	}

	public void panicModeMethodCall() {
		parser.addError("ERRO: Chamada a metodo mal feita na linha " + parser.getTokensList().get(parser.index - 1).line);
	}
	
	public void panicModeAccess() {
		parser.addError("ERRO: Acesso mal formado na linha " + parser.getTokensList().get(parser.index - 1).line);
	}
	
}
