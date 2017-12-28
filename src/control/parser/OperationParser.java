package control.parser;

import model.EscopoMetodo;
import model.SemanticAnalyzer;

/**
 * 
 * Classe responsavel por reconhecer operacoes aritmeticas, logicas e relacionais
 *
 */
public class OperationParser {
	
	private FileParser parser;
	EscopoMetodo escopo;
	
	public OperationParser(FileParser parser, EscopoMetodo escopo) {
		this.parser = parser;
		this.escopo = escopo;
	}
	
	// reconhece a estrutura sintatica de operacao aritmetica
	public boolean recognizeArithmeticOperation() {
		if (parser.getTokensList().get(parser.index).type.equals("ID") || parser.getTokensList().get(parser.index).type.equals("NUM")) {
			parser.index = parser.index + 1;
			while (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("ARIOP")) {
				parser.index = parser.index + 1;
				if (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("ID") || parser.getTokensList().get(parser.index).type.equals("NUM")) {
					parser.index = parser.index + 1;
				}
			}
			if (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("DEL")) {
				return true;
			}
		}
		return false;
	}
	
	// reconhece a estrutura sintatica de operacao logica
	public boolean recognizeLogicalOperation() {
		if (parser.tokensToRead() && (parser.getTokensList().get(parser.index).lexeme.equals("&&") || parser.getTokensList().get(parser.index).lexeme.equals("||"))) {
			parser.index = parser.index + 1;
			if (parser.tokensToRead()) {
				return recognizeRelationalOperation();
			}
		}
		return false;
	}
	
	// reconhece a estrutura sintatica de operacao relacional
	public boolean recognizeRelationalOperation() {
		String[] relOp = new String[3];
		if (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("ID")) {
			relOp[0] = parser.getTokensList().get(parser.index).lexeme;
			parser.index = parser.index + 1;
			if (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("RELOP")) {
				relOp[1] = parser.getTokensList().get(parser.index).lexeme;
				parser.index = parser.index + 1;
				if (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("ID") || parser.getTokensList().get(parser.index).type.equals("NUM") || parser.getTokensList().get(parser.index).type.equals("STR") || 
						parser.getTokensList().get(parser.index).lexeme.equals("true") || parser.getTokensList().get(parser.index).lexeme.equals("false")) {
					relOp[2] = parser.getTokensList().get(parser.index).lexeme;
					if (SemanticAnalyzer.checkRelationalOperation(relOp, escopo).equals("ok")) {
						System.out.println("Operacao relacional semanticamente correta");
					} else {
						System.out.println("Operacao relacional semanticamente incorreta");
					}
					if (parser.getTokensList().get(parser.index + 1).lexeme.equals(";") || parser.getTokensList().get(parser.index + 1).lexeme.equals(")")) {
						return true;
					} else {
						parser.index = parser.index + 1;
						return recognizeLogicalOperation();
					}
				}
			}
		}
		return false;
	}

	public void panicModeArithmeticOperation() {
		parser.addError("ERRO: Operacao aritmetica mal formada na linha " + parser.getTokensList().get(parser.index-1).line);
	}
	
}
