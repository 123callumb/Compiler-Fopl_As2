package compiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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
import javafx.scene.control.ToolBar;
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
	
	private static void Compile(TextArea xmlArea, TextArea javaScriptArea) throws Exception {
		XMLWorker xmlBuilder = new XMLWorker();
		WalkCurrentTree(xmlBuilder);
		xmlBuilder.createXML(xmlArea);
	}
	
	
	private static void Search(String value, TextArea searchResults, ListView<String> searchCalcs) throws IOException {
		Searcher search = new Searcher(value);
		WalkCurrentTree(search);
		searchResults.clear();
		searchResults.setText(search.getResults());
	}
	
	private static void WalkCurrentTree(SmileyBoiBaseListener type) throws IOException {
		ANTLRInputStream input = new ANTLRInputStream(currentFile);
		SmileyBoiLexer lexer = new SmileyBoiLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SmileyBoiParser parser = new SmileyBoiParser(tokens);
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
		mainStage.setWidth(1340);
		mainStage.setHeight(720);
		mainStage.setResizable(false);
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
		TextArea searchResult = new TextArea();
		Label varLabel = new Label("Variables:");
		ListView<String> variableCalculations = new ListView<String>();
		Label returnLabel = new Label("Program Returns = (Press 'Run' to Calculate)");
		VBox searchMenu = new VBox(searchBoxLabel, searchBox, searchButton, searchLabel, searchResult, varLabel, variableCalculations, returnLabel);
		searchMenu.setPadding(new Insets(20));
		
		// These are the menu button
		Button newFile = new Button("New File");
		Button loadFile = new Button("Load File");
		Button compileBut = new Button("Parse");
		Button runBut = new Button("Run Script");
		
		
		ToolBar menuBar = new ToolBar(newFile, loadFile, compileBut, runBut);
		HBox contentBox = new HBox(searchMenu, codePad, xmlResult, javascriptResult);	
		VBox mainHolder = new VBox(menuBar, contentBox);
		
		
		// Button actions here
		newFile.setOnAction(value -> {
			codePad.setText("(:\n\tScript new_smile_script~ \n\n\tSmile -> {\n\t\tP->\"Hello World\"~\n\t}\n :)");
		});
		
		loadFile.setOnAction(value -> {
			FileChooser chooseBox = new FileChooser();
			chooseBox.getExtensionFilters().add(new FileChooser.ExtensionFilter("Smile Scripts", "*.smile"));
			File selectedFile = chooseBox.showOpenDialog(mainStage);
			Scanner loadscan;
			try {
				loadscan = new Scanner(selectedFile).useDelimiter("\n");
				codePad.clear();
				while(loadscan.hasNext()) {
					codePad.appendText(loadscan.next() + "\n");
				}
			} catch (FileNotFoundException e) {
				System.err.println("Could not find file!");
			}

		});
		
		compileBut.setOnAction(value -> {
			try {
				setCurrentFile(codePad.getText());
				Compile(xmlResult, javascriptResult);
			} catch (Exception e) {
				System.err.println("Failure Compiling Script :( \n\nError:" + e);
			}
		});
		
		searchButton.setOnAction(value -> {
			try {
				if(searchBox.getValue() != null) {
					setCurrentFile(codePad.getText());
					Search(searchBox.getValue(), searchResult, variableCalculations);
				}

			} catch (Exception e) {
				System.err.println("Error searching for element!");
			}
		});
		
		Scene mainScene = new Scene(mainHolder, mainStage.getWidth(), mainStage.getHeight());
		mainStage.setScene(mainScene);
		

	}

}
