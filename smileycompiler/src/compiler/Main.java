package compiler;

import java.io.BufferedWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToolBar;

import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import smiley.*;

@SuppressWarnings("deprecation")
public class Main extends Application {

	private static FileInputStream currentFile;
	
	public static void main(String[] args) throws Exception {
		Application.launch(args);
	}
	
	private static void Compile(TextArea smileArea, TextArea xmlArea, TextArea javaScriptArea, TextArea errorConsole) throws Exception {
				
		// XML Walker
		XMLWorker xmlBuilder = new XMLWorker();
		WalkCurrentTree(xmlBuilder, errorConsole);
		xmlBuilder.createXML(xmlArea);
		
		// Set the current file again as it gets closed!
		setCurrentFile(smileArea.getText());
		
		// JS Walker
		JSWorker jsBuilder = new JSWorker();
		WalkCurrentTree(jsBuilder, errorConsole);
		jsBuilder.writeJStoUI(javaScriptArea);
	
	}
	
	
	private static void Search(String value, boolean custom, boolean isVariable, TreeView<String> searchResults, TextArea errorConsole) throws IOException {
		Searcher search = new Searcher(value, custom, isVariable);
		WalkCurrentTree(search, errorConsole);
		searchResults.setRoot(search.getResults());
	}
	
	private static void CalculateVariables(ListView<String> searchCalcs, TextArea errorConsole) throws IOException {
		VariableCalculator varCal = new VariableCalculator();
		WalkCurrentTree(varCal, errorConsole);
		searchCalcs.getItems().clear();
		varCal.addScannedVariables(searchCalcs);
	}
	
