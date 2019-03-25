package compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import smiley.SmileyBoiBaseListener;
import smiley.SmileyBoiParser;
import smiley.SmileyBoiParser.*;

public class XMLWorker extends SmileyBoiBaseListener {
	
	String xmlString = new String("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
	
	@Override public void enterFileCompilation(FileCompilationContext ctx) {
		xmlString += "<Script ";
		xmlString += "name=\"" + ctx.getChild(1).getChild(1).getText() + "\"";
		xmlString += ">";
	}
	
	@Override public void exitFileCompilation(FileCompilationContext ctx) { 
		xmlString +="</Script>";
	}
	
	@Override public void enterMainCodeBlock(SmileyBoiParser.MainCodeBlockContext ctx) {
		xmlString += "<Main>";
	}
	
	@Override public void exitMainCodeBlock(SmileyBoiParser.MainCodeBlockContext ctx) {
		xmlString += "</Main>";
	}
	
	@Override public void enterAssign(SmileyBoiParser.AssignContext ctx) {
		xmlString += "<Var";
		
		//Check if assign contains an array value 
		if(ctx.getChildCount() == 1) {
			
			//This is catches array[i] <- value~ statements
			if(ctx.getChild(0).getChild(1).getText().equals("[")) {
				xmlString += " arrayName=\"" + ctx.getChild(0).getChild(0) + "\">";
			}
			
			//This catches str value <- array[i]~ statements
			if(ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 1).getText().equals("]")) {
				xmlString += " name=\"" + ctx.getChild(0).getChild(0).getChild(ctx.getChild(0).getChild(0).getChildCount() - 1).getText() + "\"";
			}
			
		}else{
			int typePos = ctx.getChild(0).getChild(0).getText().equals("Const") ? 1 : 0; 
			if(ctx.getChild(0).getChildCount() != 1) { // Have to check in case it's a reassign to an existing variable.
				xmlString += " type=\"" + ctx.getChild(0).getChild(typePos).getText() + "\"";
				xmlString += " const=\"" + (typePos == 0 ? "false" : "true") + "\"";
			}
			// So this is just getting the name correct based on array variables and stuff.
			xmlString += " name=\"";
			xmlString += ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 1).getText(); 
			xmlString += "\">";
			
			//Then the var value can go here
			xmlString += ctx.getChildCount() != 1 ? ctx.getChild(ctx.getChildCount() -1).getText() : "null";
		}	
		
	}
	
	@Override public void enterArrayIndex(SmileyBoiParser.ArrayIndexContext ctx) { 
		xmlString += "<ArrayValue index=\"" + ctx.getChild(2).getText() + "\">";
		xmlString += ctx.getChild(ctx.getChildCount() - 1).getText();
	}

	@Override public void exitArrayIndex(SmileyBoiParser.ArrayIndexContext ctx) {
		xmlString += "</ArrayValue>";
	}
	
	@Override public void exitAssign(SmileyBoiParser.AssignContext ctx) {
		xmlString += "</Var>";
	}
	
	@Override public void enterPrintStmt(SmileyBoiParser.PrintStmtContext ctx) {
		xmlString += "<Print>";
		xmlString += ctx.getChild(1).getText();
	}
	
	@Override public void exitPrintStmt(SmileyBoiParser.PrintStmtContext ctx) { 
		xmlString += "</Print>";
	}
	
	@Override public void enterIfStmt(SmileyBoiParser.IfStmtContext ctx) { 
		xmlString += "<ConditionBlock>";
	}
	
	@Override public void exitIfStmt(SmileyBoiParser.IfStmtContext ctx) {
		xmlString += "</ConditionBlock>";
	}
	
	@Override public void enterIfCondition(SmileyBoiParser.IfConditionContext ctx) {
		xmlString += "<If";
		xmlString += " condition=\"" + ctx.getChild(1).getChild(1).getText().replaceAll(">", "&gt;").replaceAll("<", "&lt;") + "\">";
	}
	
	@Override public void exitIfCondition(SmileyBoiParser.IfConditionContext ctx) {
		xmlString += "</If>";
	}
	
	@Override public void enterElseStmt(SmileyBoiParser.ElseStmtContext ctx) { 
		xmlString += "<Else>";
	}
	
	@Override public void exitElseStmt(SmileyBoiParser.ElseStmtContext ctx) { 
		xmlString += "</Else>";
	}
	
	@Override public void enterElseIfStmt(SmileyBoiParser.ElseIfStmtContext ctx) { 
		xmlString += "<ElseIf ";
		xmlString += " condition=\"" + ctx.getChild(1).getChild(1).getText().replaceAll(">", "&gt;").replaceAll("<", "&lt;") + "\">";
	}
	
	@Override public void exitElseIfStmt(SmileyBoiParser.ElseIfStmtContext ctx) { 
		xmlString += "</ElseIf>";
	}
		
	@Override public void enterFunctionDeclare(SmileyBoiParser.FunctionDeclareContext ctx) { 
		xmlString += "<Function";
		xmlString += " identity=\"" + ctx.getChild(2).getText() + "\"";
		xmlString += ctx.getChild(4).getText().equals("(") ? "" : ">";
	}
	
	@Override public void exitFunctionDeclare(SmileyBoiParser.FunctionDeclareContext ctx) { 
		xmlString += "</Function>";
	}
	
	@Override public void enterParams(SmileyBoiParser.ParamsContext ctx) { 
		for(int i = 0; i < ctx.getChildCount(); i += 2) { // we add two so we skip the comma
			xmlString += " paramName_" + (i/2) + "=\"" + ctx.getChild(i).getText() + "\"";
		}
		xmlString += ctx.getChildCount() > 0 ? ">" : "";
	}
	
	@Override public void enterFunctionCall(SmileyBoiParser.FunctionCallContext ctx) { 
		xmlString += "<FuncCall identity=\"";
		xmlString += ctx.getChild(1).getText() + "\"";
		xmlString += ctx.getChildCount() > 2 && ctx.getChild(2).getText().equals("(") ? "" : ">";
	}
	
	@Override public void exitFunctionCall(SmileyBoiParser.FunctionCallContext ctx) { 
		xmlString += "</FuncCall>";
	}
	
	@Override public void enterArguments(SmileyBoiParser.ArgumentsContext ctx) {
		for(int i = 0; i < ctx.getChildCount(); i += 2) { // we add two so we skip the comma
			xmlString += " argument_" + (i/2) + "=\"" + ctx.getChild(i).getText() + "\"";
		}
		xmlString += ctx.getChildCount() > 0 ? ">" : "";
	}
	
	@Override public void enterIterationStmt(SmileyBoiParser.IterationStmtContext ctx) { xmlString += "<Iteration>"; }
	@Override public void exitIterationStmt(SmileyBoiParser.IterationStmtContext ctx) { xmlString += "</Iteration>"; }
	
	@Override public void enterWhileStmt(SmileyBoiParser.WhileStmtContext ctx) {
		xmlString += "<While condition=\"" + ctx.getChild(2).getText().replaceAll(">", "&gt;").replaceAll("<", "&lt;") + "\" >";
	}
	
	@Override public void exitWhileStmt(SmileyBoiParser.WhileStmtContext ctx) { 
		xmlString += "</While>";
	}
	
	@Override public void enterDoWhileStmt(SmileyBoiParser.DoWhileStmtContext ctx) {
		xmlString += "<DoWhile condition=\"" + ctx.getChild(5).getText().replaceAll(">", "&gt;").replaceAll("<", "&lt;") + "\" >";
	}
	
	@Override public void exitDoWhileStmt(SmileyBoiParser.DoWhileStmtContext ctx) { 
		xmlString += "</DoWhile>";
	}
	
	@Override public void enterForLoopStmt(SmileyBoiParser.ForLoopStmtContext ctx) {
		xmlString += "<ForLoop variable=\"" + ctx.getChild(2).getChild(ctx.getChild(2).getChildCount() -1).getText() + "\"";
		xmlString += " condition=\"" + ctx.getChild(4).getText().replaceAll(">", "&gt;").replaceAll("<", "&lt;") + "\"";
		xmlString += " calculation=\"" + ctx.getChild(6).getText() + "\">";
	}
	
	@Override public void exitForLoopStmt(SmileyBoiParser.ForLoopStmtContext ctx) {
		xmlString += "</ForLoop>";
	}
	
	@Override public void enterForEachStmt(SmileyBoiParser.ForEachStmtContext ctx) {
		xmlString += "<ForEach createdVariable=\"" + ctx.getChild(2).getText() + "\"";
		xmlString += " fromVarialbe=\"" + ctx.getChild(4).getText() + "\">";
	}
	
	@Override public void exitForEachStmt(SmileyBoiParser.ForEachStmtContext ctx) {
		xmlString += "</ForEach>";
	}
	
	public void createXML(String filename) throws Exception {
		// So here we can use the document builder to parse the string that has been created
		// while walking the tree.
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
		System.out.println(xmlString);
		// This is to make sure the output XML is tabulated, because no one likes a long ass string.
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(builder.parse(new InputSource(new StringReader(xmlString))));
		FileWriter fw = new FileWriter(new File(filename));
		StreamResult sr = new StreamResult(fw);
		transformer.transform(source, sr);
		System.out.println("Saved XML document to: \n" + filename + "\n\n");
	}


}
 