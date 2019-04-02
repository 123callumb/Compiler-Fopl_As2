package compiler;


import org.antlr.v4.runtime.ParserRuleContext;

import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;
import smiley.SmileyBoiBaseListener;
import smiley.SmileyBoiParser;

public class Searcher extends SmileyBoiBaseListener {
	
	private String searchValue;
	private String returnedAreas = "";	
	private boolean customSearchVal, buildingCustomSearch = false;
	private TreeItem<String> currentTree;
	private String currentRule; // For when we are building the tree we need to know which tree we are currently inside of.
	String[] ruleNames = SmileyBoiParser.ruleNames; // Get all the rules to string
	
	public Searcher(String value, boolean custom) {
		searchValue = value;
		customSearchVal = custom;
		currentTree = new TreeItem<String>("Results");
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
	
	public TreeItem<String> getResults() {
		currentTree.setExpanded(true);
		if(!(currentTree.getChildren().size() > 0)) {
			Alert noneFound = new Alert(AlertType.WARNING);
			noneFound.setTitle("Oh no!");
			noneFound.setContentText("There are no rules in this script with that name :(");
			noneFound.showAndWait();
		}
		return currentTree;
	}
	
	// This method is amazing especially for a custom search because it gets called every time any single rule is called!
	@Override public void enterEveryRule(ParserRuleContext ctx) {
		
		// Checking here if the search value is equal to a parser rule name and allow adding to the string
		if(ruleNames[ctx.getRuleIndex()].equals(searchValue)) {
			buildingCustomSearch = true;
		}
		
		if(buildingCustomSearch) {
			TreeItem<String> newCurrent = new TreeItem<String>(ruleNames[ctx.getRuleIndex()]);
			newCurrent.getChildren().add(new TreeItem<String>("^: " + ctx.getText()));
			currentTree.getChildren().add(newCurrent);
			currentTree = newCurrent;
		}
		
	}

	@Override public void exitEveryRule(ParserRuleContext ctx) { 
		if(currentTree.getValue().equals(ruleNames[ctx.getRuleIndex()])) {
			currentTree = currentTree.getParent();
		}
		if(ruleNames[ctx.getRuleIndex()].equals(searchValue)) {
			buildingCustomSearch = false;
			currentRule = "";
		}
	}
	
	private void getElementText(String ctxType, String ctxString) {
		if(searchValue.equals(ctxType)) {
			returnedAreas += ctxString + "\n";
		}
	}

}
