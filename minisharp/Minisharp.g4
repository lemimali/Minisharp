grammar Minisharp ;

FULLCOMMENT : '/*' .*? '*/' -> skip ;

ROWCOMMENT : '//' ~[\r\n]* -> skip ; 

WS : [ \t\n\r]+ -> skip ;

ID : [\p{XID_Start}] [\p{XID_Continue}]* ;

CONST : [0-9]+ ;

start : 'Main(' (paramlist)? ')' '{' block '}' ;

block : (stmt)* ;

paramlist : param (',' param)* ;

param : type ID ;

type : 'int'     		# typeInt
	 | 'intsequence'	# typeIntSequence
     | 'double' 		# typeDouble
     | 'boolean' 		# typeBool
     ;

decl : type ID '=' expr ';'								# declExpr
     | type'[]' ID '=' '{' CONST (',' CONST)* '}' ';'	# declArray
     ;

assign : ID '=' expr ';'						# assignExpr
	   | ID '=' '{' CONST (',' CONST)* '}' ';'	# assignArray
	   ; 

iterator : ID op=( '++' | '--' ) ;
     
expr : ID                               # varExpr
     | ID '[' expr ']'     				# indexExpr
     | ID '.length'                     # lengthExpr
     | CONST                            # constExpr
     | '(' expr ')'                     # parenExpr
     | expr op=( '/' | '*' ) expr       # mulExpr
     | expr op=( '+' | '-' ) expr       # addExpr
     | expr op='<' expr                 # lessthanExpr
     | expr op='>' expr					# greaterthanExpr
     | expr op='==' expr				# equalExpr
     ;
     
ifstmt : 'if' '('expr')' '{' block '}' ('else' '{' block '}')? ;

forstmt : 'for' '(' decl expr ';' iterator ')' '{' block '}' ;

stmt : decl					# stmtDecl
	 | assign				# stmtAssign
	 | ifstmt				# stmtIfstmt
	 | forstmt				# stmtForstmt
	 | 'return' expr ';'	# stmtReturn
	 ;