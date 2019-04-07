package compiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

import javafx.scene.control.TextArea;
import smiley.SmileyBoiBaseListener;
import smiley.SmileyBoiParser;
import smiley.SmileyBoiParser.*;

public class XMLWorker extends SmileyBoiBaseListener {
	
	StringBuilder xmlString = new StringBuilder("<?xml version=\"1.0\" encoding=\"utf-8\"?>"); // Idk why use String builder when this works just fine?
	
	@Override public void enterFileCompilation(FileCompilationContext ctx) {
		xmlString.append("<Script ");
		xmlString.append("name=\"" + ctx.getChild(1).getChild(1).getText() + "\"");
		xmlString.append(">");
		
	}
	
	@Override public void exitFileCompilation(FileCompilationContext ctx) { 
		xmlString.append("</Script>");
	}
	
	@Override public void enterMainCodeBlock(SmileyBoiParser.MainCodeBlockContext ctx) {
		xmlString.append("<Main>");
	}
	
	@Override public void exitMainCodeBlock(SmileyBoiParser.MainCodeBlockContext ctx) {
		xmlString.append("</Main>");
	}
	
	@Override public void enterAssign(SmileyBoiParser.AssignContext ctx) {
			xmlString.append("<Var");
			
			// Here we can see if assignment is calling a function.
			boolean isCallingFunc = ctx.getChild(ctx.getChildCount() - 1).getChild(0).getChild(0).getChild(0) != null && ctx.getChild(ctx.getChildCount() - 1).getChild(0).getChild(0).getChild(0).getText().equals("->");

			//Then it's an array
			if(ctx.getChildCount() == 1) {
				xmlString.append(">");
			}else { // If it's a regular variable
				
				int typePos = ctx.getChild(0).getChild(0) != null && ctx.getChild(0).getChild(0).getText().equals("Const") ? 1 : 0; 
				if(ctx.getChild(0).getChildCount() != 1) { // Have to check in case it's a reassign to an existing variable.
					xmlString.append(" type=\"" + ctx.getChild(0).getChild(typePos).getText() + "\"");
					xmlString.append(" const=\"" + (typePos == 0 ? "false" : "true") + "\"");
				}
				// So this is just getting the name correct based on array variables and stuff.
				String name = ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 1).getText(); 
				xmlString.append(" name=\"");
				xmlString.append(name + "\"");
				xmlString.append(ctx.getChild(1).getText().matches("\\+|\\*|-|/|\\^") ? " action=\"" + ctx.getChild(1).getText() + "\">" : ">");
				
				//Then the var value can go here
				xmlString.append(ctx.getChildCount() != 1 && !isCallingFunc ? ctx.getChild(ctx.getChildCount() -1).getText() : "");
				
			}
		
	}
	
	@Override public void enterAssignArray(SmileyBoiParser.AssignArrayContext ctx) { 
		xmlString.append( "<AssignArray>");
	}
	
	@Override public void exitAssignArray(SmileyBoiParser.AssignArrayContext ctx) {xmlString.append( "</AssignArray>"); }
	@Override public void enterArrayAssign(SmileyBoiParser.ArrayAssignContext ctx) { 
		xmlString.append("<ArrayAssign arrayName=\"" + ctx.getChild(0).getText() + "\" index=\"" + ctx.getChild(2).getText() + "\">");
		xmlString.append(ctx.getChild(ctx.getChildCount() - 1).getText());
	}
	@Override public void exitArrayAssign(SmileyBoiParser.ArrayAssignContext ctx) {  xmlString.append("</ArrayAssign>");}
	@Override public void enterArrayInitial(SmileyBoiParser.ArrayInitialContext ctx) {  
		int count = ctx.getChildCount();
		String name = ctx.getChild(count - 1).getText().equals('}') ? ctx.getChild(count - 3).getText() : ctx.getChild(count - 1).getText();
		String type = ctx.getChild(0).equals("Const") ? ctx.getChild(1).getText() : ctx.getChild(0).getText();
		xmlString.append("<ArrayInitial arrayName=\"" + name + "\" type=\"" + type + "\">");	
	}
	
	@Override public void exitArrayInitial(SmileyBoiParser.ArrayInitialContext ctx) {  xmlString.append("</ArrayInitial>"); }
	
	@Override public void exitAssign(SmileyBoiParser.AssignContext ctx) {
		xmlString.append("</Var>");
	}
	
	@Override public void enterPrintStmt(SmileyBoiParser.PrintStmtContext ctx) {
		String logVal =  ctx.getChild(1).getText();
		xmlString.append("<Print>");
		xmlString.append(logVal);
	}
	
	@Override public void exitPrintStmt(SmileyBoiParser.PrintStmtContext ctx) { 
		xmlString.append("</Print>");
	}
	
	@Override public void enterIfStmt(SmileyBoiParser.IfStmtContext ctx) { 
		xmlString.append("<ConditionBlock>");
	}
	
	@Override public void exitIfStmt(SmileyBoiParser.IfStmtContext ctx) {
		xmlString.append("</ConditionBlock>");
	}
	
	@Override public void enterIfCondition(SmileyBoiParser.IfConditionContext ctx) {
		xmlString.append("<If");
		xmlString.append(" condition=\"" + ctx.getChild(1).getChild(1).getText().replaceAll(">", "&gt;").replaceAll("<", "&lt;") + "\">");
	}
	
	@Override public void exitIfCondition(SmileyBoiParser.IfConditionContext ctx) {
		xmlString.append("</If>");
	}
	
	@Override public void enterElseStmt(SmileyBoiParser.ElseStmtContext ctx) { 
		xmlString.append("<Else>");
	}
	
	@Override public void exitElseStmt(SmileyBoiParser.ElseStmtContext ctx) { 
		xmlString.append("</Else>");
	}
	
	@Override public void enterElseIfStmt(SmileyBoiParser.ElseIfStmtContext ctx) { 
		xmlString.append("<ElseIf ");
		xmlString.append(" condition=\"" + ctx.getChild(1).getChild(1).getText().replaceAll(">", "&gt;").replaceAll("<", "&lt;") + "\">");
	}
	
	@Override public void exitElseIfStmt(SmileyBoiParser.ElseIfStmtContext ctx) { 
		xmlString.append("</ElseIf>");
	}
		
	@Override public void enterFunctionDeclare(SmileyBoiParser.FunctionDeclareContext ctx) { 
		xmlString.append("<Function");
		xmlString.append(" identity=\"" + ctx.getChild(2).getText() + "\"");
		xmlString.append(ctx.getChild(4).getText().equals("(") ? "" : ">");
	}
	
	@Override public void exitFunctionDeclare(SmileyBoiParser.FunctionDeclareContext ctx) { 
		xmlString.append("</Function>");
	}
	
	@Override public void enterParams(SmileyBoiParser.ParamsContext ctx) { 
		for(int i = 0; i < ctx.getChildCount(); i += 2) { // we add two so we skip the comma
			String paramName = ctx.getChild(i).getText();
			xmlString.append(" paramName_" + (i/2) + "=\"" + paramName + "\"");
		}
		xmlString.append( ctx.getChildCount() > 0 ? ">" : "");
	}
	
	@Override public void enterFunctionCall(SmileyBoiParser.FunctionCallContext ctx) { 
		xmlString.append("<FuncCall identity=\"");
		xmlString.append(ctx.getChild(1).getText() + "\"");
		xmlString.append(ctx.getChildCount() > 2 && ctx.getChild(2).getText().equals("(") ? "" : ">");
	}
	
	@Override public void exitFunctionCall(SmileyBoiParser.FunctionCallContext ctx) { 
		xmlString.append("</FuncCall>");
	}
	
	@Override public void enterArguments(SmileyBoiParser.ArgumentsContext ctx) {
		for(int i = 0; i < ctx.getChildCount(); i += 2) { // we add two so we skip the comma
			String argValue = ctx.getChild(i).getText();
			xmlString.append(" argument_" + (i/2) + "=\"" + argValue + "\"");
		}
		xmlString.append( ctx.getChildCount() > 0 ? ">" : "");
	}
	
	@Override public void enterIterationStmt(SmileyBoiParser.IterationStmtContext ctx) { xmlString.append("<Iteration>"); }
	@Override public void exitIterationStmt(SmileyBoiParser.IterationStmtContext ctx) { xmlString.append("</Iteration>"); }
	
	@Override public void enterWhileStmt(SmileyBoiParser.WhileStmtContext ctx) {
		xmlString.append("<While condition=\"" + ctx.getChild(2).getText().replaceAll(">", "&gt;").replaceAll("<", "&lt;") + "\" >");
	}
	
	@Override public void exitWhileStmt(SmileyBoiParser.WhileStmtContext ctx) { 
		xmlString.append("</While>");
	}
	
	@Override public void enterDoWhileStmt(SmileyBoiParser.DoWhileStmtContext ctx) {
		xmlString.append("<DoWhile condition=\"" + ctx.getChild(5).getText().replaceAll(">", "&gt;").replaceAll("<", "&lt;") + "\" >");
	}
	
	@Override public void exitDoWhileStmt(SmileyBoiParser.DoWhileStmtContext ctx) { 
		xmlString.append("</DoWhile>");
	}
	
	@Override public void enterForLoopStmt(SmileyBoiParser.ForLoopStmtContext ctx) {
		xmlString.append("<ForLoop variable=\"" + ctx.getChild(2).getChild(ctx.getChild(2).getChildCount() -1).getText() + "\"");
		xmlString.append(" condition=\"" + ctx.getChild(4).getText().replaceAll(">", "&gt;").replaceAll("<", "&lt;") + "\"");
		xmlString.append(" calculation=\"" + ctx.getChild(6).getText() + "\">");
	}
	
	@Override public void exitForLoopStmt(SmileyBoiParser.ForLoopStmtContext ctx) {
		xmlString.append("</ForLoop>");
	}
	
	@Override public void enterForEachStmt(SmileyBoiParser.ForEachStmtContext ctx) {
		xmlString.append("<ForEach createdVariable=\"" + ctx.getChild(2).getText() + "\"");
		xmlString.append(" fromVarialbe=\"" + ctx.getChild(4).getText() + "\">");
	}
	
	@Override public void exitForEachStmt(SmileyBoiParser.ForEachStmtContext ctx) {
		xmlString.append("</ForEach>");
	}
	
	@Override public void enterGlobalItems(SmileyBoiParser.GlobalItemsContext ctx) {
//		System.out.print("We have found some global items " + ctx.getText());
	}
	
	@Override public void enterReturnStmt(SmileyBoiParser.ReturnStmtContext ctx) { 
		xmlString.append("<Return>" + ctx.getChild(2).getText() + "</Return>");
	}
	
	public void createXML(TextArea xmlArea) throws Exception {
		// So here we can use the document builder to parse the string that has been created
		// while walking the tree.
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = dbf.newDocumentBuilder();
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer transformer = tf.newTransformer();
//		System.out.println(xmlString);
		// This is to make sure the output XML is tabulated, because no one likes a long ass string.
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		DOMSource source = new DOMSource(builder.parse(new InputSource(new StringReader(xmlString.toString()))));
		File xmlFile = new File("temp-smile-xml.temp");
		FileWriter fw = new FileWriter(xmlFile);
		StreamResult sr = new StreamResult(fw);
		transformer.transform(source, sr);
		writeToUI(xmlArea, xmlFile);
	}
	
	private void writeToUI(TextArea xmlArea, File xmlFile) throws IOException {
		
		try(Scanner loadscan = new Scanner(xmlFile).useDelimiter("\n")){
			xmlArea.clear();
			while(loadscan.hasNext()) {
				xmlArea.appendText(loadscan.next() + "\n");
			}
		}catch(IOException e) {
			System.err.print("Error printing xml to ui");
		}

	}


}
 