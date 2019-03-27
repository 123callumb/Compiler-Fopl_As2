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
	: assign ENDLN//GlobalAssignment
	| functionDeclare //FunctionsOutsideOfMainBlock
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
	: assign ENDLN
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
	: RETURN ASSIGNTO expr ENDLN
	;

//	If statement including else if and else.
ifStmt
	: ifCondition elseIfStmt* elseStmt?  
	;

ifCondition
	: IF conditionBlock
	;

elseIfStmt
	: RARROW conditionBlock
	;

elseStmt 
	: RARROW codeBlockReturn
	;
	
//	I have allowed returning inside of the iteration because most languages do it...
iterationStmt
	: whileStmt   //WhileStatement
	| doWhileStmt //DoWhileStatement 
	| forLoopStmt //ForStatement
	| forEachStmt //ForInStatement 
	;

whileStmt
	: WHILE RARROW expr RARROW codeBlockReturn ENDLN
	;

doWhileStmt 
	: WHILE RARROW codeBlockReturn LARROW expr ENDLN
	;

forLoopStmt
	: FOR RARROW assign COL expr COL expr RARROW codeBlockReturn ENDLN
	;

forEachStmt
	: FOR RARROW identifier IN expr RARROW codeBlockReturn ENDLN
	;

	
conditionBlock
	: LPAR expr RPAR RARROW codeBlockReturn
	;
	
printStmt
	: PRINT STRING ENDLN
	;
	
expr
	: expr (MUL | DIV | MOD) expr //Multi
	| expr (ADD | SUB) expr //Additio
	| expr PWR expr //Power
	| expr (GREATER | GREATER EQUALS | LESS | LESS EQUALS) expr //Relational
	| expr (EQUALS EQUALS | NOT EQUALS) expr //EqualAndNotEqual
	| NOT expr //not 
	| expr AND expr //forTwoExpr
	| expr OR expr //choiceExpr
	| expr DEC //DecreaseVal
	| expr INC //IncreaseVal
	| LPAR expr RPAR // So we can use parens
	| value //valueOfExpr
	;

//	If the function doesn't have any arguments then you can just call its name
functionCall
	: RARROW identifier (LPAR arguments RPAR)? ENDLN?
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
	: identifier //Variables can't do var or that redefines the variable.
	| literal //LiteralValues
	| functionCall //ValueReturnedFromFunction
	| arrayList //ArrayStructures
	| arrayIndex //ValueAtArrayIndex
	;
	
// For init with expressions or size
arrayList
	: LCB (expr (',' expr)*)? RCB
	;

assignArray // var <- arrayName[i]
	: var ASSIGNTO identifier LSB (DIGIT | expr)+ RSB
	;

arrayInitial // Const type[] name <- {x, x}
	: CONSTANT? (FLOAT_ID | BOOL_ID | STRING_ID) LSB RSB identifier (ASSIGNTO arrayList)?
	;

arrayAssign // array[i] <- x
	: identifier LSB (DIGIT | expr)+ RSB ASSIGNTO expr
	;

arrayIndex
	: identifier LSB (DIGIT | expr)+ RSB
	;
	
literal
	: CHAR
	| STRING
	| BOOL
	| FLOAT
	;

//	Assign like Const str jeff <- 'JEFF'~
assign 
	: var (ADD | SUB | DIV | MUL)? ASSIGNTO expr 
	| assignArray
	| arrayInitial
	| arrayAssign 
	;

	
//	Variables look a little like flt myVariable
var
	: CONSTANT? (FLOAT_ID | BOOL_ID | STRING_ID) (LSB (DIGIT | expr)? RSB)? identifier
	| identifier
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
 	 | DIGIT+ 
 	 ;
 	
//	KEYWORDS
SCRIPTNAME : 'Script';
IF	:		 '?';
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
//CALL: 		 'Call';

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