	private static void WalkCurrentTree(SmileyBoiBaseListener type, TextArea errorConsole) throws IOException {
		ANTLRInputStream input = new ANTLRInputStream(currentFile);
		SmileyBoiLexer lexer = new SmileyBoiLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		ErrorHandler errorListener = new ErrorHandler(errorConsole);
		SmileyBoiParser parser = new SmileyBoiParser(tokens);
		// Add custom error listener here
		parser.addErrorListener(errorListener);
		ParseTree tree = parser.fileCompilation();
		ParseTreeWalker walker = new ParseTreeWalker();
		walker.walk(type, tree); 
	}
	
	
	private static void setCurrentFile(String padText) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter("temp-smile.temp"));
		writer.write(padText);
		writer.close();
		currentFile = new FileInputStream("temp-smile.temp");	
	}

	// This method is absolute hell but If i get time I'll clean it up!!!!!
	@SuppressWarnings("resource") // For file loading errors
	@Override
	public void start(Stage mainStage) throws Exception {
		
		// This is going to start to get messy but once i have the basics i will clean it up
		// when i know how the data is passed through the application.
		mainStage.setTitle("Smiley Boi Parser/Compiler - FOP");
		mainStage.setMinWidth(1340);
		mainStage.setMinHeight(720);
		mainStage.show();
		
		
		// This is the bit where you can write or load code in.
		
		TextArea codePad = new TextArea("Write your script here!");
		codePad.setMinHeight(675);
		TextArea xmlResult = new TextArea("XML Result");
		TextArea javascriptResult = new TextArea("JavaScript Result");
		
		// This is the search menu bit
		Label searchBoxLabel = new Label("Search for Elements");
		ComboBox<String> searchBox = new ComboBox<String>();
		String[] elementChoices = {"Functions", "Variable Assignment", "Iterations", "Conditions", "Print Statements"};
		searchBox.getItems().addAll(elementChoices);
		Button searchButton = new Button("Search");
		Label searchLabel = new Label("Elements Found");
		TreeView<String> searchResult = new TreeView<String>();
		searchResult.setMaxHeight(240);
		Label varLabel = new Label("Variables:");
		ListView<String> variableCalculations = new ListView<String>();
		variableCalculations.setMaxHeight(200);
//		Label returnLabel = new Label("Program Returns = (Press 'Run' to Calculate)");
		Label errorConsoleLabel = new Label("Error Console:");
		TextArea errorConsole = new TextArea();
		errorConsole.setWrapText(true);
		errorConsole.setStyle("-fx-text-fill: #F15D5E;");
		errorConsole.setEditable(false);
		Button clearConsole = new Button("Clear");
		Label customSearch = new Label("Custom Search:");
		Button searchElement = new Button("Search Element");
		Button searchVar = new Button("Search Variable");
		HBox customSearchBox = new HBox(searchElement, searchVar);
		customSearchBox.setSpacing(10);
		VBox searchMenu = new VBox(searchBoxLabel, searchBox, searchButton, customSearch, customSearchBox, searchLabel, searchResult, varLabel, variableCalculations, errorConsoleLabel, errorConsole, clearConsole);
		searchMenu.setPadding(new Insets(20));
		searchMenu.setSpacing(10);
		
		// These are the menu button
		Button newFile = new Button("New File");
		Button loadFile = new Button("Load File");
		Button compileBut = new Button("Parse");
		Button runBut = new Button("Run Script");
		Button saveSmile = new Button("Save");
		Button saveXML = new Button("Save XML");
		Button saveJS = new Button("Save JavaScript");
		Button rulesButton = new Button("View Parser Rules");
		
		
		ToolBar menuBar = new ToolBar(newFile, loadFile, compileBut, runBut, saveSmile, saveXML, saveJS, rulesButton);
		HBox contentBox = new HBox(searchMenu, codePad, xmlResult, javascriptResult);	
		VBox mainHolder = new VBox(menuBar, contentBox);
		
		// Save/load dialogue
		FileChooser chooseBox = new FileChooser();
		chooseBox.getExtensionFilters().add(new FileChooser.ExtensionFilter("Smile Scripts", "*.smile"));
		chooseBox.getExtensionFilters().add(new FileChooser.ExtensionFilter("JavaScript", "*.js"));
		chooseBox.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML File", "*.xml"));
		
		// Button actions here
		newFile.setOnAction(value -> {
			codePad.setText("(:\n\tScript new_smile_script~ \n\n\tSmile -> {\n\t\tP->\"Hello World\"~\n\t}\n :)");
		});
		
		loadFile.setOnAction(value -> {
			chooseBox.setSelectedExtensionFilter(chooseBox.getExtensionFilters().get(0));
			File selectedFile = chooseBox.showOpenDialog(mainStage);
			Scanner loadscan;
			try {
				loadscan = new Scanner(selectedFile).useDelimiter("\n");
				codePad.clear();
				while(loadscan.hasNext()) {
					codePad.appendText(loadscan.next() + "\n");
				}
			} catch (FileNotFoundException e) {
				errorConsole.appendText("Could not find file!");
			}

		});
		
		compileBut.setOnAction(value -> {
			try {
				setCurrentFile(codePad.getText());
				Compile(codePad, xmlResult, javascriptResult, errorConsole);
			} catch (Exception e) {
				errorConsole.appendText("Failure Compiling Script :( \n\nError:" + e);
				System.err.print(e);
			}
		});
		
		searchButton.setOnAction(value -> {
			try {
				if(searchBox.getValue() != null) {
					setCurrentFile(codePad.getText());
					Search(searchBox.getValue(), false, false, searchResult, errorConsole);
				}
			} catch (Exception e) {
				errorConsole.appendText("Error searching for element!");
			}
		});
		
		runBut.setOnAction(value -> {
			try {
				setCurrentFile(codePad.getText());
				CalculateVariables(variableCalculations, errorConsole);
			}catch(Exception e) {
				errorConsole.appendText("Error calculation varables." + e);
			}
		});
		
		saveSmile.setOnAction(value -> {
			chooseBox.setSelectedExtensionFilter(chooseBox.getExtensionFilters().get(0));
			File destFile = chooseBox.showSaveDialog(mainStage);
			SaveFile(destFile, codePad, chooseBox, errorConsole);
		});
		
		saveXML.setOnAction(value -> {
			chooseBox.setSelectedExtensionFilter(chooseBox.getExtensionFilters().get(2));
			File destFile = chooseBox.showSaveDialog(mainStage);
			SaveFile(destFile, xmlResult, chooseBox, errorConsole);
		});
		
		saveJS.setOnAction(value -> {
			chooseBox.setSelectedExtensionFilter(chooseBox.getExtensionFilters().get(1));
			File destFile = chooseBox.showSaveDialog(mainStage);
			SaveFile(destFile, javascriptResult, chooseBox, errorConsole);
		});
		
		clearConsole.setOnAction(value -> {
			errorConsole.clear();
		});
		
		searchElement.setOnAction(value -> {
			TextInputDialog tid = new TextInputDialog("ifStmt");
			tid.setTitle("Custom Element Search");
			tid.setHeaderText("Search for Parser Rule");
			Optional<String> res = tid.showAndWait();
			if(res.isPresent()) {
				try {
					setCurrentFile(codePad.getText());
					Search(res.get(), true, false, searchResult, errorConsole);
				}catch(Exception e) {
					errorConsole.appendText("Error doing custom search: " + e);
				}

			}
		});
		
		searchVar.setOnAction(value -> {
			TextInputDialog tid = new TextInputDialog("Variable Name");
			tid.setTitle("Custom Variable Search");
			tid.setHeaderText("Search for Variable in Current Script.");
			Optional<String> res = tid.showAndWait();
			if(res.isPresent()) {
				try {
					setCurrentFile(codePad.getText());
					Search(res.get(), true, true, searchResult, errorConsole);
				}catch(Exception e) {
					
				}
			}
		});
		
		rulesButton.setOnAction(value -> {
			Stage rulesStage = new Stage();
			rulesStage.setTitle("Element Parser Rule Names");
			ListView<String> allRules = new ListView<String>();
			allRules.getItems().addAll(SmileyBoiParser.ruleNames);
			Scene ruleScene = new Scene(allRules);
			ruleScene.getStylesheets().add("compiler/UIStyle.css");
			rulesStage.setScene(ruleScene);
			rulesStage.show();
		});
		
		Scene mainScene = new Scene(mainHolder, mainStage.getWidth(), mainStage.getHeight());
		mainScene.getStylesheets().add("compiler/UIStyle.css");
		mainStage.setScene(mainScene);

	}
	
	private void SaveFile(File fileChosen, TextArea area, FileChooser chooseBox, TextArea errorBox) {
		chooseBox.setInitialFileName("untitled");
		if(fileChosen != null) {
			try {
				FileWriter fw = new FileWriter(fileChosen);
				fw.write(area.getText());
				fw.close();
			} catch (IOException e) {
				errorBox.appendText("Could not save smile script " + e);
			}
		}
	}

}
