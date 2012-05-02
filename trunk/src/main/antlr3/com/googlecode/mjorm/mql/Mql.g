

grammar Mql;
 
tokens {
  TEST;
}

@header {
	package com.googlecode.mjorm.mql;
}

@lexer::header {
	package com.googlecode.mjorm.mql;
}

something : TEST*;

TEST : '0' .. '9'; 