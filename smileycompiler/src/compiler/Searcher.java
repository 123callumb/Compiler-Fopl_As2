package compiler;


import smiley.SmileyBoiBaseListener;
import smiley.SmileyBoiParser;

public class Searcher extends SmileyBoiBaseListener {
	
	private String searchValue;
	private String returnedAreas = "";	
	
	public Searcher(String value) {
		searchValue = value;
	}
	
	@Override public void enterFunctionDeclare(SmileyBoiParser.FunctionDeclareContext ctx) { 
		getElementText("Functions", ctx.getText());
	}
	
	@Override public void enterAssign(SmileyBoiParser.AssignContext ctx) { 
		getElementText("Variable Assignment", ctx.getText());
	}
	
	@Override public void enterIterationStmt(SmileyBoiParser.IterationStmtContext ctx) { 
		getElementText("Iterations", ctx.getText());
	}
	
	@Override public void enterConditionBlock(SmileyBoiParser.ConditionBlockContext ctx) { 
		getElementText("Conditions", ctx.getText());
	}
	
	@Override public void enterPrintStmt(SmileyBoiParser.PrintStmtContext ctx) {
		getElementText("Print Statements", ctx.getText());
	}
	
	public String getResults() {
		return returnedAreas;
	}
	
	private void getElementText(String ctxType, String ctxString) {
		if(searchValue.equals(ctxType)) {
			returnedAreas += ctxString + "\n";
		}
	}

}
