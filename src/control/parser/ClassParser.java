package control.parser;

import model.EscopoClasse;
import model.Simbol;

/**
 * 
 * Classe responsavel por reconhecer o conteudo de uma classe, ou seja, metodos e atributos
 *
 */
public class ClassParser {
	
	private FileParser fileParser;
	EscopoClasse ec;
	
	public ClassParser(FileParser parser, EscopoClasse ec) {
		this.ec = ec;
		this.fileParser = parser;
	}
	
	// reconhece o conteudo de uma classe, isto eh, metodos e atributos
	public boolean recognizeClassContent() {
		boolean isVector = false;
		boolean isFinal = false;
		if (fileParser.getTokensList().get(fileParser.index).lexeme.equals("final")) { // atributo pode ser constante
			isFinal = true;
			fileParser.index = fileParser.index + 1;
		}
		if (fileParser.tokensToRead() && (fileParser.isAttributeType() || fileParser.getTokensList().get(fileParser.index).type.equals("ID"))) {
			String varType = fileParser.getTokensList().get(fileParser.index).lexeme;
			fileParser.index = fileParser.index + 1;
			if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).lexeme.equals("=") && isVector == false && isFinal == false) { // inicializacao de atributo
				fileParser.index = fileParser.index - 1; // para comecar a varredura de inicializacao de variavel pelo id
				if (!new VariableParser(fileParser).recognizeInitialization(false, varType)) { // verifica se a atribuicao esta correta
					panicModeAttributeInitialization();
				} else {
					fileParser.index = fileParser.index + 1;
					if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).lexeme.equals(";")) { // inicializacao
						System.out.println("Inicializacao de atributo correta na linha " + fileParser.getTokensList().get(fileParser.index).line);
						return true;
					} else if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).type.equals("ARIOP")) { // inicializacao com operacao aritmetica
						fileParser.index = fileParser.index + 1;
						if (fileParser.tokensToRead() && new OperationParser(fileParser).recognizeArithmeticOperation()) {
							System.out.println("Inicializacao de atributo com operacao aritmetica correta na linha " + fileParser.getTokensList().get(fileParser.index).line);
							return true;
						} else {
							panicModeAttributeInitialization();
						}
					} else {
						panicModeAttributeInitialization();
					}
				}
			} else { // declaracao de atributo ou metodo
				if (fileParser.tokensToRead() && new VariableParser(fileParser).recognizeVector()) { // verifica se eh vetor ou matriz
					isVector = true;
					fileParser.index = fileParser.index + 1;
				}
				if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).lexeme.equals("main") && isVector == false && isFinal == false) { // declaracao da main nao pode ter vetor nem final
					Simbol simbol = new Simbol();
					simbol.name = "main";
					simbol.type = "metodo";
					simbol.isConstant = isFinal;
					ec.addSimbol(simbol);
					fileParser.index = fileParser.index - 1;
					MethodParser mParser = new MethodParser(fileParser, ec);
					if (!mParser.recognizeMain()) {
						panicModeMethod();
					} else {
						System.out.println("Main correta na linha " + fileParser.getTokensList().get(fileParser.index).line);
						return true;
					}
				} else if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).type.equals("ID")) {
					fileParser.index = fileParser.index + 1;
					if (fileParser.tokensToRead() && fileParser.getTokensList().get(fileParser.index).lexeme.equals("(") && isVector == false && isFinal == false) { // declaracao de metodo nao pode ter vetor nem final
						fileParser.index = fileParser.index - 2; // para comecar a varredura da estrutura do metodo a partir do tipo de retorno
						MethodParser mParser = new MethodParser(fileParser, ec);
						if (!mParser.recognizeMethod()) {
							panicModeMethod();
						} else {
							System.out.println("Metodo correto na linha " + fileParser.getTokensList().get(fileParser.index).line);
							//mParser.em.showSimbols();
							return true;
						}
					} else if (fileParser.getTokensList().get(fileParser.index).lexeme.equals(";") || fileParser.getTokensList().get(fileParser.index).lexeme.equals(",")) { // declaracao de variavel
						fileParser.index = fileParser.index - 1; // para comecar a varredura da estrutura de declaracao de variavel a partir do nome
						varType = fileParser.getTokensList().get(fileParser.index - 1).lexeme;
						if (!new VariableParser(fileParser).recognizeVariableDeclaration(varType, ec)) {
							panicModeAttributeDeclaration();
						} else {
							System.out.println("Declaracao de atributo correta na linha " + fileParser.getTokensList().get(fileParser.index).line);
							return true;
						}
					} else {
						panicModeAttributeDeclaration();
					}
				} else {
					panicModeAttributeDeclaration();
				}
			}
		}
		return false;
	}
	
	public void panicModeAttributeDeclaration() {
		fileParser.addError("ERRO: Declaracao de atributo mal formada na linha " + fileParser.getTokensList().get(fileParser.index-1).line);
	}
	
	public void panicModeAttributeInitialization() {
		fileParser.addError("ERRO: Inicializacao de atributo mal formada na linha " + fileParser.getTokensList().get(fileParser.index-1).line);
	}

	public void panicModeMethod() {
		fileParser.addError("ERRO: Metodo mal formado na linha " + fileParser.getTokensList().get(fileParser.index-1).line);
	}
	
}
