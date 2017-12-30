package control.parser;

import java.util.ArrayList;

import model.Escopo;
import model.EscopoClasse;
import model.EscopoGlobal;
import model.SemanticAnalyzer;
import model.Symbol;
import model.Token;

/**
 * 
 * Classe responsavel por reconhecer codigos de classes e variaveis globais
 *
 */
public class FileParser {
	
	int index; // indice atual da lista de tokens
	private ArrayList<Token> tokensList; // lista de tokens recebida do lexico
	private ArrayList<String> errorsList; // lista de erros sintaticos
	private String[] classStructure = {"class", "<name>", "{", "<content>", "}"}; // estrutura sintatica de uma classe
	public ArrayList<Escopo> escopos;
	EscopoGlobal eg;
	
	public FileParser(ArrayList<Token> tokensList) {
		this.tokensList = tokensList;
		errorsList = new ArrayList<String>();
		eg = new EscopoGlobal();
		escopos = new ArrayList<>();
		escopos.add(eg);
	}
	
	// metodo inicial que chama o reconhecimento de classes e variaveis globais
	public void fileParsing() {
		for (index = 0; index < tokensList.size(); index++) {
			if (tokensList.get(index).lexeme.equals("class")) { // declaracao de classe
				if (!recognizeClass()) {
					panicModeClass();
				} else {
					System.out.println("Classe correta na linha " + tokensList.get(index).line);
				}
			} else { // declaracao de variavel global
				if (tokensList.get(index).lexeme.equals("final")) { // pode ser constante
					index++;
					if (tokensToRead() &&  (isAttributeType() || tokensList.get(index).type.equals("ID"))) {
						recognizeGlobalVariable(true);
					}
				} else if (isAttributeType() || tokensList.get(index).type.equals("ID")) {
					recognizeGlobalVariable(false);
				} else {
					// Comando nao reconhecido
				}
			}
		}
	}
	
	// reconhece a estrutura sintatica de uma classe
	public boolean recognizeClass() {
		boolean isCorrect = true;
		int classIndex = 0;
		Symbol childClass = null, parentClass = null;
		while (classIndex < classStructure.length) {
			if (tokensToRead()) {
				if (classIndex == 1) { // verifica se eh um nome de classe valido
					if (!tokensList.get(index).type.equals("ID")) {
						isCorrect = false;
					}
					childClass = new Symbol();
					childClass.name = getTokensList().get(index).lexeme;
					childClass.type = "class";
					if (eg.addSimbol(childClass) == 0) {
						System.out.println("ERRO SEMANTICO: Identificador duplicado na linha " + getTokensList().get(index).line);
					}
					classIndex++;
					index++;
				} else if (classIndex == 2) {
					if (tokensList.get(index).lexeme.equals(":")) {
						index++;
						if (tokensToRead() && !tokensList.get(index).type.equals("ID")) {
							isCorrect = false;
						} else {
							parentClass = eg.getSymbol(getTokensList().get(index).lexeme);
							if (parentClass != null) {
								if (!eg.getSimbols().contains(parentClass)) {
									System.out.println("ERRO SEMANTICO: Classe pai da heranca nao existe na linha " + getTokensList().get(index).line);
								} else {
									if (parentClass.hasParent == true) {
										System.out.println("ERRO SEMANTICO: Existe heranca em cadeia na linha " + getTokensList().get(index).line);
									} else {
										System.out.println("HERANÇA CORRETA NA LINHA " + getTokensList().get(index).line);
									}
									eg.setSymbolParent(childClass); // indica que esta classe eh filha de uma classe pai
								}
							} else {
								System.out.println("ERRO SEMANTICO: Classe pai da heranca nao existe na linha " + getTokensList().get(index).line);
							}
						}
					} else if (!tokensList.get(index).lexeme.equals("{")) {
						isCorrect = false;
					} 
					if (tokensList.get(index).lexeme.equals("{")) {
						classIndex++;
					}
					index++;
				} else if (classIndex == 3) { // verifica se os comandos estao corretos
					EscopoClasse ec = new EscopoClasse();
					ec.setEscopoPai(eg);
					escopos.add(ec);
					if (tokensList.get(index).lexeme.equals("}")) { // se for um um "}" eh porque nao ha nenhum comando dentro da classe
						classIndex++;
					} else {
						while (tokensToRead() && new ClassParser(this, ec).recognizeClassContent()) { // enquanto houver comandos validos dentro da classe 
							index++;							
						}
						if (!tokensList.get(index).lexeme.equals("}")) { // nao ha mais comandos dentro da classe, achou o "}"
							index++;
						}
						classIndex++;
					}
				} else { // verifica se os demais tokens estao corretos
					if (!tokensList.get(index).lexeme.equals(classStructure[classIndex])) {
						isCorrect = false;
					}
					if (classIndex < 4) { // se nao for o ultimo token, avanca o indice
						index++;
					} 
					classIndex++;
				}
			}
		}
		return isCorrect;
	}
	
