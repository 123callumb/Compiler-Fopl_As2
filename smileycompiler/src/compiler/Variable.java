package compiler;

import java.util.ArrayList;
import java.util.Stack;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import smiley.SmileyBoiParser.ExprContext;

public class Variable {
	
	// Basically this is like a class for sorting out floats, strings and bools
	// I'm making it now so, it may be useful later on, if not then that's fine too!
	
	// Name of variable recognised by program
	private String name;
	private String stringValue;
	private ExprContext expr;
	Stack<String> expressionStack = new Stack<String>();
	
	public Variable(String varName) {
		name = varName;
	}
	
	public String getName() {
		return name;
	}
	
	public void setAssignedExpr(ExprContext exprData, ArrayList<Variable> currentVariables) {
		expr = exprData;
		stringValue = expr.getText();
		
		// First we must replace all variable names found in the expression with their current value.
		for(int v = 0; v< currentVariables.size(); v++) {
			// These are strings because they are parsed later on with the script engine!
			String varName = currentVariables.get(v).getName();
			String varValue = currentVariables.get(v).getValueAsText();
			stringValue = stringValue.contains(varName) ? stringValue.replaceAll(varName, varValue) : stringValue;
		}	
		
		// Check here if string or boolean
		if(expr.getChildCount() == 1 && (expr.getChild(0).getText().endsWith("\"") || expr.getChild(0).equals("yes") || expr.getChild(0).getText().equals("no"))) {
			
		}else {
			try {
				ScriptEngineManager mgr = new ScriptEngineManager();
				ScriptEngine js = mgr.getEngineByName("JavaScript");
				Double result = (Double) js.eval(stringValue);
				stringValue = String.valueOf(result);
			} catch (ScriptException e) {
				// TODO Auto-generated catch block
				System.err.println("JS Calc error" + e);
			}
		}
	}
	
	public void modifyValue(ExprContext exprData) {
		stringValue += " + " + exprData;
	}
	
	
	public String getValueAsText() {
		return stringValue;
	}
	
	
	
	
}
