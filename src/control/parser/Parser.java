package control.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import model.Escopo;
import model.Token;

public class Parser {
	
	private ArrayList<Token> tokensList; // lista de tokens recebida do lexico
	FileWriter fw;
	BufferedWriter bw;
	
	public Parser(ArrayList<Token> tokensList) {
		this.tokensList = tokensList;
	}
	
	public void readTokens(String dir, String filename) throws IOException {
		//System.out.println("---------- INICIO DA ANALISE SINTATICA ----------");
		FileParser fp = new FileParser(tokensList);
		fp.fileParsing();
		//fp.eg.showSimbols();
		//System.out.println(fp.escopos.size());
		for(Escopo escopo : fp.escopos) {
			escopo.showSimbols();
		}
		//System.out.println("---------- FIM DA ANALISE SINTATICA ----------");
		
		File folder = new File(dir + "/results");
		if (!folder.exists()) {
		    boolean result = false;
		    folder.mkdir();
		    result = true;
		    if(result) {
		        System.out.println("Folder created");  
		    }
		}
		
		fw = new FileWriter(dir + "/results/" + filename);
		bw = new BufferedWriter(fw);
		ArrayList<String> errorsList = fp.getSemanticErrorsList();
		if (errorsList.size() == 0) {
			//System.out.println("Codigo sintaticamente correto");
			bw.write("Codigo semanticamente correto");
		} else {
			for (int i = 0; i < errorsList.size(); i++) {
				//System.out.println(errorsList.get(i));
				bw.write(errorsList.get(i) + "\n");
			}
		}
		bw.close();
		fw.close();
	}
}