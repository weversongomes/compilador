package control.parser;

import model.EscopoClasse;
import model.EscopoMetodo;
import model.SemanticAnalyzer;
import model.Symbol;

/**
 * 
 * Classe responsavel por reconhecer metodos, incluindo o metodo main
 *
 */
public class MethodParser {
	
	private FileParser parser;
	private String[] mainStructure = {"bool", "main", "(", ")", "{", "<commands>", "<return>", "}"};
	private String[] methodStructure = {"<return_type>", "<name>", "(", "<parameters>", ")", "{", "<commands>", "<return>", "}"};
	private String[] methodReturnStructure = {"<", ":", "<return>", ":", ">"};
	EscopoClasse escopoPai;
	public EscopoMetodo em;
	String varMethodReturn;
	
	public MethodParser(FileParser parser, EscopoClasse escopo) {
		this.escopoPai = escopo;
		em = new EscopoMetodo();
		em.escopoPai = escopoPai;
		this.parser = parser;
		parser.escopos.add(em);
	}
	
	// reconhece a estrutura sintatica de um metodo
	public boolean recognizeMethod(String varType) {
		boolean isCorrect = true;
		int methodIndex = 0;
		Symbol symbol = new Symbol();
		while (methodIndex < methodStructure.length) {
			if (parser.tokensToRead()) {
				if (methodIndex == 0) { // verifica se o retorno do metodo esta correto
					if (!(parser.isAttributeType() || parser.getTokensList().get(parser.index).type.equals("ID"))) {
						isCorrect = false;
					}
					symbol.type = "metodo";
					symbol.value = varType;
					methodIndex++;
					parser.index = parser.index + 1;
				} else if (methodIndex == 1) { // verifica se o nome do metodo eh valido
					if (!parser.getTokensList().get(parser.index).type.equals("ID")) {
						isCorrect = false;
					}
					symbol.name = parser.getTokensList().get(parser.index).lexeme;
					if (escopoPai.addSimbol(symbol) == 0) {
						parser.addSemanticError("ERRO SEMANTICO: Identificador de metodo duplicado na linha " + parser.getTokensList().get(parser.index).line);
					}
					methodIndex++;
					parser.index = parser.index + 1;
				} else if (methodIndex == 3) { // verifica se os parametros do metodo estao corretos
					if (!recognizeMethodParameters()) {
						isCorrect = false;
					}
					methodIndex++;
					parser.index = parser.index + 1;
				} else if (methodIndex == 6) { // verifica se os comandos estao corretos
					if (parser.getTokensList().get(parser.index).lexeme.equals("<")) { // se for um um "<" eh porque nao ha nenhum comando dentro do metodo, apenas o retorno
						methodIndex++;
					} else {
						while (parser.tokensToRead() && new CommandParser(parser, em).recognizeCommand()) { // enquanto houver comandos validos dentro do metodo
							parser.index = parser.index + 1;							
						}
						if (!parser.getTokensList().get(parser.index).lexeme.equals("<")) { // nao ha mais comandos dentro do metodo, achou o "<" do inicio do retorno
							parser.index = parser.index + 1;
						}
						methodIndex++;
					}
				} else if (methodIndex == 7) {
					if (!recognizeMethodReturn()) {
						isCorrect = false;
					}
					methodIndex++;
					parser.index = parser.index + 1;
				} else { // verifica se os demais tokens estao corretos
					if (!parser.getTokensList().get(parser.index).lexeme.equals(methodStructure[methodIndex])) {
						isCorrect = false;
					}
					if (methodIndex < 8) {
						parser.index = parser.index + 1;
					}
					methodIndex++;
				}
			}
			//if (!isCorrect) {
				//forparser.index = parser.index + 1;
			//}		
		}
		return isCorrect;
	}
	
