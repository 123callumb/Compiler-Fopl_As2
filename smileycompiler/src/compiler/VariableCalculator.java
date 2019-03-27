package compiler;

import java.util.ArrayList;

import javafx.scene.control.ListView;
import smiley.SmileyBoiBaseListener;
import smiley.SmileyBoiParser;
import smiley.SmileyBoiParser.ExprContext;

public class VariableCalculator extends SmileyBoiBaseListener {
	
	ArrayList<Variable> variables = new ArrayList<Variable>();
	int currentVariableIndex;
	
	
	@Override public void enterAssign(SmileyBoiParser.AssignContext ctx) { 
		
		// Record data that is not an array.
		if(ctx.getChildCount() != 1) {
			
			// Variable Name
			String name = ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 1).getText();
			// Variable Expr
			ExprContext exprValue = (ExprContext) ctx.getChild(ctx.getChildCount() - 1);
			
			// check to see if the variable exists already
			currentVariableIndex = findVariableIndex(name);
			
			if(currentVariableIndex != -1) {
				
				// modify the variable if it exists
				variables.get(currentVariableIndex).modifyValue(exprValue);
				
			}else{
				
				// Create the variable as it doesn't exist
				Variable newVariable = new Variable(name);
				
				// Pass the current expression context to the variable.
				newVariable.setAssignedExpr(exprValue); // This is like my own custom listener I guess :P
				
				// Add to list of variables
				variables.add(newVariable);
				
				// Set the current variable index or else the expression
				// method wont know what to amend the expression to.
				currentVariableIndex = findVariableIndex(name);
				 
			}
					
		}else{ // deal with arrays
			System.out.println("Skipping array calculations for now.");
		}
	}
	
	public void addScannedVariables(ListView<String> variableList) {
		variables.forEach(var -> variableList.getItems().add(var.getName() + " = " + var.getValueAsText()));
	}
	
	private int findVariableIndex(String name) {
		boolean found = false;
		int index = 0;
		while(!found && variables.size() < index) {
			found = variables.get(index++).getName().equals(name) ? true : false;	
		}
		return found ? index : -1;
	}

}
