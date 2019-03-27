package compiler;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

import javafx.scene.control.TextArea;

public class ErrorHandler extends BaseErrorListener {
	
	private TextArea errorUI;
	
	public ErrorHandler(TextArea errorLog) {
		errorUI = errorLog;
	}
	
	@Override
	public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
		errorUI.appendText("Parse Error on line " + line + " at col " + charPositionInLine + " " + msg + "\n");
	}
	
	
}
