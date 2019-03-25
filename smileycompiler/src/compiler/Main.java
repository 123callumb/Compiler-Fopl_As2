package compiler;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.*;

import smiley.*;

@SuppressWarnings("deprecation")
public class Main {

	private static String fileName;
	
	public static void main(String[] args) throws Exception {
		
		ANTLRInputStream input = new ANTLRInputStream(setInputFile());
		SmileyBoiLexer lexer = new SmileyBoiLexer(input);
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		SmileyBoiParser parser = new SmileyBoiParser(tokens);
		ParseTree tree = parser.fileCompilation();
		ParseTreeWalker walker = new ParseTreeWalker();
		// For cleaning my XML parser is in here. 
		XMLWorker xmlBuilder = new XMLWorker();
		walker.walk(xmlBuilder, tree); 
		
		xmlBuilder.createXML(fileName.replace(".", "-") + "-output.xml");
	}
	
	
	private static FileInputStream setInputFile() throws Exception {
		System.out.println("Please enter the name of your chosen script file:");
		System.out.println("Example Files: test1.smile, test2.smile and test3.smile\n");
		
		Scanner scan = new Scanner(System.in);
		fileName = scan.next();
		File currentFile = new File(fileName);
		while(!currentFile.exists()) {
			System.out.println("File not found! Make sure the file is in the working directory...\n");
			fileName = scan.next();
			currentFile = new File(fileName);
		}
		
		scan.close();
		System.out.println("\nFile found! Walking Tree...\n");
		FileInputStream file = new FileInputStream(fileName);
		return file;
	}

}
