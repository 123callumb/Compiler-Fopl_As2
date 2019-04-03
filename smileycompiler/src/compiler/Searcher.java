package compiler;


import org.antlr.v4.runtime.ParserRuleContext;

import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import javafx.scene.control.Alert.AlertType;
import smiley.SmileyBoiBaseListener;
import smiley.SmileyBoiParser;

public class Searcher extends SmileyBoiBaseListener {
	
	private String searchValue;
	private boolean buildingCustomSearch = false, isVar = false;
	
	// For when we are building the tree we need to know which tree we are currently inside of.
	private TreeItem<String> currentTree; 
	
	// Get all the rules to string
	private String[] ruleNames = SmileyBoiParser.ruleNames; 
	
	public Searcher(String value, boolean custom, boolean isVariable) {
		searchValue = value;
		isVar = isVariable;
		// If the search is from the combo box then set it to its actual rule name
		if(!custom) {
			String[] elementChoices = {"Functions", "Variable Assignment", "Iterations", "Conditions", "Print Statements"};
			String[] ruleChoices = {"functionDeclare", "assign", "iterationStmt", "ifStmt", "printStmt"};
			for(int i = 0; i < elementChoices.length; i++) {
				searchValue = searchValue.equals(elementChoices[i]) ? ruleChoices[i] : searchValue;
			}
		}
		currentTree = new TreeItem<String>("Results");
	}
	
	/* Here we are just returning the result and adding it to the search list view.
	 * If we end up returning a tree with no children then we know that the searched value
	 * does is not valid or does not exist it the script so we can tell the user that with
	 * an alert box.
	 */ 
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
		
		// When searching for a variable we check the actual context text not rule name
		if(isVar) {
			if(ruleNames[ctx.getRuleIndex()].equals("assign")) {
				String name = ctx.getChild(0).getChild(ctx.getChild(0).getChildCount() - 1).getText();
				if(name.equals(searchValue)) {
					currentTree.getChildren().add(new TreeItem<String>(ctx.getText()));
				}
			}
		}else { 
			// Here we are checking the search value to the rules that are present in the script.
			// If there is one we open up the tree and start creating the sub tree.
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

		
	}

	@Override public void exitEveryRule(ParserRuleContext ctx) { 
		// Here we are closing off the subtree when we exit a rule.
		// If the rule is the one we searched for initially then the current tree is completely closed.
		if(!isVar) {
			
			if(currentTree.getValue().equals(ruleNames[ctx.getRuleIndex()])) {
				currentTree = currentTree.getParent();
			}
			if(ruleNames[ctx.getRuleIndex()].equals(searchValue)) {
				buildingCustomSearch = false;
			}
		}
	}
	

}
