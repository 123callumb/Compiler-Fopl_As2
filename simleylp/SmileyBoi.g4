grammar SmileyBoi;

@header {
	package smiley;
}

fileCompilation  
	: '(:' codename globalItems* mainCodeBlock ':)'
	;        

//	My functions and global variables are read before the actual main block is ran
//	Therefore functions are NOT allowed to be created in the main method, only called.	
globalItems
	: assign #GlobalAssignment
	| functionDeclare #FunctionsOutsideOfMainBlock
	;

//	Identify the name of the script you're writing	
codename 
	: SCRIPTNAME identifier ENDLN
	; 

//	This is the main method
mainCodeBlock
	: 'Smile' RARROW codeBlock
	;

codeBlock
 	: LCB codeBlockStmt* RCB
	;

codeBlockReturn
	: LCB codeBlockReturnStmt* RCB
	;

//	I created two blocks one with return because the main method doesn't need to return anything and void methods don't.		
codeBlockStmt
	: assign
	| ifStmt
	| iterationStmt
	| functionCall
	| printStmt
	;
	
codeBlockReturnStmt
	: codeBlockStmt
	| returnStmt
	;
	
returnStmt
	: RETURN LARROW expr ENDLN
	;

//	If statement including else if and else.
ifStmt
	:'?' conditionBlock (RARROW conditionBlock)* (RARROW codeBlockReturn)?  
	;

//	I have allowed returning inside of the iteration because most languages do it...
iterationStmt
	: WHILE RARROW expr RARROW codeBlockReturn #WhileStatement
	| WHILE RARROW codeBlockReturn LARROW expr ENDLN #DoWhileStatement 
	| FOR RARROW assign COL expr? COL expr RARROW codeBlockReturn #ForStatement
	| FOR RARROW assign IN expr RARROW codeBlockReturn #ForInStatement 
	;
	
conditionBlock
	: LPAR expr RPAR RARROW codeBlockReturn
	;
	
printStmt
	: PRINT
	| PRINT STRING ENDLN
	;
	
expr
	: expr (MUL | DIV | MOD) expr #Multi
	| expr (ADD | SUB) expr #Additio
	| expr PWR expr #Power
	| expr (GREATER | GREATER EQUALS | LESS | LESS EQUALS) expr #Relational
	| expr (EQUALS EQUALS | NOT EQUALS) expr #EqualAndNotEqual
	| NOT expr #not 
	| expr AND expr #forTwoExpr
	| expr OR expr #choiceExpr
	| expr DEC #DecreaseVal
	| expr INC #IncreaseVal
	| value #valueOfExpr
	;

//	If the function doesn't have any arguments then you can just call its name
functionCall
	: identifier (LPAR arguments RPAR)? ENDLN?
	;

//	If the function doesn't need arguments then you don't have to put them in.
//	The type of code block written will determine if it returns (contains a return), like in JavaScript.
functionDeclare
	: FUNCIDENT RARROW identifier RARROW (LPAR params RPAR)? (codeBlock | codeBlockReturn)
	;

arguments
	: expr (',' expr)*
	;
	
params
	: identifier (',' identifier)*
	;
		
value 
	: variable #Variables
	| literal #LiteralValues
	| functionCall #ValueReturnedFromFunction
	| arrayList #ArrayStructures
	| arrayIndex #ValueAtArrayIndex
	;
	
// For init with expressions or size
arrayList
	: LCB (expr (',' expr)*)? RCB
	| LSB DIGIT+ RSB
	;

arrayIndex
	: variable LSB DIGIT+ RSB
	;

literal
	: CHAR
	| STRING
	| BOOL
	| FLOAT
	;

//	Assign like Const str jeff <- 'JEFF'~
assign 
	: CONSTANT? variable (ADD | SUB | DIV | MUL)? LARROW expr ENDLN
	| arrayIndex (ADD | SUB | DIV | MUL)? LARROW expr ENDLN
	;

	
//	Variables look a little like flt myVariable
variable
	: (FLOAT_ID | BOOL_ID | STRING_ID) (LSB RSB)? identifier
	; 
	
//	So i can stick to a naming scheme that always starts with a letter then followed by numbers or letters.
identifier
	: IDENT (IDENT | DIGIT | UNDERSCORE)*
	;
	
// IDENTIFIABLE TOKENS:
ASSIGNTO : 	LARROW;
ENDLN:		TIL;

//	LITERALS
CHAR: SQUOTE IDENT SQUOTE;
STRING : DQUOTE (ESC|.)*? DQUOTE;
BOOL: TRUE
	| FALSE
	;
FLOAT: DIGIT+ POINT DIGIT* 
 	 | POINT DIGIT+
 	 ;
 	
//	KEYWORDS
SCRIPTNAME : 'Script';
TRUE :		 'yes';
FALSE :		 'no';
WHILE :		 'During';
FUNCIDENT :  'f)';
RETURN :	 'ret)';
FOR :		 'Go';
IN : 		 'In';
CONSTANT : 	 'Const';
PRINT:		 'P->';
FLOAT_ID:	 'flt';
BOOL_ID:	 'bln';
STRING_ID:	 'str'; 

//	SEPARATORS
LPAR : 		'(';
RPAR : 		')';
LSB : 		'[';
RSB : 		']';
LCB :		'{';
RCB :		'}';	
SCOL : 		';';
POINT : 	'.';
COL : 		':';
SQUOTE : 	'\'';
DQUOTE :	'"';
TIL:		'~';

//	OPERATORS
PWR : 		'^';
MUL : 		'*';
ADD : 		'+';
SUB : 		'-';
DIV : 		'/';
MOD : 		'%';
LESS : 		'<';
GREATER : 	'>';
NOT : 		'!';
EQUALS : 	'=';
LARROW: 	'<-';
RARROW:		'->';
AND:		'&';
OR:			'|';
INC:	    '++';
DEC:		'--';

//	WHITE SPACE + COMMENTS
WS : 		[ \t\r\n]+ -> skip ; 
COMMENT: 	'(-:' .*? ':-)' -> channel(HIDDEN);
DOLLAR: 	'$';
UNDERSCORE:	'_';


//	FRAGMENT RULES These used to have the words fragment infront of them, they now don't because the compiler wouldn't pick it up?
IDENT: [a-zA-Z];
DIGIT:[0-9];
ESC: '\\"' 	//	For escaping string literals like \n etc.
			| '\\\\'
			; 