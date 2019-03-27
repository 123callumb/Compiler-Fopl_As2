package compiler;

import smiley.SmileyBoiParser.ExprContext;

public class Variable {
	
	// Basically this is like a class for sorting out floats, strings and bools
	// I'm making it now so, it may be useful later on, if not then that's fine too!
	
	// Name of variable recognised by program
	private String name;
	private String stringValue;
	private ExprContext expr;
	
	public Variable(String varName) {
		name = varName;
	}
	
	public String getName() {
		return name;
	}
	
	public void setAssignedExpr(ExprContext exprData) {
		expr = exprData;
		
		stringValue = expr.getText();
		
		if(exprData.getChildCount() == 1) {
			
		}
	}
	
	public void modifyValue(ExprContext exprData) {
		stringValue += " + " + exprData;
	}
	
	
	public String getValueAsText() {
		return stringValue;
	}
	
	
	
	
}
