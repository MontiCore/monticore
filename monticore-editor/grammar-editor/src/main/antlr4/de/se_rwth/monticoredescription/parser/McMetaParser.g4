parser grammar McMetaParser;

options {
    tokenVocab = McMetaLexer ;
}

grammarSpec : packageDeclaration? COMPONENT? GRAMMAR H_ID superGrammars? GRAMMAR_BODY_BEGIN productions GRAMMAR_BODY_END ;

packageDeclaration : PACKAGE H_QUALIFIED_ID H_SEMICOLON ;

superGrammars : H_EXTENDS H_QUALIFIED_ID (H_COMMA H_QUALIFIED_ID)* ;


productions : (classProd | interfaceProd | lexerProd | externalProd | abstractProd | astRule | concept)* ;

classProd : name action? SYMBOL_ID? (EXTENDS ID)? (IMPLEMENTS ID (COMMA ID)*)? (EQUALS alternatives)? SEMICOLON ;

interfaceProd : INTERFACE name SYMBOL_ID?  (EXTENDS ID (COMMA ID)*)? SEMICOLON ;

lexerProd : FRAGMENT? TOKEN name (EQUALS alternatives)? SEMICOLON ;

externalProd : EXTERNAL name SYMBOL_ID? SEMICOLON ;

abstractProd : ABSTRACT name (EXTENDS ID)? (IMPLEMENTS ID (COMMA ID)*)? SYMBOL_ID? SEMICOLON ;

astRule : AST name (ASTEXTENDS ID)? (ASTIMPLEMENTS ID)? (EQUALS (ALIEN_BLOCK | label? ID EBNF_SUFFIX? cardinality?)+)? SEMICOLON ;

concept : ID {$ID.text.equals("concept")}? ID {$ID.text.equals("antlr")}? ALIEN_BLOCK ;


alternatives : sequence (PIPE sequence?)* ;

sequence : symbol* ;

symbol : action? label? (nonterminal | terminal | block | terminalBlock) EBNF_SUFFIX? action? ;

nonterminal : ID AMPERSAND? ;

terminal : (STRING | STRING DOTDOT STRING);

block : TILDE? OPEN_PAREN alternatives CLOSE_PAREN ;

terminalBlock : label? OPEN_BRACKET alternatives CLOSE_BRACKET ;

action : COLON? ALIEN_BLOCK (EBNF_SUFFIX {$EBNF_SUFFIX.text.equals("?")}?)? ;

cardinality : ID {$ID.text.equals("min") || $ID.text.equals("max")}? EQUALS NUMBER ;


name : ID ;

label : ID (COLON | EQUALS) ;

