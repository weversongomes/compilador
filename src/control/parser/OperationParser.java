package control.parser;

import java.util.ArrayList;

import model.Escopo;
import model.SemanticAnalyzer;

/**
 * 
 * Classe responsavel por reconhecer operacoes aritmeticas, logicas e relacionais
 *
 */
public class OperationParser {
	
	private FileParser parser;
	Escopo escopo;
	
	public OperationParser(FileParser parser, Escopo escopo) {
		this.parser = parser;
		this.escopo = escopo;
	}
	
	// reconhece a estrutura sintatica de operacao aritmetica
	public boolean recognizeArithmeticOperation(String varValue1, String varValue2) {
		ArrayList<String> operators = new ArrayList<String>();
		// como a leitura comeca apos o primeiro operador, eh necessario adicionar as duas variaveis anteriores para checagem semantica
		if (!varValue1.equals("")) { // por ex.: A = B + C, varValue1 eh A 
			operators.add(varValue1);
		}
		if (!varValue2.equals("")) { // por ex.: A = B + C, varValue2 eh B 
			operators.add(varValue2);
		}
		if (parser.getTokensList().get(parser.index).type.equals("ID") || parser.getTokensList().get(parser.index).type.equals("NUM")) {
			operators.add(parser.getTokensList().get(parser.index).lexeme);
			parser.index = parser.index + 1;
			while (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("ARIOP")) {
				parser.index = parser.index + 1;
				if (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("ID") || parser.getTokensList().get(parser.index).type.equals("NUM")) {
					operators.add(parser.getTokensList().get(parser.index).lexeme);
					parser.index = parser.index + 1;
				}
			}
			if (parser.tokensToRead() && parser.getTokensList().get(parser.index).type.equals("DEL")) {
				String message = SemanticAnalyzer.checkArithmeticOperation(operators, escopo, parser.getTokensList().get(parser.index).line);
				if (message.equals("ok")) {
					//System.out.println("Operacao aritmetica semanticamente correta apos a atribuicao");
				} else {
					parser.addSemanticError(message);
				}
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
						//System.out.println("Operacao relacional semanticamente correta");
					} else {
						parser.addSemanticError("ERRO SEMANTICO: Operacao relacional semanticamente incorreta");
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
