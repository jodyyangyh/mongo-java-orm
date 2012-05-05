
grammar Mql;

options {
	output			= AST;
	ASTLabelType	= CommonTree;
}

tokens {
  SEMI_COLON    = ';';
  STAR          = '*';
  BACK_SLASH    = '\\';
  FORWARD_SLASH = '/';
  EQUALS        = '=';
  NOT_EQUALS    = '!=';
  COMMA         = ',';
  MINUS         = '-';
  GT            = '>';
  LT            = '<';
  LT_GT         = '<>';
  GT_EQUALS     = '>=';
  LT_EQUALS     = '<=';
  L_PAREN       = '(';
  R_PAREN       = ')';
  L_BRACKET     = '[';
  R_BRACKET     = ']';
  MATCHES       = '=~';
  DOT           = '.';
  TRUE          = 'true';
  FALSE         = 'false';
  FROM          = 'from';
  WHERE         = 'where';
  SKIP          = 'skip';
  LIMIT         = 'limit';
  NOT           = 'not';
  SELECT        = 'select';
  DELETE        = 'delete';
  UPDATE        = 'update';
  EXPLAIN       = 'explain';
  HINT          = 'hint';
  NATURAL       = 'natural';
  ATOMIC        = 'atomic';
  INC           = 'inc';
  UPSERT        = 'upsert';
  MULTI         = 'multi';
  UNSET         = 'unset';
  SET           = 'set';
  POP           = 'pop';
  SHIFT         = 'shift';
  PUSH          = 'push';
  EACH          = 'each';
  PULL          = 'pull';
  RENAME        = 'rename';
  BITWISE       = 'bitwise';
  SORT          = 'sort';
  ASC           = 'asc';
  DESC          = 'desc';
  RETURN        = 'return';
  NEW           = 'new';
  OLD           = 'old';
  OR            = 'or';
  AND           = 'and';
  ALL			= 'all';
  FIND_AND_MODIFY	= 'find and modify';
  FIND_AND_DELETE	= 'find and delete';
  ADD_TO_SET		= 'add to set';

  ARRAY;
  ADD_TO_SET_EACH;
  PUSH_ALL;
  PULL_ALL;
}

@header {
	package com.googlecode.mjorm.mql;
}

@lexer::header {
	package com.googlecode.mjorm.mql;
}

/** start **/
start
	: command (command)* EOF!
	;

/** command **/
command
	: FROM collection_name (WHERE c+=criteria (COMMA? c+=criteria)*)? action SEMI_COLON? -> ^(collection_name ^($c+? action))
	;

/** criteria **/

criteria
	: (criterion_group | negated_criterion | criterion)
	;

criterion
	: (function_criterion | compare_criterion)
	;
	
negated_criterion
	: NOT^ criterion
	;

criterion_group
	: function_name^ L_PAREN! (criteria (COMMA!? criteria) | variable_literal)* R_PAREN!
	;
	
compare_criterion
	: field_name^ comparison_operator variable_literal
	;

function_criterion
	: field_name^ function_call
	;

/** hint **/
hint
	: HINT^ (hint_natural | hint_index_name | (hint_field (COMMA!? hint_field)*))
	;

hint_natural
	: NATURAL
	;

hint_index_name
	: string
	;

hint_field
	: field_name direction
	;

/** action **/
action
	: (select_action | explain_action | delete_action | update_action | fam_action | fad_action)
	;

// explain
explain_action
	: EXPLAIN hint?
	;

// select
select_action
	: SELECT^ select_fields hint? sort? pagination?
	;

select_fields
	: (STAR | (field_name (COMMA!? field_name)*))
	;

pagination
	: ((LIMIT^ first_document) | (LIMIT^ first_document COMMA! number_of_documents))
	;

first_document
	: INTEGER
	;

number_of_documents
	: INTEGER
	;

// find and modify
fam_action
	: UPSERT? FIND_AND_MODIFY^ fam_return? update_operations SELECT select_fields sort?
	;

fam_return
	: (RETURN^ (NEW | OLD))
	;
	
// find and delete
fad_action
	: FIND_AND_DELETE^ select_fields? sort?
	;

// delete
delete_action
	: ATOMIC? DELETE
	;

// update
update_action
	: ATOMIC? (UPDATE^ | UPSERT^) MULTI? update_operations
	;
	
update_operations
	: update_operation (COMMA? update_operation)*
	;

update_operation
	: (
		operation_inc
		| operation_set 
		| operation_unset 
		| operation_push 
		| operation_push_all 
		| operation_add_to_set
		| operation_add_to_set_each
		| operation_pop
		| operation_shift
		| operation_pull
		| operation_pull_all
		| operation_rename
		| operation_bitwise
	)
	;

