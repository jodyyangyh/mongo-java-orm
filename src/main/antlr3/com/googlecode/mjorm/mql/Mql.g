
grammar Mql;
 
tokens {
  	SEMI_COLON	= ';' ;
	SINGLE_QUOTE	= '\'' ;
	DOUBLE_QUOTE	= '\"' ;
	BACK_SLASH	= '\\' ;
	FORWARD_SLASH	= '/' ;
	EQUALS		= '=' ;
	NOT_EQUALS	= '!=';
	COMMA		= ',' ;
  	GT		= '>' ;
  	LT		= '<' ;
  	LT_GT		= '<>' ;
  	GT_EQUALS	= '>=' ;
	LT_EQUALS	= '<=' ;
  	L_PAREN		= '(' ;
  	R_PAREN		= ')' ;
  	MATCHES		= '=~' ;
  	DOT		= '.';
  	FROM		= 'from' ;
  	WHERE		= 'where' ;
  	SKIP		= 'skip' ;
  	LIMIT		= 'limit' ;
  	NOT		= 'not' ;
}

@header {
	package com.googlecode.mjorm.mql;
}

@lexer::header {
	package com.googlecode.mjorm.mql;
}


/**
 * PARSER RULES
 */
 
start
	: command (command)* EOF
	;

command
	: FROM collection_name (WHERE command_criteria)? (pagination)? (SEMI_COLON)?
	;
	
collection_name
	: SCHEMA_IDENTIFIER
	;
	
function_name
	: SCHEMA_IDENTIFIER
	;

command_criteria
	: (criteria_group | criteria)+
	;

criteria
	: (criterion | negated_criterion)
	;

pagination
	: ((LIMIT first_document) | (LIMIT first_document COMMA number_of_documents))
	;

first_document
	: NUMBER
	;

number_of_documents
	: NUMBER
	;
	
criteria_group
	: function_name L_PAREN (group_criteria)* R_PAREN
	;
	
group_criteria
	: criterion (COMMA criterion)*
	;

criterion
	: (function_criterion | compare_criterion)
	;
	
compare_criterion
	: field_name comparison_operator variable_literal
	;

function_criterion
	: field_name function_name L_PAREN (function_args)* R_PAREN
	;

function_args
	: variable_literal (COMMA variable_literal)*
	;

function_argument
	: variable_literal
	;

negated_criterion
	: (NOT criterion)
	;

comparison_operator
	: (MATCHES | EQUALS | NOT_EQUALS | LT_GT | GT | LT | GT_EQUALS | LT_EQUALS)
	;

variable_literal
	: (regex | string | number)
	;

regex
	: REGEX
	;

string
	: (DOUBLE_QUOTED_STRING | SINGLE_QUOTED_STRING)
	;
	
number
	: (NUMBER | DECIMAL)
	;

field_name
	: SCHEMA_IDENTIFIER
	;

/**
 * LEXER RULES
 */

fragment HEX_DIGIT
	: (DIGIT | 'a'..'f' | 'A'..'F')
	;

fragment DIGIT
	: '0'..'9'
	;
	
NUMBER
	: DIGIT+
	;

DECIMAL
	: NUMBER (DOT NUMBER).
	;
	
SCHEMA_IDENTIFIER
	: ('a'..'z' | 'A'..'Z' | '0'..'9' | '.' | '$' | '_' )+
	;

REGEX
	: FORWARD_SLASH (ESCAPE | ~(BACK_SLASH | FORWARD_SLASH))* FORWARD_SLASH
	;
	
DOUBLE_QUOTED_STRING @init { final StringBuilder buf = new StringBuilder(); }
	: DOUBLE_QUOTE (ESCAPE_EVALED[buf] | i = ~(BACK_SLASH | DOUBLE_QUOTE) { buf.appendCodePoint(i); })* DOUBLE_QUOTE { setText(buf.toString()); }
	;
	    
SINGLE_QUOTED_STRING @init { final StringBuilder buf = new StringBuilder(); }
	: SINGLE_QUOTE (ESCAPE_EVALED[buf] | i = ~(BACK_SLASH | SINGLE_QUOTE) { buf.appendCodePoint(i); })* SINGLE_QUOTE { setText(buf.toString()); }
	;

fragment ESCAPE_EVALED[StringBuilder buf]
	:
	    '\\'
	    ( 
	    	'n'    		{buf.append("\n");}
	        | 'r'   	{buf.append("\r");}
	        | 't'    	{buf.append("\t");}
	        | 'b'    	{buf.append("\b");}
	        | 'f'    	{buf.append("\f");}
	        | '"'    	{buf.append("\"");}
	        | '\''   	{buf.append("\'");}
	        | FORWARD_SLASH {buf.append("/"); }
	        | BACK_SLASH   	{buf.append("\\");}
	        | 'u' i=HEX_DIGIT j=HEX_DIGIT k=HEX_DIGIT l=HEX_DIGIT   {setText(i.getText()+j.getText()+k.getText()+l.getText());}
	    )
	;

fragment ESCAPE
	: '\\' ( 'n' | 'r' | 't' | 'b' | 'f' | '"' | '\'' | FORWARD_SLASH | BACK_SLASH | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT)
	;

WHITESPACE
	: ( '\t' | ' ' | '\r' | '\n' )+ {skip();}
	;