	// reconhece a estrutura sintatica de uma variavel global
	public boolean recognizeGlobalVariable(boolean isConstant) {
		String varType = tokensList.get(index).lexeme;
		String attrVar = varType;
		if (tokensList.get(index).type.equals("ID") && !isConstant) { // inicializacao de variavel global
			index++;
			if (tokensToRead() && new VariableParser(this).recognizeVector()) { // verifica se eh vetor ou matriz
				index++;
			}
			if (tokensToRead() && tokensList.get(index).lexeme.equals("=")) {
				String auxVar = ""; 
				while (!tokensList.get(index).type.equals("ID")) {
					index--; // para comecar a varredura de inicializacao de variavel pelo id
					auxVar = tokensList.get(index).lexeme + auxVar; // caso seja vetor ou matriz, adiciona as dimensoes ao id: ID [NUM][NUM]
				}
				attrVar = auxVar;
				if (!new VariableParser(this).recognizeInitialization(false, varType, eg)) { // verifica se a atribuicao esta correta
					panicModeGlobalVariableInitialization();
				} else {
					String checkType = SemanticAnalyzer.checkType(attrVar, tokensList.get(index).lexeme, eg, true);
					if (checkType.equals("ok")) {
						System.out.println("tipo compativel na linha " + tokensList.get(index).line);
					} else if (checkType.equals("string")) {
						if (tokensList.get(index).type.equals("STR")) {
							System.out.println("string compativel na linha " + tokensList.get(index).line);
						} else {
							System.out.println("string incompativel na linha " + tokensList.get(index).line);
						}
					} else {
						System.out.println("tipo incompativel na linha " + tokensList.get(index).line);
					}
					index++;
					if (tokensToRead() && tokensList.get(index).lexeme.equals(";")) { // inicializacao
						System.out.println("Inicializacao de variavel global correta na linha " + tokensList.get(index).line);
						return true;
/*					} else if (tokensToRead() && tokensList.get(index).type.equals("ARIOP")) { // inicializacao com operacao aritmetica
						index++;
						if (tokensToRead() && new OperationParser(this).recognizeArithmeticOperation()) {
							System.out.println("Inicializacao de variavel global com operacao aritmetica correta na linha " + tokensList.get(index).line);
							return true;
						} else {
							panicModeGlobalVariableInitialization();
						}*/
					} else {
						panicModeGlobalVariableInitialization();
					}
				}
			} else {
				panicModeGlobalVariableDeclaration();
			}
		} else if (isAttributeType()) { // declaracao de variavel global ou constante
			index++;
			if (tokensToRead() && new VariableParser(this).recognizeVector() && !isConstant) { // verifica se eh vetor ou matriz
				index++;
			}
			if (tokensToRead() && tokensList.get(index).type.equals("ID")) {
				varType = tokensList.get(index - 1).lexeme;
				varType = isVector(varType); // caso seja um vetor ou matriz, o tipo sera <TIPO>[NUM]*
				index++;
				if (tokensToRead() && (tokensList.get(index).lexeme.equals(";") || tokensList.get(index).lexeme.equals(",")) && !isConstant) {
					index--; // para comecar a varredura da estrutura de declaracao de variavel a partir do id
					if (!new VariableParser(this).recognizeVariableDeclaration(varType, eg, false)) {
						panicModeGlobalVariableDeclaration();
					} else {
						System.out.println("Declaracao de variavel local correta na linha " + tokensList.get(index).line);
						return true;
					}
				} else if (tokensToRead() && tokensList.get(index).lexeme.equals("=") && isConstant) { // constante
					index--; // para comecar a varredura da estrutura de declaracao e inicializacao de constante a partir do id
					if (!new VariableParser(this).recognizeInitialization(true, varType, eg)) {
						panicModeGlobalVariableInitialization();
					} else {
						index++;
						
						if (tokensToRead() && tokensList.get(index).lexeme.equals(";")) {
							System.out.println("Declaracao de constante correta na linha " + tokensList.get(index).line);
							return true;
						}
					}
				} else {
					panicModeGlobalVariableDeclaration();
				}
			}
		}
		return false;
	}
	
	public void panicModeClass() {
		addError("ERRO: Classe mal formada na linha " + tokensList.get(index-1).line);
	}
	
	public void panicModeGlobalVariableInitialization() {
		addError("ERRO: Inicializacao de variavel global mal formada na linha " + tokensList.get(index-1).line);
	}
	
	public void panicModeGlobalVariableDeclaration() {
		addError("ERRO: Declaracao de variavel global mal formada na linha " + tokensList.get(index-1).line);
	}
	
	public ArrayList<Token> getTokensList() {
		return tokensList;
	}
	
	public ArrayList<String> getErrorsList() {
		return errorsList;
	}
	
	public void addError(String message) {
		errorsList.add(message);
	}
	
	public boolean isAttributeType() {
		return (tokensList.get(index).lexeme.equals("int") || tokensList.get(index).lexeme.equals("float") ||
				tokensList.get(index).lexeme.equals("bool") || tokensList.get(index).lexeme.equals("string"));
	}
	
	public boolean tokensToRead() {
		return index < tokensList.size();
	}
	
	public boolean hasMain() {
		for (Escopo e : escopos) {
			if (e instanceof EscopoClasse) {
				for (Symbol s : e.getSimbols()) {
					if (s.name.equals("main")) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	public String isVector(String varType) { // caso seja um vetor ou matriz, a string eh montada: <tipo><dimensoes>
		if (varType.equals("]")) {
			int auxIndex = index - 1;
			varType = "";
			while (!tokensList.get(auxIndex).type.equals("RES")) {
				varType = tokensList.get(auxIndex).lexeme + varType;
				auxIndex--;
			}
			varType = tokensList.get(auxIndex).lexeme + varType;
		}
		return varType;
	}

}