operation_inc
	: INC^ field_name number
	;
		
operation_set
	: SET^ field_name EQUALS! variable_literal
	;
	
operation_unset
	: UNSET^ field_name
	;
	
operation_push
	: PUSH^ field_name variable_literal
	;
			
operation_push_all
	: PUSH ALL field_name array -> ^(PUSH_ALL field_name array)
	;
			
operation_add_to_set_each
	: ADD_TO_SET field_name EACH array -> ^(ADD_TO_SET_EACH field_name array)
	;

operation_add_to_set
	: ADD_TO_SET^ field_name array
	;
		
operation_pop
	: POP^ field_name variable_literal
	;
	
operation_shift
	: SHIFT^ field_name variable_literal
	;
		
operation_pull
	: PULL^ field_name variable_literal
	;

operation_pull_all
	: PULL ALL field_name array -> ^(PULL_ALL field_name array)
	;

operation_rename
	: RENAME^ field_name field_name
	;

operation_bitwise
	: BITWISE^ (OR | AND) field_name INTEGER
	;
	
/** sort **/
sort
	: SORT sort_field (COMMA? sort_field)*
	;

sort_field
	: field_name direction
	;

/** general **/

collection_name
	: SCHEMA_IDENTIFIER
	;
		
field_name
	: SCHEMA_IDENTIFIER
	;

function_name
	: SCHEMA_IDENTIFIER | ALL | OR | AND
	;

comparison_operator
	: (MATCHES | EQUALS | NOT_EQUALS | LT_GT | GT | LT | GT_EQUALS | LT_EQUALS)
	;

variable_literal
	: (regex | string | bool | number | array)
	;

function_call
	: function_name^ L_PAREN! variable_list? R_PAREN!
	;
	
variable_list
	: variable_literal (COMMA! variable_literal)*
	;

integer
	: (SIGNED_INTEGER | INTEGER)
	;

decimal
	: (SIGNED_DECIMAL | DECIMAL)
	;

number
	: (HEX_NUMBER | integer | decimal)
	;
	
direction
	: (ASC | DESC)
	;

array
	: L_BRACKET variable_list? R_BRACKET -> ^(ARRAY variable_list)
	;

regex
	: REGEX
	;

string
	: (DOUBLE_QUOTED_STRING | SINGLE_QUOTED_STRING)
	;

bool
	: (TRUE | FALSE)
	;

/**
 * LEXER RULES
 */

fragment HEX_DIGIT
	: ('0'..'9' | 'a'..'f' | 'A'..'F')
	;

fragment DIGIT
	: ('0'..'9')
	;

fragment SINGLE_QUOTE
  : '\''
  ;

fragment DOUBLE_QUOTE
  : '\"'
  ;

INTEGER
	: DIGIT+
	;

SIGNED_INTEGER
	: MINUS? DIGIT+
	;

HEX_NUMBER
	: '0' 'x' HEX_DIGIT+
	;

DECIMAL
	: INTEGER (DOT INTEGER)?
	;
	
SIGNED_DECIMAL
	: SIGNED_INTEGER (DOT INTEGER)?
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
	: '\'' (ESCAPE_EVALED[buf] | i = ~(BACK_SLASH | SINGLE_QUOTE) { buf.appendCodePoint(i); })* SINGLE_QUOTE { setText(buf.toString()); }
	;

fragment ESCAPE_EVALED[StringBuilder buf]
	: '\\'
	    ( 
		'n'		{buf.append("\n");}
	        | 'r' 	{buf.append("\r");}
	        | 't'	{buf.append("\t");}
	        | 'b'	{buf.append("\b");}
	        | 'f'	{buf.append("\f");}
	        | '"'	{buf.append("\"");}
	        | '\'' 	{buf.append("\'");}
	        | FORWARD_SLASH {buf.append("/"); }
	        | BACK_SLASH 	{buf.append("\\");}
	        | 'u' i=HEX_DIGIT j=HEX_DIGIT k=HEX_DIGIT l=HEX_DIGIT   {setText(i.getText()+j.getText()+k.getText()+l.getText());}
	    )
	;

fragment ESCAPE
	: '\\' ( 'n' | 'r' | 't' | 'b' | 'f' | '"' | '\'' | FORWARD_SLASH | BACK_SLASH | 'u' HEX_DIGIT HEX_DIGIT HEX_DIGIT HEX_DIGIT)
	;

WHITESPACE
	: ( '\t' | ' ' | '\r' | '\n' )+ {skip();}
	;
