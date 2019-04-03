package compiler;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import javafx.scene.control.TextArea;
import smiley.SmileyBoiBaseListener;
import smiley.SmileyBoiParser;
import smiley.SmileyBoiParser.AssignContext;

public class JSWorker extends SmileyBoiBaseListener {
	
	String JSString = "";
	STGroup jsTemplateG;
	
	public JSWorker() {
		jsTemplateG = new STGroupFile("./jsSTG.stg");
	}
	
	
	@Override public void enterMainCodeBlock(SmileyBoiParser.MainCodeBlockContext ctx) { JSString += "function Main(){\n"; }
	@Override public void exitMainCodeBlock(SmileyBoiParser.MainCodeBlockContext ctx) { JSString += "}\n\nMain();"; }
	
	@Override public void enterAssign(SmileyBoiParser.AssignContext ctx) {
			
			// Here we can see if assignment is calling a function.
			boolean isCallingFunc = ctx.getChild(ctx.getChildCount() - 1).getChild(0).getChild(0).getChild(0) != null && ctx.getChild(ctx.getChildCount() - 1).getChild(0).getChild(0).getChild(0).getText().equals("->");
			
			//Then it's an array
			if(ctx.getChildCount() == 1) {

			}else { // If it's a regular variable
				
				int typePos = ctx.getChild(0).getChild(0).getText().equals("Const") ? 1 : 0; 
				if(ctx.getChild(0).getChildCount() != 1) { // Have to check in case it's a reassign to an existing variable.
			
					// JS reassign var
					JSString += typePos == 1 ? "const " : "var ";
				}
				// So this is just getting the name correct based on array variables and stuff.
				String name = ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 1).getText(); 
				
				//Then the var value can go here
				
				//JS implement 
				JSString += name + " ";
				// account for the operator assign ability
				String operator = (ctx.getChild(1).getText().matches("\\+|\\*|-|/|\\^") ? ctx.getChild(1).getText() : "");
				JSString += ctx.getChildCount() != 1 && !isCallingFunc ? operator + "= " + ctx.getChild(ctx.getChildCount() -1).getText().replace("{", "[").replace("}", "]").replace("yes", "true").replace("no", "false") : "";
				JSString += isCallingFunc ? " = " : ";\n";
			}
		
	}
	
	@Override public void enterAssignArray(SmileyBoiParser.AssignArrayContext ctx) { 
		
	}
	
	@Override public void enterArrayAssign(SmileyBoiParser.ArrayAssignContext ctx) { 
		JSString += ctx.getChild(0).getText() + "[" + ctx.getChild(2).getText() + "] = " + ctx.getChild(ctx.getChildCount() - 1).getText() + ";\n"; 
	}
	@Override public void enterArrayInitial(SmileyBoiParser.ArrayInitialContext ctx) {  
		int count = ctx.getChildCount();
		String name = ctx.getChild(count - 1).getText().equals('}') ? ctx.getChild(count - 3).getText() : ctx.getChild(count - 1).getText();
//		String type = ctx.getChild(0).equals("Const") ? ctx.getChild(1).getText() : ctx.getChild(0).getText();
		JSString += "var " + name + " = " + " [];\n";
	}
	
	@Override public void exitAssign(SmileyBoiParser.AssignContext ctx) {
		
	}
	
	@Override public void enterPrintStmt(SmileyBoiParser.PrintStmtContext ctx) {
		String logVal =  ctx.getChild(1).getText();
		ST inst = jsTemplateG.getInstanceOf("print");
		inst.add("v", logVal);
		JSString += inst.render() + "\n";
	}
	
	@Override public void enterIfCondition(SmileyBoiParser.IfConditionContext ctx) {
		String ifCondition = ctx.getChild(1).getChild(1).getText();
		ST inst = jsTemplateG.getInstanceOf("ifStatement");
		inst.add("condition", ifCondition);
		JSString += inst.render() + "\n";
	}
	
	@Override public void exitIfCondition(SmileyBoiParser.IfConditionContext ctx) { JSString += "}\n"; }
	@Override public void enterElseStmt(SmileyBoiParser.ElseStmtContext ctx) { JSString += "else {\n"; }
	@Override public void exitElseStmt(SmileyBoiParser.ElseStmtContext ctx) { JSString += "}\n"; }
	
	@Override public void enterElseIfStmt(SmileyBoiParser.ElseIfStmtContext ctx) { 
		String ifCondition = ctx.getChild(1).getChild(1).getText();
		ST inst = jsTemplateG.getInstanceOf("elseIfStatement");
		inst.add("eCondition", ifCondition);
		JSString += inst.render() + "\n";
	}
	
	@Override public void exitElseIfStmt(SmileyBoiParser.ElseIfStmtContext ctx) { JSString += "}\n"; }
		
	@Override public void enterFunctionDeclare(SmileyBoiParser.FunctionDeclareContext ctx) { 
		String funcName = ctx.getChild(2).getText();
		String args = ctx.getChild(4).getText().equals("(") ? ctx.getChild(5).getText() : "";
		ST inst = jsTemplateG.getInstanceOf("functionDeclare");
		inst.add("name", funcName);
		inst.add("args", args);
		JSString += inst.render() + "\n";
//		JSString += "function " + ctx.getChild(2).getText(); + "(";
//		JSString += ctx.getChild(4).getText().equals("(") ? ""  : "){\n";
	}
	
	@Override public void exitFunctionDeclare(SmileyBoiParser.FunctionDeclareContext ctx) { JSString += "}\n\n"; }
	
	@Override public void enterParams(SmileyBoiParser.ParamsContext ctx) { 
//		for(int i = 0; i < ctx.getChildCount(); i += 2) { // we add two so we skip the comma
//			String paramName = ctx.getChild(i).getText();
//			JSString += ctx.getChildCount() - 1 == i ? paramName : paramName + ", ";
//		}
//		JSString += ctx.getChildCount() > 0 ? "){\n" : "";
	}
	
	@Override public void enterFunctionCall(SmileyBoiParser.FunctionCallContext ctx) { 
		String funcName = ctx.getChild(1).getText();
		String args = ctx.getChildCount() > 2 && ctx.getChild(2).getText().equals("(") ? ctx.getChild(3).getText() : "";
		ST inst = jsTemplateG.getInstanceOf("functionCall");
		inst.add("callName", funcName);
		inst.add("callArg", args);
		JSString += inst.render();
//		JSString += ctx.getChild(1).getText() + "(";
//		JSString += ctx.getChildCount() > 2 && ctx.getChild(2).getText().equals("(") ? "" : ")";
	}
	
	@Override public void exitFunctionCall(SmileyBoiParser.FunctionCallContext ctx) { 
		// Only if we're ending the line should we add an end line in js too.
		JSString += ctx.getChild(ctx.getChildCount() - 1).getText().equals("~") ? ";\n" : "";
	}
	
	@Override public void enterArguments(SmileyBoiParser.ArgumentsContext ctx) {
//		for(int i = 0; i < ctx.getChildCount(); i += 2) { // we add two so we skip the comma
//			String argValue = ctx.getChild(i).getText();
//			JSString += ctx.getChildCount() - 1 == i ? argValue : argValue + ", ";
//		}
//		JSString += ctx.getChildCount() > 0 ? ")" : "";
	}
	
	@Override public void enterWhileStmt(SmileyBoiParser.WhileStmtContext ctx) {
		String whileCond = ctx.getChild(2).getText();
		ST inst = jsTemplateG.getInstanceOf("whileStmt");
		inst.add("whileCond", whileCond);
		JSString += inst.render() + "{\n";
	}
	
	@Override public void exitWhileStmt(SmileyBoiParser.WhileStmtContext ctx) { JSString += "}\n"; }
	@Override public void enterDoWhileStmt(SmileyBoiParser.DoWhileStmtContext ctx) { JSString += "do{\n"; }
	
	@Override public void exitDoWhileStmt(SmileyBoiParser.DoWhileStmtContext ctx) { 
		String whileCond = ctx.getChild(5).getText();
		ST inst = jsTemplateG.getInstanceOf("whileStmt");
		inst.add("whileCond", whileCond);
		JSString += "}" + inst.render() + ";\n";
	}
	
	@Override public void enterForLoopStmt(SmileyBoiParser.ForLoopStmtContext ctx) {		
//		JSString += "for(";
		
		AssignContext asi = (AssignContext) ctx.getChild(2);
		String assign = new String();
		if(asi.getChildCount() != 1) {
			assign = "var " + asi.getChild(0).getChild(asi.getChild(0).getChildCount() - 1).getText() + "= " + asi.getChild(asi.getChildCount() - 1).getText();
		}else{
			assign = ctx.getChild(4).getText();
		}
		String cond = ctx.getChild(4).getText();
		String oper = ctx.getChild(6).getText();
				
		ST inst = jsTemplateG.getInstanceOf("forLoop");
		inst.add("fAssign", assign);
		inst.add("fCond", cond);
		inst.add("fOper", oper);
		JSString += inst.render() + "\n";
		
//		JSString += ";" +  + ";" +  + "){\n";
	}
	
	@Override public void exitForLoopStmt(SmileyBoiParser.ForLoopStmtContext ctx) { JSString += "}\n"; }
	
	@Override public void enterForEachStmt(SmileyBoiParser.ForEachStmtContext ctx) {
		String element = ctx.getChild(2).getText();
		String array = ctx.getChild(4).getText();
		ST inst = jsTemplateG.getInstanceOf("forIn");
		inst.add("fElement", element);
		inst.add("fArray", array);
		JSString += inst.render() + "\n";
	}
	
	@Override public void exitForEachStmt(SmileyBoiParser.ForEachStmtContext ctx) { JSString += "}\n"; }
	
	@Override public void enterReturnStmt(SmileyBoiParser.ReturnStmtContext ctx) {
		String retVal = ctx.getChild(2).getText();
		ST inst = jsTemplateG.getInstanceOf("returnVal");
		inst.add("retVal", retVal);
		JSString += inst.render() + "\n";
	}
	
	
	
	public void writeJStoUI(TextArea jsUI) {
		jsUI.clear();
		// I try to pretty the javascript here
		String[] JSLines = JSString.split("\n");
		String currentTabs = "";
		jsUI.appendText(JSLines[0] + "\n");
		for(int l = 1; l < JSLines.length; l++) {
			//This removes the variable created under the for loop
			JSLines[l] = JSLines[l - 1].replaceAll("\t", "").startsWith("for(") && !JSLines[l-1].contains("in") ?  "" : JSLines[l];
			// So this is checking the previous line to see if it is a { or } and will format the tabulation.
			currentTabs = (JSLines[l-1].contains("{")) ? currentTabs + "\t"  : JSLines[l].contains("}") ? currentTabs.substring(0, currentTabs.length() - 1) : currentTabs;
			//Then here we just append it to the ui
			if(!JSLines[l].equals("")) {
				jsUI.appendText(currentTabs + JSLines[l] + "\n");
			}
		}
	}
}
