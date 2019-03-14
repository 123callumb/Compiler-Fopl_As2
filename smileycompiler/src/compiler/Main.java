package compiler;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.*;

import smiley.*;

@SuppressWarnings("deprecation")
public class Main {

	public static void main(String[] args) throws Exception {
		ANTLRInputStream input = new ANTLRInputStream(System.in);
		SmileyBoiLexer lexer = new SmileyBoiLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SmileyBoiParser parser = new SmileyBoiParser(tokens);
		ParseTree tree = parser.fileCompilation();
		ParseTreeWalker walker = new ParseTreeWalker();
		// For cleaning my XML parser is in here. 
		walker.walk(new XMLWorker(), tree); 
	}

}
