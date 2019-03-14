package compiler;

import smiley.SmileyBoiBaseListener;
import smiley.SmileyBoiParser;
import smiley.SmileyBoiParser.*;

public class XMLWorker extends SmileyBoiBaseListener {
	
	@Override public void enterFileCompilation(FileCompilationContext ctx) {System.out.println("<Script>");}
	@Override public void exitFileCompilation(FileCompilationContext ctx) {System.out.println("</Script>");}
	@Override public void enterMainCodeBlock(SmileyBoiParser.MainCodeBlockContext ctx) {System.out.println("<Main>");}
	@Override public void exitMainCodeBlock(SmileyBoiParser.MainCodeBlockContext ctx) {System.out.println("</Main>");}
	@Override public void enterCodeBlockStmt(SmileyBoiParser.CodeBlockStmtContext ctx) {System.out.println("<Statement>");}
	@Override public void exitCodeBlockStmt(SmileyBoiParser.CodeBlockStmtContext ctx) {System.out.println("</Statement>");}
	@Override public void enterPrintStmt(SmileyBoiParser.PrintStmtContext ctx) {
		System.out.println("<Print>");
		System.out.println(ctx.getChild(1).getText());
	}
	
	@Override public void exitPrintStmt(SmileyBoiParser.PrintStmtContext ctx) { 
		System.out.println("</Print>");
	}

}


/*
 *		---- USING THIS SPACE FOR COPY AND PASTING MY OWN SCRIPT YEE ---- 
 * 		(: Script test~ Smile->{P->"Hello World"~}  :)
 *
 * */
 