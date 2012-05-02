
grammar Mql;
 
tokens {
	SINGLE_QUOTE	= '\'' ;
	DOUBLE_QUOTE	= '\"' ;
	EQUALS		= '=' ;
	COMMA		= ',' ;
  	GT		= '>' ;
  	LT		= '<' ;
  	OPEN_PAREN	= '(' ;
  	CLOSE_PAREN	= ')' ;
  	DOT		= '.';
  	FROM		= 'from' ;
  	WHERE		= 'where' ;
  	SKIP		= 'skip' ;
  	LIMIT		= 'limit' ;
  	SEMI_COLON	= ';' ;
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
	: FROM collection_name (criteria)* (pagination)? (SEMI_COLON)?
	;

collection_name
	: SCHEMA_IDENTIFIER
	;

criteria
	: WHERE field_assignment+
	;
	
pagination
	: ((LIMIT first_document) | (LIMIT first_document COMMA number_of_documents)).
	;

first_document
	: NUMBER
	;

number_of_documents
	: NUMBER
	;

field_assignment
	: field_name EQUALS field_value
	;

field_value
	: (string | NUMBER)
	;
		
string
	: (DOUBLE_QUOTED_STRING | SINGLE_QUOTED_STRING)
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
    
DOUBLE_QUOTED_STRING @init { final StringBuilder buf = new StringBuilder(); }
	: '"' ( ESCAPE[buf] | i = ~( '\\' | '"' ) { buf.appendCodePoint(i); })* '"' { setText(buf.toString()); }
	;
	    
SINGLE_QUOTED_STRING @init { final StringBuilder buf = new StringBuilder(); }
	: '\'' ( ESCAPE[buf] | i = ~( '\\' | '\'' ) { buf.appendCodePoint(i); })* '\'' { setText(buf.toString()); }
	;

fragment ESCAPE[StringBuilder buf]
	:
	    '\\'
	    ( 
	        | 'r'    {buf.append("\r");}
	        | 't'    {buf.append("\t");}
	        | 'b'    {buf.append("\b");}
	        | 'f'    {buf.append("\f");}
	        | '"'    {buf.append("\"");}
	        | '\''   {buf.append("\'");}
	        | '/'    {buf.append("/");}
	        | '\\'   {buf.append("\\");}
	        | ('u')+ i=HEX_DIGIT j=HEX_DIGIT k=HEX_DIGIT l=HEX_DIGIT   {buf.append("Fix:"+i.getText()+j.getText()+k.getText()+l.getText());}

	    )
	;

WS
	: ( '\t' | ' ' | '\r' | '\n' )+ {skip();}
	;

/*
    from collectionName
            where
                    field=val
                    field2 in(val, val, val)
                    field3 > x
                    field4 < y
                    some.deep.field between(val, val)
                    or(
                            field=val,
                            field.anus=val
                    )
                    and(
                            field=val,
                            field.anus=val
                    )
            ACTION
            skip X
            limit y
     
    ACTIONS
    select *
    select a1, a2.b2
    select a1
    update ???
    delete
     
    {
            a1: "test",
            a2: {
                    b1: "test",
                    b2: {
                            c1: "test"
                    }
            }
    }

*/