	// reconhece a estrutura sintatica dos parametros de um metodo
	public boolean recognizeMethodParameters() {
		boolean isFirstParameter = true;
		while (parser.tokensToRead() && !parser.getTokensList().get(parser.index).lexeme.equals(")")) {
			Symbol symbol = new Symbol();
			if (isFirstParameter) { // se for o primeiro parametro, nao tem virgula antes
				if (parser.isAttributeType() || parser.getTokensList().get(parser.index).type.equals("ID")) { // verifica se o tipo do parametro esta correto
					symbol.type = parser.getTokensList().get(parser.index).lexeme;
					parser.index = parser.index + 1;
				} else {
					return false;
				}
				if (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("ID")) { // verifica se o nome do parametro eh valido
					symbol.name = parser.getTokensList().get(parser.index).lexeme;
					em.addParam(symbol);
					parser.index = parser.index + 1;
					isFirstParameter = false; // se tiver proximo parametro, nao sera mais o primeiro
				} else {
					return false;
				}
			} else {
				if (parser.getTokensList().get(parser.index).lexeme.equals(",")) {
					parser.index = parser.index + 1;
				} else {
					return false;
				}
				if (parser.tokensToRead() && (parser.isAttributeType() || parser.getTokensList().get(parser.index).type.equals("ID"))) { // verifica se o tipo do parametro esta correto
					symbol.type = parser.getTokensList().get(parser.index).lexeme;
					parser.index = parser.index + 1;
				} else {
					return false;
				}
				if (parser.tokensToRead() && (parser.getTokensList().get(parser.index).type.equals("ID"))) { // verifica se o nome do parametro eh valido
					symbol.name = parser.getTokensList().get(parser.index).lexeme;
					em.addParam(symbol);
					parser.index = parser.index + 1;
				} else {
					return false;
				}
			}
		}
		parser.index = parser.index - 1; // achou o ")", entao a leitura continua a partir dele
		return true;
	}
	
	public void setMethodName(String methodName) {
		em.name = methodName;
	}
	
	// reconhece a estrutura sintatica do retorno de um metodo
	public boolean recognizeMethodReturn() {
		boolean isCorrect = true;
		int methodReturnIndex = 0;
		while (methodReturnIndex < methodReturnStructure.length) {
			if (parser.tokensToRead()) {
				if (methodReturnIndex == 2) {
					if (!parser.getTokensList().get(parser.index).type.equals("ID")) {
						isCorrect = false;
					}
					varMethodReturn = parser.getTokensList().get(parser.index).lexeme;
					methodReturnIndex++;
					parser.index = parser.index + 1;
				} else {
					if (!parser.getTokensList().get(parser.index).lexeme.equals(methodReturnStructure[methodReturnIndex])) {
						isCorrect = false;
					}
					if (methodReturnIndex < 4) {
						parser.index = parser.index + 1;
					}
					methodReturnIndex++;
				}
			}
		}
		return isCorrect;
	}
	
	// reconhece a estrutura sintatica do metodo main
	public boolean recognizeMain() {
		boolean isCorrect = true;
		int mainIndex = 0;
		while (mainIndex < mainStructure.length) {
			if (parser.tokensToRead()) {
				if (mainIndex == 0) { // verifica se o retorno da main eh bool
					if (!parser.getTokensList().get(parser.index).lexeme.equals("bool")) {
						isCorrect = false;
					}
					mainIndex++;
					parser.index = parser.index + 1;
				} else if (mainIndex == 1) { // verifica se o nome da main esta correta
					if (!parser.getTokensList().get(parser.index).lexeme.equals("main")) {
						isCorrect = false;
					}
					mainIndex++;
					parser.index = parser.index + 1;
				} else if (mainIndex == 5) { // verifica se os comandos estao corretos
					if (parser.getTokensList().get(parser.index).lexeme.equals("<")) { // se for um um "<" eh porque nao ha nenhum comando dentro da main, apenas o retorno
						mainIndex++;
					} else {
						while (parser.tokensToRead() && new CommandParser(parser, em).recognizeCommand()) { // enquanto houver comandos validos dentro da main
							parser.index = parser.index + 1;							
						}
						if (!parser.getTokensList().get(parser.index).lexeme.equals("<")) { // nao ha mais comandos dentro da main, achou o "<" do inicio do retorno
							parser.index = parser.index + 1;
						}
						mainIndex++;
					}
				} else if (mainIndex == 6) {
					if (!recognizeMethodReturn()) {
						isCorrect = false;
					}
					mainIndex++;
					parser.index = parser.index + 1;
				} else { // verifica se os demais tokens estao corretos
					if (!parser.getTokensList().get(parser.index).lexeme.equals(mainStructure[mainIndex])) {
						isCorrect = false;
					}
					if (mainIndex < 7) {
						parser.index = parser.index + 1;
					} else {
						if (!parser.hasMain()) {
							//System.out.println("Correto. Apenas um metodo main existente ate aqui");
						} else {
							parser.addSemanticError("ERRO SEMANTICO: Ja existe um metodo main no codigo");
						}
					}
					mainIndex++;
				}
			}
			//if (!isCorrect) {
				//forparser.index = parser.index + 1;
			//}		
		}
		return isCorrect;
	}
	
}
