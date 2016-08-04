lexer grammar McMetaLexer ;

COMPONENT : 'component' ;

GRAMMAR : 'grammar' ;

PACKAGE : 'package' ;

GRAMMAR_BODY_BEGIN : OPEN_BRACE -> pushMode(GRAMMAR_BODY) ;

H_EXTENDS : EXTENDS ;

H_SEMICOLON : SEMICOLON ;

H_COMMA : COMMA ;

H_ID : ID ;

H_QUALIFIED_ID : QUALIFIED_ID ;

H_BLOCK_COMMENT : BLOCK_COMMENT -> channel(HIDDEN) ;

H_LINE_COMMENT : LINE_COMMENT -> channel(HIDDEN) ;

H_WS : WS -> channel(HIDDEN) ;



mode GRAMMAR_BODY ;

METHOD : 'method ' ~'{'* -> more ;

ALIEN_BLOCK : OPEN_BRACE (~'}' | ALIEN_BLOCK)* '}' ;

GRAMMAR_BODY_END : '}' -> popMode ;

TOKEN : 'token' ;

FRAGMENT : 'fragment' ;

AST : 'ast' ;

EXTENDS : 'extends' ;

IMPLEMENTS : 'implements' ;

INTERFACE : 'interface' ;

ASTEXTENDS : 'astextends' ;

ASTIMPLEMENTS : 'astimplements' ;

EXTERNAL : 'external' ;

ABSTRACT : 'abstract' ;

STRING : DOUBLEQUOTE_STRING | SINGLEQUOTE_STRING ;

fragment DOUBLEQUOTE_STRING : '"' (~[\\"] | '\\' ('\\' | 'r' | 'n' | 't' | '"'))* '"' ;

fragment SINGLEQUOTE_STRING : '\'' (~[\\'] | '\\' ('\\' | 'r' | 'n' | 't' | '\''))* '\'' ;

DOTDOT : '..' ;

COLON : ':' ;

EQUALS : '=' ;

PIPE : '|' ;

SEMICOLON : ';' ;

TILDE : '~' ;

AMPERSAND : '&' ;

COMMA : ',' ;

OPEN_BRACE : '{' ;

OPEN_PAREN : '(' ;

CLOSE_PAREN : ')' ;

OPEN_BRACKET : '[' ;

CLOSE_BRACKET : ']' ;

DASH : '-' ;

EBNF_SUFFIX : '*' | '+' | '?' ;

NUMBER : [0-9]+ ;

ID : [_0-9a-zA-Z]+ ;

QUALIFIED_ID : ID ('.' ID)* ;

SYMBOL_ID : '@!' ID? ;

BLOCK_COMMENT : '/*' .*? '*/' -> channel(HIDDEN) ;

LINE_COMMENT : '//' .*? '\n' -> channel(HIDDEN) ;

WS : [ \n\r\t]+ -> channel(HIDDEN) ;
