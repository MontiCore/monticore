grammar Grammar_WithConcepts;
 @parser::header {
package de.monticore.grammar._parser;
import mc.antlr4.MCParser;
}
@lexer::header {
package de.monticore.grammar._parser;
}
options {
superClass=MCParser;
}

@parser::members{
/**
             * Counts the number of LT of type parameters and type arguments.
             * It is used in semantic predicates to ensure the right number
             * of closing '>' characters; which actually may have been
             * either GT, SR (GTGT), or BSR (GTGTGT) tokens.
             */
  public int ltCounter = 0;

  public int cmpCounter = 0;// convert function for OctalNumeral
private String convertOctalNumeral(Token t)  {
    return t.getText().intern();
}

// convert function for String
private String convertString(Token t)  {
    return t.getText().intern();
}

// convert function for HexDigit
private String convertHexDigit(Token t)  {
    return t.getText().intern();
}

// convert function for HexIntegerLiteral
private String convertHexIntegerLiteral(Token t)  {
    return t.getText().intern();
}

// convert function for BinaryDigit
private String convertBinaryDigit(Token t)  {
    return t.getText().intern();
}

// convert function for BinaryExponentIndicator
private String convertBinaryExponentIndicator(Token t)  {
    return t.getText().intern();
}

// convert function for OctalEscape
private String convertOctalEscape(Token t)  {
    return t.getText().intern();
}

// convert function for HexadecimalDoublePointLiteral
private String convertHexadecimalDoublePointLiteral(Token t)  {
    return t.getText().intern();
}

// convert function for IntegerTypeSuffix
private String convertIntegerTypeSuffix(Token t)  {
    return t.getText().intern();
}

// convert function for BinaryDigits
private String convertBinaryDigits(Token t)  {
    return t.getText().intern();
}

// convert function for OctalIntegerLiteral
private String convertOctalIntegerLiteral(Token t)  {
    return t.getText().intern();
}

// convert function for OctalDigit
private String convertOctalDigit(Token t)  {
    return t.getText().intern();
}

// convert function for OctalDigitOrUnderscore
private String convertOctalDigitOrUnderscore(Token t)  {
    return t.getText().intern();
}

// convert function for SL_COMMENT
private String convertSL_COMMENT(Token t)  {
    return t.getText().intern();
}

// convert function for OctalDigits
private String convertOctalDigits(Token t)  {
    return t.getText().intern();
}

// convert function for Name
private String convertName(Token t)  {
    return t.getText().intern();
}

// convert function for Num_Int
private String convertNum_Int(Token t)  {
    return t.getText().intern();
}

// convert function for Underscores
private String convertUnderscores(Token t)  {
    return t.getText().intern();
}

// convert function for SingleCharacter
private String convertSingleCharacter(Token t)  {
    return t.getText().intern();
}

// convert function for DecimalIntegerLiteral
private String convertDecimalIntegerLiteral(Token t)  {
    return t.getText().intern();
}

// convert function for SignedInteger
private String convertSignedInteger(Token t)  {
    return t.getText().intern();
}

// convert function for StringCharacter
private String convertStringCharacter(Token t)  {
    return t.getText().intern();
}

// convert function for Num_Double
private String convertNum_Double(Token t)  {
    return t.getText().intern();
}

// convert function for HexDigits
private String convertHexDigits(Token t)  {
    return t.getText().intern();
}

// convert function for ExponentPart
private String convertExponentPart(Token t)  {
    return t.getText().intern();
}

// convert function for Num_Float
private String convertNum_Float(Token t)  {
    return t.getText().intern();
}

// convert function for FloatTypeSuffix
private String convertFloatTypeSuffix(Token t)  {
    return t.getText().intern();
}

// convert function for WS
private String convertWS(Token t)  {
    return t.getText().intern();
}

// convert function for Digit
private String convertDigit(Token t)  {
    return t.getText().intern();
}

// convert function for StringCharacters
private String convertStringCharacters(Token t)  {
    return t.getText().intern();
}

// convert function for Num_Long
private String convertNum_Long(Token t)  {
    return t.getText().intern();
}

// convert function for EscapeSequence
private String convertEscapeSequence(Token t)  {
    return t.getText().intern();
}

// convert function for DecimalDoublePointLiteral
private String convertDecimalDoublePointLiteral(Token t)  {
    return t.getText().intern();
}

// convert function for HexDigitOrUnderscore
private String convertHexDigitOrUnderscore(Token t)  {
    return t.getText().intern();
}

// convert function for BinaryDigitOrUnderscore
private String convertBinaryDigitOrUnderscore(Token t)  {
    return t.getText().intern();
}

// convert function for Digits
private String convertDigits(Token t)  {
    return t.getText().intern();
}

// convert function for BinaryNumeral
private String convertBinaryNumeral(Token t)  {
    return t.getText().intern();
}

// convert function for ExponentIndicator
private String convertExponentIndicator(Token t)  {
    return t.getText().intern();
}

// convert function for Char
private String convertChar(Token t)  {
    return t.getText().intern();
}

// convert function for UnicodeEscape
private String convertUnicodeEscape(Token t)  {
    return t.getText().intern();
}

// convert function for DoubleTypeSuffix
private String convertDoubleTypeSuffix(Token t)  {
    return t.getText().intern();
}

// convert function for DigitOrUnderscore
private String convertDigitOrUnderscore(Token t)  {
    return t.getText().intern();
}

// convert function for HexSignificand
private String convertHexSignificand(Token t)  {
    return t.getText().intern();
}

// convert function for HexNumeral
private String convertHexNumeral(Token t)  {
    return t.getText().intern();
}

// convert function for BinaryIntegerLiteral
private String convertBinaryIntegerLiteral(Token t)  {
    return t.getText().intern();
}

// convert function for NEWLINE
private String convertNEWLINE(Token t)  {
    return t.getText().intern();
}

// convert function for NonZeroDigit
private String convertNonZeroDigit(Token t)  {
    return t.getText().intern();
}

// convert function for ZeroToThree
private String convertZeroToThree(Token t)  {
    return t.getText().intern();
}

// convert function for DecimalNumeral
private String convertDecimalNumeral(Token t)  {
    return t.getText().intern();
}

// convert function for HexadecimalFloatingPointLiteral
private String convertHexadecimalFloatingPointLiteral(Token t)  {
    return t.getText().intern();
}

// convert function for DecimalFloatingPointLiteral
private String convertDecimalFloatingPointLiteral(Token t)  {
    return t.getText().intern();
}

// convert function for BinaryExponent
private String convertBinaryExponent(Token t)  {
    return t.getText().intern();
}

// convert function for ML_COMMENT
private String convertML_COMMENT(Token t)  {
    return t.getText().intern();
}

// convert function for Sign
private String convertSign(Token t)  {
    return t.getText().intern();
}


}
@lexer::members {

private mc.antlr4.MCParser _monticore_parser;
protected mc.antlr4.MCParser getCompiler() {
   return _monticore_parser;
}
public void setMCParser(mc.antlr4.MCParser in) {
  this._monticore_parser = in;
}
}


mcstatement_eof returns [de.monticore.grammar.astscript._ast.ASTMCStatement ret = null] :
    tmp = mcstatement  {$ret = $tmp.ret;} EOF ;

mcstatement returns [de.monticore.grammar.astscript._ast.ASTMCStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.astscript._ast.ASTMCStatement a = null;
a=de.monticore.grammar.astscript._ast.AstScriptNodeFactory.createASTMCStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        'once' {a.setOnce(true);}
    )
    ?
    (
         tmp0=scriptstatement {a.getStatements().add(_localctx.tmp0.ret);}   
    )
    *
;




constructor_eof returns [de.monticore.grammar.astscript._ast.ASTConstructor ret = null] :
    tmp = constructor  {$ret = $tmp.ret;} EOF ;

constructor returns [de.monticore.grammar.astscript._ast.ASTConstructor ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.astscript._ast.ASTConstructor a = null;
a=de.monticore.grammar.astscript._ast.AstScriptNodeFactory.createASTConstructor();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( EXCLAMATIONMARK   )  
    ( LPAREN   )  
    (
         tmp0=nonconstructorstatement {a.getStatements().add(_localctx.tmp0.ret);}   
    )
    *
    ( RPAREN   )  
    ( SEMI   )  
;




assignment_eof returns [de.monticore.grammar.astscript._ast.ASTAssignment ret = null] :
    tmp = assignment  {$ret = $tmp.ret;} EOF ;

assignment returns [de.monticore.grammar.astscript._ast.ASTAssignment ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.astscript._ast.ASTAssignment a = null;
a=de.monticore.grammar.astscript._ast.AstScriptNodeFactory.createASTAssignment();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setLeftSide(convertName($tmp0));})  
    (
    EQUALS {a.setOperator(de.monticore.grammar.astscript._ast.ASTConstantsAstScript.EQUALS);}
    |
    PLUSEQUALS {a.setOperator(de.monticore.grammar.astscript._ast.ASTConstantsAstScript.PLUSASSIGN);}
    )
    ( tmp1=Name  {a.setRightSide(convertName($tmp1));})  
    ( SEMI   )  
;




pushstatement_eof returns [de.monticore.grammar.astscript._ast.ASTPushStatement ret = null] :
    tmp = pushstatement  {$ret = $tmp.ret;} EOF ;

pushstatement returns [de.monticore.grammar.astscript._ast.ASTPushStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.astscript._ast.ASTPushStatement a = null;
a=de.monticore.grammar.astscript._ast.AstScriptNodeFactory.createASTPushStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'push'   )  
    ( LPAREN   )  
    ( tmp0=Name  {a.setStackname(convertName($tmp0));})  
    ( COMMA   )  
    ( tmp1=Name  {a.setValue(convertName($tmp1));})  
    ( RPAREN   )  
    ( SEMI   )  
;




popstatement_eof returns [de.monticore.grammar.astscript._ast.ASTPopStatement ret = null] :
    tmp = popstatement  {$ret = $tmp.ret;} EOF ;

popstatement returns [de.monticore.grammar.astscript._ast.ASTPopStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.astscript._ast.ASTPopStatement a = null;
a=de.monticore.grammar.astscript._ast.AstScriptNodeFactory.createASTPopStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'pop'   )  
    ( LPAREN   )  
    ( tmp0=Name  {a.setStackname(convertName($tmp0));})  
    ( RPAREN   )  
    ( SEMI   )  
;




conceptattributes_eof returns [de.monticore.grammar.concepts.attributes._ast.ASTConceptAttributes ret = null] :
    tmp = conceptattributes  {$ret = $tmp.ret;} EOF ;

conceptattributes returns [de.monticore.grammar.concepts.attributes._ast.ASTConceptAttributes ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.concepts.attributes._ast.ASTConceptAttributes a = null;
a=de.monticore.grammar.concepts.attributes._ast.AttributesNodeFactory.createASTConceptAttributes();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( LCURLY   )  
    (
         tmp0=attributedefinition {a.getAttributeDefinition().add(_localctx.tmp0.ret);}   
      |
         tmp1=attributeconcretion {a.getAttributeConcretion().add(_localctx.tmp1.ret);}   
    )
    *
    ( RCURLY   )  
;




attributeconcretion_eof returns [de.monticore.grammar.concepts.attributes._ast.ASTAttributeConcretion ret = null] :
    tmp = attributeconcretion  {$ret = $tmp.ret;} EOF ;

attributeconcretion returns [de.monticore.grammar.concepts.attributes._ast.ASTAttributeConcretion ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.concepts.attributes._ast.ASTAttributeConcretion a = null;
a=de.monticore.grammar.concepts.attributes._ast.AttributesNodeFactory.createASTAttributeConcretion();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    ( LCURLY   )  
    (
         tmp1=calculation {a.getCalculation().add(_localctx.tmp1.ret);}   
    )
    +
    ( RCURLY   )  
;




calculation_eof returns [de.monticore.grammar.concepts.attributes._ast.ASTCalculation ret = null] :
    tmp = calculation  {$ret = $tmp.ret;} EOF ;

calculation returns [de.monticore.grammar.concepts.attributes._ast.ASTCalculation ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.concepts.attributes._ast.ASTCalculation a = null;
a=de.monticore.grammar.concepts.attributes._ast.AttributesNodeFactory.createASTCalculation();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        (
            ( tmp0=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp0));})  
          |
            ( 'grammar'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("grammar");} )  
          |
            ( 'ast'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("ast");} )  
        )

        (
            ( POINT   )  
            (
                ( tmp1=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp1));})  
              |
                ( 'grammar'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("grammar");} )  
              |
                ( 'ast'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("ast");} )  
            )

        )
        *
    )

    (
        ( EQUALS   )  
         tmp2=externalattributetype {a.setCalculationClass(_localctx.tmp2.ret);}   
    )
    ?
;




attributedefinition_eof returns [de.monticore.grammar.concepts.attributes._ast.ASTAttributeDefinition ret = null] :
    tmp = attributedefinition  {$ret = $tmp.ret;} EOF ;

attributedefinition returns [de.monticore.grammar.concepts.attributes._ast.ASTAttributeDefinition ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.concepts.attributes._ast.ASTAttributeDefinition a = null;
a=de.monticore.grammar.concepts.attributes._ast.AttributesNodeFactory.createASTAttributeDefinition();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
    'inh' {a.setKind(de.monticore.grammar.concepts.attributes._ast.ASTConstantsAttributes.INH);}
    |
    'syn' {a.setKind(de.monticore.grammar.concepts.attributes._ast.ASTConstantsAttributes.SYN);}
    |
    'global' {a.setKind(de.monticore.grammar.concepts.attributes._ast.ASTConstantsAttributes.GLOBAL);}
    )
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    (
        ( COLON   )  
         tmp1=referenceinattributes {a.setType(_localctx.tmp1.ret);}   
        (
            ( LCURLY   )  
            ( tmp2=overrides {a.getOverrides().add(_localctx.tmp2.ret);} ) * 
            ( RCURLY   )  
          |
            ( SEMI   )  
        )

      |
        ( LCURLY   )  
        ( tmp3=overrides {a.getOverrides().add(_localctx.tmp3.ret);} ) + 
        ( RCURLY   )  
    )

;




overrides_eof returns [de.monticore.grammar.concepts.attributes._ast.ASTOverrides ret = null] :
    tmp = overrides  {$ret = $tmp.ret;} EOF ;

overrides returns [de.monticore.grammar.concepts.attributes._ast.ASTOverrides ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.concepts.attributes._ast.ASTOverrides a = null;
a=de.monticore.grammar.concepts.attributes._ast.AttributesNodeFactory.createASTOverrides();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        ( tmp0=Name  {if (a.getRules()==null){a.setRules(new java.util.ArrayList());};  a.getRules().add(convertName($tmp0));})  
        (
            ( COMMA   )  
            ( tmp1=Name  {if (a.getRules()==null){a.setRules(new java.util.ArrayList());};  a.getRules().add(convertName($tmp1));})  
        )
        *
    )

    ( COLON   )  
     tmp2=referenceinattributes {a.setType(_localctx.tmp2.ret);}   
    ( SEMI   )  
;




rulereferenceinattribute_eof returns [de.monticore.grammar.concepts.attributes._ast.ASTRuleReferenceInAttribute ret = null] :
    tmp = rulereferenceinattribute  {$ret = $tmp.ret;} EOF ;

rulereferenceinattribute returns [de.monticore.grammar.concepts.attributes._ast.ASTRuleReferenceInAttribute ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.concepts.attributes._ast.ASTRuleReferenceInAttribute a = null;
a=de.monticore.grammar.concepts.attributes._ast.AttributesNodeFactory.createASTRuleReferenceInAttribute();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    (
        STAR {a.setIterated(true);}
    )
    ?
;




externalattributetype_eof returns [de.monticore.grammar.concepts.attributes._ast.ASTExternalAttributeType ret = null] :
    tmp = externalattributetype  {$ret = $tmp.ret;} EOF ;

externalattributetype returns [de.monticore.grammar.concepts.attributes._ast.ASTExternalAttributeType ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.concepts.attributes._ast.ASTExternalAttributeType a = null;
a=de.monticore.grammar.concepts.attributes._ast.AttributesNodeFactory.createASTExternalAttributeType();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( SLASH   )  
     tmp0=genericattributetype {a.setGenericAttributeType(_localctx.tmp0.ret);}   
;




genericattributetype_eof returns [de.monticore.grammar.concepts.attributes._ast.ASTGenericAttributeType ret = null] :
    tmp = genericattributetype  {$ret = $tmp.ret;} EOF ;

genericattributetype returns [de.monticore.grammar.concepts.attributes._ast.ASTGenericAttributeType ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.concepts.attributes._ast.ASTGenericAttributeType a = null;
a=de.monticore.grammar.concepts.attributes._ast.AttributesNodeFactory.createASTGenericAttributeType();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        ( tmp0=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp0));})  
      |
        ( 'grammar'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("grammar");} )  
      |
        ( 'ast'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("ast");} )  
    )

    (
        ( POINT   )  
        (
            ( tmp1=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp1));})  
          |
            ( 'grammar'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("grammar");} )  
          |
            ( 'ast'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("ast");} )  
        )

    )
    *
    (
        ( LT   )  
         tmp2=genericattributetype {a.getGeneric().add(_localctx.tmp2.ret);}   
        (
            ( COMMA   )  
             tmp3=genericattributetype {a.getGeneric().add(_localctx.tmp3.ret);}   
        )
        *
        ( GT   )  
    )
    ?
    {a.setDimension(0);}
    (
        ( LBRACK   )  
        ( RBRACK   )  
        {a.setDimension(a.getDimension() + 1);}
    )
    *
;




conceptantlr_eof returns [de.monticore.grammar.concepts.antlr._ast.ASTConceptAntlr ret = null] :
    tmp = conceptantlr  {$ret = $tmp.ret;} EOF ;

conceptantlr returns [de.monticore.grammar.concepts.antlr._ast.ASTConceptAntlr ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.concepts.antlr._ast.ASTConceptAntlr a = null;
a=de.monticore.grammar.concepts.antlr._ast.AntlrNodeFactory.createASTConceptAntlr();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( LCURLY   )  
    (
         tmp0=antlrparsercode {a.getAntlrParserCode().add(_localctx.tmp0.ret);}   
      |
         tmp1=antlrparseraction {a.getAntlrParserAction().add(_localctx.tmp1.ret);}   
      |
         tmp2=antlrlexercode {a.getAntlrLexerCode().add(_localctx.tmp2.ret);}   
      |
         tmp3=antlrlexeraction {a.getAntlrLexerAction().add(_localctx.tmp3.ret);}   
    )
    *
    ( RCURLY   )  
;




antlrparsercode_eof returns [de.monticore.grammar.concepts.antlr._ast.ASTAntlrParserCode ret = null] :
    tmp = antlrparsercode  {$ret = $tmp.ret;} EOF ;

antlrparsercode returns [de.monticore.grammar.concepts.antlr._ast.ASTAntlrParserCode ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.concepts.antlr._ast.ASTAntlrParserCode a = null;
a=de.monticore.grammar.concepts.antlr._ast.AntlrNodeFactory.createASTAntlrParserCode();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'parser'   )  
    ( 'antlr'   )  
    ( tmp0=Name  {a.setName(convertName($tmp0));}) ? 
    ( LCURLY   )  

    ( RCURLY   )  
;




antlrparseraction_eof returns [de.monticore.grammar.concepts.antlr._ast.ASTAntlrParserAction ret = null] :
    tmp = antlrparseraction  {$ret = $tmp.ret;} EOF ;

antlrparseraction returns [de.monticore.grammar.concepts.antlr._ast.ASTAntlrParserAction ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.concepts.antlr._ast.ASTAntlrParserAction a = null;
a=de.monticore.grammar.concepts.antlr._ast.AntlrNodeFactory.createASTAntlrParserAction();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'parser'   )  
    ( 'java'   )  
    ( LCURLY   )  
     tmp0=action {a.setText(_localctx.tmp0.ret);}   
    ( RCURLY   )  
;




antlrlexercode_eof returns [de.monticore.grammar.concepts.antlr._ast.ASTAntlrLexerCode ret = null] :
    tmp = antlrlexercode  {$ret = $tmp.ret;} EOF ;

antlrlexercode returns [de.monticore.grammar.concepts.antlr._ast.ASTAntlrLexerCode ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.concepts.antlr._ast.ASTAntlrLexerCode a = null;
a=de.monticore.grammar.concepts.antlr._ast.AntlrNodeFactory.createASTAntlrLexerCode();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'lexer'   )  
    ( 'antlr'   )  
    ( tmp0=Name  {a.setName(convertName($tmp0));}) ? 
    ( LCURLY   )  

    ( RCURLY   )  
;




antlrlexeraction_eof returns [de.monticore.grammar.concepts.antlr._ast.ASTAntlrLexerAction ret = null] :
    tmp = antlrlexeraction  {$ret = $tmp.ret;} EOF ;

antlrlexeraction returns [de.monticore.grammar.concepts.antlr._ast.ASTAntlrLexerAction ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar.concepts.antlr._ast.ASTAntlrLexerAction a = null;
a=de.monticore.grammar.concepts.antlr._ast.AntlrNodeFactory.createASTAntlrLexerAction();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'lexer'   )  
    ( 'java'   )  
    ( LCURLY   )  
     tmp0=action {a.setText(_localctx.tmp0.ret);}   
    ( RCURLY   )  
;




action_eof returns [de.monticore.grammar._ast.ASTAction ret = null] :
    tmp = action  {$ret = $tmp.ret;} EOF ;

action returns [de.monticore.grammar._ast.ASTAction ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTAction a = null;
a=de.monticore.grammar._ast.Grammar_WithConceptsNodeFactory.createASTAction();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=statements {a.setStatements(_localctx.tmp0.ret);}   
;




nullliteral_eof returns [de.monticore.literals._ast.ASTNullLiteral ret = null] :
    tmp = nullliteral  {$ret = $tmp.ret;} EOF ;

nullliteral returns [de.monticore.literals._ast.ASTNullLiteral ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.literals._ast.ASTNullLiteral a = null;
a=de.monticore.literals._ast.LiteralsNodeFactory.createASTNullLiteral();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'null'   )  
;




booleanliteral_eof returns [de.monticore.literals._ast.ASTBooleanLiteral ret = null] :
    tmp = booleanliteral  {$ret = $tmp.ret;} EOF ;

booleanliteral returns [de.monticore.literals._ast.ASTBooleanLiteral ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.literals._ast.ASTBooleanLiteral a = null;
a=de.monticore.literals._ast.LiteralsNodeFactory.createASTBooleanLiteral();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
    'true' {a.setSource(de.monticore.literals._ast.ASTConstantsLiterals.TRUE);}
    |
    'false' {a.setSource(de.monticore.literals._ast.ASTConstantsLiterals.FALSE);}
    )
;




charliteral_eof returns [de.monticore.literals._ast.ASTCharLiteral ret = null] :
    tmp = charliteral  {$ret = $tmp.ret;} EOF ;

charliteral returns [de.monticore.literals._ast.ASTCharLiteral ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.literals._ast.ASTCharLiteral a = null;
a=de.monticore.literals._ast.LiteralsNodeFactory.createASTCharLiteral();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Char  {a.setSource(convertChar($tmp0));})  
;




stringliteral_eof returns [de.monticore.literals._ast.ASTStringLiteral ret = null] :
    tmp = stringliteral  {$ret = $tmp.ret;} EOF ;

stringliteral returns [de.monticore.literals._ast.ASTStringLiteral ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.literals._ast.ASTStringLiteral a = null;
a=de.monticore.literals._ast.LiteralsNodeFactory.createASTStringLiteral();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=String  {a.setSource(convertString($tmp0));})  
;




intliteral_eof returns [de.monticore.literals._ast.ASTIntLiteral ret = null] :
    tmp = intliteral  {$ret = $tmp.ret;} EOF ;

intliteral returns [de.monticore.literals._ast.ASTIntLiteral ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.literals._ast.ASTIntLiteral a = null;
a=de.monticore.literals._ast.LiteralsNodeFactory.createASTIntLiteral();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Num_Int  {a.setSource(convertNum_Int($tmp0));})  
;




signedintliteral_eof returns [de.monticore.literals._ast.ASTSignedIntLiteral ret = null] :
    tmp = signedintliteral  {$ret = $tmp.ret;} EOF ;

signedintliteral returns [de.monticore.literals._ast.ASTSignedIntLiteral ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.literals._ast.ASTSignedIntLiteral a = null;
a=de.monticore.literals._ast.LiteralsNodeFactory.createASTSignedIntLiteral();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        MINUS {a.setNegative(true);}
    )
    ?
    ( tmp0=Num_Int  {a.setSource(convertNum_Int($tmp0));})  
;




longliteral_eof returns [de.monticore.literals._ast.ASTLongLiteral ret = null] :
    tmp = longliteral  {$ret = $tmp.ret;} EOF ;

longliteral returns [de.monticore.literals._ast.ASTLongLiteral ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.literals._ast.ASTLongLiteral a = null;
a=de.monticore.literals._ast.LiteralsNodeFactory.createASTLongLiteral();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Num_Long  {a.setSource(convertNum_Long($tmp0));})  
;




signedlongliteral_eof returns [de.monticore.literals._ast.ASTSignedLongLiteral ret = null] :
    tmp = signedlongliteral  {$ret = $tmp.ret;} EOF ;

signedlongliteral returns [de.monticore.literals._ast.ASTSignedLongLiteral ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.literals._ast.ASTSignedLongLiteral a = null;
a=de.monticore.literals._ast.LiteralsNodeFactory.createASTSignedLongLiteral();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        MINUS {a.setNegative(true);}
    )
    ?
    ( tmp0=Num_Long  {a.setSource(convertNum_Long($tmp0));})  
;




floatliteral_eof returns [de.monticore.literals._ast.ASTFloatLiteral ret = null] :
    tmp = floatliteral  {$ret = $tmp.ret;} EOF ;

floatliteral returns [de.monticore.literals._ast.ASTFloatLiteral ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.literals._ast.ASTFloatLiteral a = null;
a=de.monticore.literals._ast.LiteralsNodeFactory.createASTFloatLiteral();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Num_Float  {a.setSource(convertNum_Float($tmp0));})  
;




signedfloatliteral_eof returns [de.monticore.literals._ast.ASTSignedFloatLiteral ret = null] :
    tmp = signedfloatliteral  {$ret = $tmp.ret;} EOF ;

signedfloatliteral returns [de.monticore.literals._ast.ASTSignedFloatLiteral ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.literals._ast.ASTSignedFloatLiteral a = null;
a=de.monticore.literals._ast.LiteralsNodeFactory.createASTSignedFloatLiteral();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        MINUS {a.setNegative(true);}
    )
    ?
    ( tmp0=Num_Float  {a.setSource(convertNum_Float($tmp0));})  
;




doubleliteral_eof returns [de.monticore.literals._ast.ASTDoubleLiteral ret = null] :
    tmp = doubleliteral  {$ret = $tmp.ret;} EOF ;

doubleliteral returns [de.monticore.literals._ast.ASTDoubleLiteral ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.literals._ast.ASTDoubleLiteral a = null;
a=de.monticore.literals._ast.LiteralsNodeFactory.createASTDoubleLiteral();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Num_Double  {a.setSource(convertNum_Double($tmp0));})  
;




signeddoubleliteral_eof returns [de.monticore.literals._ast.ASTSignedDoubleLiteral ret = null] :
    tmp = signeddoubleliteral  {$ret = $tmp.ret;} EOF ;

signeddoubleliteral returns [de.monticore.literals._ast.ASTSignedDoubleLiteral ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.literals._ast.ASTSignedDoubleLiteral a = null;
a=de.monticore.literals._ast.LiteralsNodeFactory.createASTSignedDoubleLiteral();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        MINUS {a.setNegative(true);}
    )
    ?
    ( tmp0=Num_Double  {a.setSource(convertNum_Double($tmp0));})  
;




qualifiedname_eof returns [de.monticore.types._ast.ASTQualifiedName ret = null] :
    tmp = qualifiedname  {$ret = $tmp.ret;} EOF ;

qualifiedname returns [de.monticore.types._ast.ASTQualifiedName ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.types._ast.ASTQualifiedName a = null;
a=de.monticore.types._ast.TypesNodeFactory.createASTQualifiedName();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add(convertName($tmp0));}
    /* Automatically added keywords [astscript, synchronized, ast, do, while, astextends, grammar, java, continue, else, options, catch, if, case, new, init, void, package, method, antlr, finally, this, throws, lexer, push, enum, external, parser, null, extends, once, astimplements, true, try, returns, implements, import, concept, for, syn, global, interface, switch, pop, default, min, assert, class, EOF, inh, break, max, false, MCA, abstract, follow, instanceof, token, super, throw, return] */
     | ( 'astscript'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("astscript");} )  
     | ( 'synchronized'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("synchronized");} )  
     | ( 'ast'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("ast");} )  
     | ( 'do'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("do");} )  
     | ( 'while'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("while");} )  
     | ( 'astextends'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("astextends");} )  
     | ( 'grammar'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("grammar");} )  
     | ( 'java'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("java");} )  
     | ( 'continue'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("continue");} )  
     | ( 'else'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("else");} )  
     | ( 'options'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("options");} )  
     | ( 'catch'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("catch");} )  
     | ( 'if'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("if");} )  
     | ( 'case'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("case");} )  
     | ( 'new'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("new");} )  
     | ( 'init'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("init");} )  
     | ( 'void'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("void");} )  
     | ( 'package'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("package");} )  
     | ( 'method'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("method");} )  
     | ( 'antlr'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("antlr");} )  
     | ( 'finally'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("finally");} )  
     | ( 'this'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("this");} )  
     | ( 'throws'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("throws");} )  
     | ( 'lexer'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("lexer");} )  
     | ( 'push'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("push");} )  
     | ( 'enum'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("enum");} )  
     | ( 'external'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("external");} )  
     | ( 'parser'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("parser");} )  
     | ( 'null'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("null");} )  
     | ( 'extends'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("extends");} )  
     | ( 'once'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("once");} )  
     | ( 'astimplements'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("astimplements");} )  
     | ( 'true'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("true");} )  
     | ( 'try'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("try");} )  
     | ( 'returns'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("returns");} )  
     | ( 'implements'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("implements");} )  
     | ( 'import'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("import");} )  
     | ( 'concept'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("concept");} )  
     | ( 'for'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("for");} )  
     | ( 'syn'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("syn");} )  
     | ( 'global'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("global");} )  
     | ( 'interface'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("interface");} )  
     | ( 'switch'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("switch");} )  
     | ( 'pop'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("pop");} )  
     | ( 'default'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("default");} )  
     | ( 'min'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("min");} )  
     | ( 'assert'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("assert");} )  
     | ( 'class'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("class");} )  
     | ( 'EOF'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("EOF");} )  
     | ( 'inh'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("inh");} )  
     | ( 'break'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("break");} )  
     | ( 'max'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("max");} )  
     | ( 'false'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("false");} )  
     | ( 'MCA'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("MCA");} )  
     | ( 'abstract'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("abstract");} )  
     | ( 'follow'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("follow");} )  
     | ( 'instanceof'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("instanceof");} )  
     | ( 'token'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("token");} )  
     | ( 'super'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("super");} )  
     | ( 'throw'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("throw");} )  
     | ( 'return'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("return");} )  
    )  
    (
        ( POINT   )  
        ( tmp1=Name  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add(convertName($tmp1));}
        /* Automatically added keywords [astscript, synchronized, ast, do, while, astextends, grammar, java, continue, else, options, catch, if, case, new, init, void, package, method, antlr, finally, this, throws, lexer, push, enum, external, parser, null, extends, once, astimplements, true, try, returns, implements, import, concept, for, syn, global, interface, switch, pop, default, min, assert, class, EOF, inh, break, max, false, MCA, abstract, follow, instanceof, token, super, throw, return] */
         | ( 'astscript'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("astscript");} )  
         | ( 'synchronized'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("synchronized");} )  
         | ( 'ast'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("ast");} )  
         | ( 'do'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("do");} )  
         | ( 'while'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("while");} )  
         | ( 'astextends'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("astextends");} )  
         | ( 'grammar'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("grammar");} )  
         | ( 'java'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("java");} )  
         | ( 'continue'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("continue");} )  
         | ( 'else'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("else");} )  
         | ( 'options'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("options");} )  
         | ( 'catch'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("catch");} )  
         | ( 'if'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("if");} )  
         | ( 'case'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("case");} )  
         | ( 'new'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("new");} )  
         | ( 'init'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("init");} )  
         | ( 'void'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("void");} )  
         | ( 'package'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("package");} )  
         | ( 'method'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("method");} )  
         | ( 'antlr'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("antlr");} )  
         | ( 'finally'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("finally");} )  
         | ( 'this'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("this");} )  
         | ( 'throws'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("throws");} )  
         | ( 'lexer'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("lexer");} )  
         | ( 'push'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("push");} )  
         | ( 'enum'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("enum");} )  
         | ( 'external'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("external");} )  
         | ( 'parser'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("parser");} )  
         | ( 'null'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("null");} )  
         | ( 'extends'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("extends");} )  
         | ( 'once'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("once");} )  
         | ( 'astimplements'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("astimplements");} )  
         | ( 'true'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("true");} )  
         | ( 'try'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("try");} )  
         | ( 'returns'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("returns");} )  
         | ( 'implements'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("implements");} )  
         | ( 'import'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("import");} )  
         | ( 'concept'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("concept");} )  
         | ( 'for'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("for");} )  
         | ( 'syn'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("syn");} )  
         | ( 'global'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("global");} )  
         | ( 'interface'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("interface");} )  
         | ( 'switch'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("switch");} )  
         | ( 'pop'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("pop");} )  
         | ( 'default'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("default");} )  
         | ( 'min'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("min");} )  
         | ( 'assert'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("assert");} )  
         | ( 'class'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("class");} )  
         | ( 'EOF'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("EOF");} )  
         | ( 'inh'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("inh");} )  
         | ( 'break'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("break");} )  
         | ( 'max'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("max");} )  
         | ( 'false'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("false");} )  
         | ( 'MCA'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("MCA");} )  
         | ( 'abstract'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("abstract");} )  
         | ( 'follow'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("follow");} )  
         | ( 'instanceof'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("instanceof");} )  
         | ( 'token'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("token");} )  
         | ( 'super'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("super");} )  
         | ( 'throw'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("throw");} )  
         | ( 'return'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("return");} )  
        )  
    )
    *
;




complexarraytype_eof returns [de.monticore.types._ast.ASTComplexArrayType ret = null] :
    tmp = complexarraytype  {$ret = $tmp.ret;} EOF ;

complexarraytype returns [de.monticore.types._ast.ASTComplexArrayType ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.types._ast.ASTComplexArrayType a = null;
a=de.monticore.types._ast.TypesNodeFactory.createASTComplexArrayType();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=complexreferencetype {a.setComponentType(_localctx.tmp0.ret);}   
    (
        (
            (
                ( LBRACK   )  
                ( RBRACK   )  
                {a.setDimensions(a.getDimensions() + 1);}
            )
            +
        )

    )

;




primitivearraytype_eof returns [de.monticore.types._ast.ASTPrimitiveArrayType ret = null] :
    tmp = primitivearraytype  {$ret = $tmp.ret;} EOF ;

primitivearraytype returns [de.monticore.types._ast.ASTPrimitiveArrayType ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.types._ast.ASTPrimitiveArrayType a = null;
a=de.monticore.types._ast.TypesNodeFactory.createASTPrimitiveArrayType();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=primitivetype {a.setComponentType(_localctx.tmp0.ret);}   
    (
        ( LBRACK   )  
        ( RBRACK   )  
        {a.setDimensions(a.getDimensions() + 1);}
    )
    +
;




voidtype_eof returns [de.monticore.types._ast.ASTVoidType ret = null] :
    tmp = voidtype  {$ret = $tmp.ret;} EOF ;

voidtype returns [de.monticore.types._ast.ASTVoidType ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.types._ast.ASTVoidType a = null;
a=de.monticore.types._ast.TypesNodeFactory.createASTVoidType();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'void'   )  
;




primitivetype_eof returns [de.monticore.types._ast.ASTPrimitiveType ret = null] :
    tmp = primitivetype  {$ret = $tmp.ret;} EOF ;

primitivetype returns [de.monticore.types._ast.ASTPrimitiveType ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.types._ast.ASTPrimitiveType a = null;
a=de.monticore.types._ast.TypesNodeFactory.createASTPrimitiveType();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
    'boolean' {a.setPrimitive(de.monticore.types._ast.ASTConstantsTypes.BOOLEAN);}
    |
    'byte' {a.setPrimitive(de.monticore.types._ast.ASTConstantsTypes.BYTE);}
    |
    'short' {a.setPrimitive(de.monticore.types._ast.ASTConstantsTypes.SHORT);}
    |
    'int' {a.setPrimitive(de.monticore.types._ast.ASTConstantsTypes.INT);}
    |
    'long' {a.setPrimitive(de.monticore.types._ast.ASTConstantsTypes.LONG);}
    |
    'char' {a.setPrimitive(de.monticore.types._ast.ASTConstantsTypes.CHAR);}
    |
    'float' {a.setPrimitive(de.monticore.types._ast.ASTConstantsTypes.FLOAT);}
    |
    'double' {a.setPrimitive(de.monticore.types._ast.ASTConstantsTypes.DOUBLE);}
    )
;




simplereferencetype_eof returns [de.monticore.types._ast.ASTSimpleReferenceType ret = null] :
    tmp = simplereferencetype  {$ret = $tmp.ret;} EOF ;

simplereferencetype returns [de.monticore.types._ast.ASTSimpleReferenceType ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.types._ast.ASTSimpleReferenceType a = null;
a=de.monticore.types._ast.TypesNodeFactory.createASTSimpleReferenceType();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp0));})  
    (
        ( POINT   )  
        ( tmp1=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp1));}
        /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, continue, else, options, catch, if, case, new, init, void, package, method, byte, double, antlr, finally, this, throws, lexer, push, enum, external, parser, null, extends, once, astimplements, true, try, returns, implements, import, concept, for, syn, global, interface, long, switch, pop, default, min, assert, class, EOF, inh, break, max, false, MCA, abstract, follow, int, instanceof, token, super, boolean, throw, char, short, return] */
         | ( 'astscript'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astscript");} )  
         | ( 'synchronized'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("synchronized");} )  
         | ( 'ast'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("ast");} )  
         | ( 'do'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("do");} )  
         | ( 'while'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("while");} )  
         | ( 'float'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("float");} )  
         | ( 'astextends'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astextends");} )  
         | ( 'grammar'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("grammar");} )  
         | ( 'java'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("java");} )  
         | ( 'continue'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("continue");} )  
         | ( 'else'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("else");} )  
         | ( 'options'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("options");} )  
         | ( 'catch'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("catch");} )  
         | ( 'if'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("if");} )  
         | ( 'case'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("case");} )  
         | ( 'new'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("new");} )  
         | ( 'init'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("init");} )  
         | ( 'void'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("void");} )  
         | ( 'package'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("package");} )  
         | ( 'method'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("method");} )  
         | ( 'byte'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("byte");} )  
         | ( 'double'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("double");} )  
         | ( 'antlr'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("antlr");} )  
         | ( 'finally'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("finally");} )  
         | ( 'this'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("this");} )  
         | ( 'throws'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("throws");} )  
         | ( 'lexer'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("lexer");} )  
         | ( 'push'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("push");} )  
         | ( 'enum'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("enum");} )  
         | ( 'external'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("external");} )  
         | ( 'parser'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("parser");} )  
         | ( 'null'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("null");} )  
         | ( 'extends'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("extends");} )  
         | ( 'once'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("once");} )  
         | ( 'astimplements'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astimplements");} )  
         | ( 'true'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("true");} )  
         | ( 'try'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("try");} )  
         | ( 'returns'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("returns");} )  
         | ( 'implements'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("implements");} )  
         | ( 'import'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("import");} )  
         | ( 'concept'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("concept");} )  
         | ( 'for'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("for");} )  
         | ( 'syn'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("syn");} )  
         | ( 'global'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("global");} )  
         | ( 'interface'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("interface");} )  
         | ( 'long'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("long");} )  
         | ( 'switch'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("switch");} )  
         | ( 'pop'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("pop");} )  
         | ( 'default'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("default");} )  
         | ( 'min'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("min");} )  
         | ( 'assert'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("assert");} )  
         | ( 'class'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("class");} )  
         | ( 'EOF'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("EOF");} )  
         | ( 'inh'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("inh");} )  
         | ( 'break'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("break");} )  
         | ( 'max'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("max");} )  
         | ( 'false'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("false");} )  
         | ( 'MCA'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("MCA");} )  
         | ( 'abstract'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("abstract");} )  
         | ( 'follow'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("follow");} )  
         | ( 'int'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("int");} )  
         | ( 'instanceof'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("instanceof");} )  
         | ( 'token'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("token");} )  
         | ( 'super'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("super");} )  
         | ( 'boolean'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("boolean");} )  
         | ( 'throw'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("throw");} )  
         | ( 'char'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("char");} )  
         | ( 'short'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("short");} )  
         | ( 'return'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("return");} )  
        )  
    )
    *
    (
         tmp2=typearguments {a.setTypeArguments(_localctx.tmp2.ret);}   
    )
    ?
;




complexreferencetype_eof returns [de.monticore.types._ast.ASTComplexReferenceType ret = null] :
    tmp = complexreferencetype  {$ret = $tmp.ret;} EOF ;

complexreferencetype returns [de.monticore.types._ast.ASTComplexReferenceType ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.types._ast.ASTComplexReferenceType a = null;
a=de.monticore.types._ast.TypesNodeFactory.createASTComplexReferenceType();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=simplereferencetype {a.getSimpleReferenceType().add(_localctx.tmp0.ret);}   
    (
        ( POINT   )  
         tmp1=simplereferencetype {a.getSimpleReferenceType().add(_localctx.tmp1.ret);}   
    )
    *
;




typearguments_eof returns [de.monticore.types._ast.ASTTypeArguments ret = null] :
    tmp = typearguments  {$ret = $tmp.ret;} EOF ;

typearguments returns [de.monticore.types._ast.ASTTypeArguments ret = null]  
@init{int ltLevel = 0;// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.types._ast.ASTTypeArguments a = null;
a=de.monticore.types._ast.TypesNodeFactory.createASTTypeArguments();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    {ltLevel = ltCounter;}
    (
        ( LT   )  
        {ltCounter++;}
         tmp0=typeargument {a.getTypeArguments().add(_localctx.tmp0.ret);}   
        (
            ( COMMA   )  
             tmp1=typeargument {a.getTypeArguments().add(_localctx.tmp1.ret);}   
        )
        *
        (
            ( GT   )  
            {ltCounter -= 1;}
          |
            ( GTGT   )  
            {ltCounter -= 2;}
          |
            ( GTGTGT   )  
            {ltCounter -= 3;}
        )
        ?
    )

    {cmpCounter = ltLevel;}
    {(cmpCounter != 0) || ltCounter == cmpCounter}?
;




wildcardtype_eof returns [de.monticore.types._ast.ASTWildcardType ret = null] :
    tmp = wildcardtype  {$ret = $tmp.ret;} EOF ;

wildcardtype returns [de.monticore.types._ast.ASTWildcardType ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.types._ast.ASTWildcardType a = null;
a=de.monticore.types._ast.TypesNodeFactory.createASTWildcardType();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( QUESTION   )  
    (
        (
            ( 'extends'   )  
             tmp0=type {a.setUpperBound(_localctx.tmp0.ret);}   
        )

      |
        (
            ( 'super'   )  
             tmp1=type {a.setLowerBound(_localctx.tmp1.ret);}   
        )

    )
    ?
;




typeparameters_eof returns [de.monticore.types._ast.ASTTypeParameters ret = null] :
    tmp = typeparameters  {$ret = $tmp.ret;} EOF ;

typeparameters returns [de.monticore.types._ast.ASTTypeParameters ret = null]  
@init{int ltLevel = 0;// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.types._ast.ASTTypeParameters a = null;
a=de.monticore.types._ast.TypesNodeFactory.createASTTypeParameters();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    {ltLevel = ltCounter;}
    (
        ( LT   )  
        {ltCounter++;}
         tmp0=typevariabledeclaration {a.getTypeVariableDeclarations().add(_localctx.tmp0.ret);}   
        (
            ( COMMA   )  
             tmp1=typevariabledeclaration {a.getTypeVariableDeclarations().add(_localctx.tmp1.ret);}   
        )
        *
        (
            ( GT   )  
            {ltCounter -= 1;}
          |
            ( GTGT   )  
            {ltCounter -= 2;}
          |
            ( GTGTGT   )  
            {ltCounter -= 3;}
        )
        ?
    )

    {cmpCounter = ltLevel;}
    {(cmpCounter != 0) || ltCounter == cmpCounter}?
  |
;




typevariabledeclaration_eof returns [de.monticore.types._ast.ASTTypeVariableDeclaration ret = null] :
    tmp = typevariabledeclaration  {$ret = $tmp.ret;} EOF ;

typevariabledeclaration returns [de.monticore.types._ast.ASTTypeVariableDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.types._ast.ASTTypeVariableDeclaration a = null;
a=de.monticore.types._ast.TypesNodeFactory.createASTTypeVariableDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    (
        ( 'extends'   )  
         tmp1=complexreferencetype {a.getUpperBounds().add(_localctx.tmp1.ret);}   
        (
            ( AND   )  
             tmp2=complexreferencetype {a.getUpperBounds().add(_localctx.tmp2.ret);}   
        )
        *
    )
    ?
;




importstatement_eof returns [de.monticore.types._ast.ASTImportStatement ret = null] :
    tmp = importstatement  {$ret = $tmp.ret;} EOF ;

importstatement returns [de.monticore.types._ast.ASTImportStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.types._ast.ASTImportStatement a = null;
a=de.monticore.types._ast.TypesNodeFactory.createASTImportStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'import'   )  
    ( tmp0=Name  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add(convertName($tmp0));}
    /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, continue, else, options, catch, if, case, new, init, void, package, method, byte, double, antlr, finally, this, throws, lexer, push, enum, external, parser, null, extends, once, astimplements, true, try, returns, implements, import, concept, for, syn, global, interface, long, switch, pop, default, min, assert, class, EOF, inh, break, max, false, MCA, abstract, follow, int, instanceof, token, super, boolean, throw, char, short, return] */
     | ( 'astscript'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("astscript");} )  
     | ( 'synchronized'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("synchronized");} )  
     | ( 'ast'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("ast");} )  
     | ( 'do'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("do");} )  
     | ( 'while'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("while");} )  
     | ( 'float'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("float");} )  
     | ( 'astextends'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("astextends");} )  
     | ( 'grammar'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("grammar");} )  
     | ( 'java'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("java");} )  
     | ( 'continue'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("continue");} )  
     | ( 'else'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("else");} )  
     | ( 'options'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("options");} )  
     | ( 'catch'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("catch");} )  
     | ( 'if'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("if");} )  
     | ( 'case'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("case");} )  
     | ( 'new'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("new");} )  
     | ( 'init'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("init");} )  
     | ( 'void'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("void");} )  
     | ( 'package'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("package");} )  
     | ( 'method'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("method");} )  
     | ( 'byte'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("byte");} )  
     | ( 'double'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("double");} )  
     | ( 'antlr'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("antlr");} )  
     | ( 'finally'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("finally");} )  
     | ( 'this'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("this");} )  
     | ( 'throws'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("throws");} )  
     | ( 'lexer'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("lexer");} )  
     | ( 'push'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("push");} )  
     | ( 'enum'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("enum");} )  
     | ( 'external'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("external");} )  
     | ( 'parser'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("parser");} )  
     | ( 'null'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("null");} )  
     | ( 'extends'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("extends");} )  
     | ( 'once'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("once");} )  
     | ( 'astimplements'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("astimplements");} )  
     | ( 'true'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("true");} )  
     | ( 'try'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("try");} )  
     | ( 'returns'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("returns");} )  
     | ( 'implements'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("implements");} )  
     | ( 'import'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("import");} )  
     | ( 'concept'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("concept");} )  
     | ( 'for'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("for");} )  
     | ( 'syn'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("syn");} )  
     | ( 'global'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("global");} )  
     | ( 'interface'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("interface");} )  
     | ( 'long'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("long");} )  
     | ( 'switch'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("switch");} )  
     | ( 'pop'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("pop");} )  
     | ( 'default'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("default");} )  
     | ( 'min'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("min");} )  
     | ( 'assert'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("assert");} )  
     | ( 'class'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("class");} )  
     | ( 'EOF'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("EOF");} )  
     | ( 'inh'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("inh");} )  
     | ( 'break'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("break");} )  
     | ( 'max'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("max");} )  
     | ( 'false'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("false");} )  
     | ( 'MCA'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("MCA");} )  
     | ( 'abstract'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("abstract");} )  
     | ( 'follow'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("follow");} )  
     | ( 'int'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("int");} )  
     | ( 'instanceof'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("instanceof");} )  
     | ( 'token'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("token");} )  
     | ( 'super'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("super");} )  
     | ( 'boolean'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("boolean");} )  
     | ( 'throw'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("throw");} )  
     | ( 'char'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("char");} )  
     | ( 'short'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("short");} )  
     | ( 'return'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("return");} )  
    )  
    (
        ( POINT   )  
        ( tmp1=Name  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add(convertName($tmp1));}
        /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, continue, else, options, catch, if, case, new, init, void, package, method, byte, double, antlr, finally, this, throws, lexer, push, enum, external, parser, null, extends, once, astimplements, true, try, returns, implements, import, concept, for, syn, global, interface, long, switch, pop, default, min, assert, class, EOF, inh, break, max, false, MCA, abstract, follow, int, instanceof, token, super, boolean, throw, char, short, return] */
         | ( 'astscript'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("astscript");} )  
         | ( 'synchronized'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("synchronized");} )  
         | ( 'ast'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("ast");} )  
         | ( 'do'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("do");} )  
         | ( 'while'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("while");} )  
         | ( 'float'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("float");} )  
         | ( 'astextends'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("astextends");} )  
         | ( 'grammar'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("grammar");} )  
         | ( 'java'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("java");} )  
         | ( 'continue'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("continue");} )  
         | ( 'else'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("else");} )  
         | ( 'options'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("options");} )  
         | ( 'catch'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("catch");} )  
         | ( 'if'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("if");} )  
         | ( 'case'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("case");} )  
         | ( 'new'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("new");} )  
         | ( 'init'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("init");} )  
         | ( 'void'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("void");} )  
         | ( 'package'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("package");} )  
         | ( 'method'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("method");} )  
         | ( 'byte'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("byte");} )  
         | ( 'double'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("double");} )  
         | ( 'antlr'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("antlr");} )  
         | ( 'finally'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("finally");} )  
         | ( 'this'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("this");} )  
         | ( 'throws'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("throws");} )  
         | ( 'lexer'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("lexer");} )  
         | ( 'push'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("push");} )  
         | ( 'enum'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("enum");} )  
         | ( 'external'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("external");} )  
         | ( 'parser'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("parser");} )  
         | ( 'null'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("null");} )  
         | ( 'extends'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("extends");} )  
         | ( 'once'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("once");} )  
         | ( 'astimplements'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("astimplements");} )  
         | ( 'true'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("true");} )  
         | ( 'try'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("try");} )  
         | ( 'returns'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("returns");} )  
         | ( 'implements'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("implements");} )  
         | ( 'import'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("import");} )  
         | ( 'concept'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("concept");} )  
         | ( 'for'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("for");} )  
         | ( 'syn'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("syn");} )  
         | ( 'global'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("global");} )  
         | ( 'interface'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("interface");} )  
         | ( 'long'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("long");} )  
         | ( 'switch'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("switch");} )  
         | ( 'pop'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("pop");} )  
         | ( 'default'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("default");} )  
         | ( 'min'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("min");} )  
         | ( 'assert'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("assert");} )  
         | ( 'class'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("class");} )  
         | ( 'EOF'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("EOF");} )  
         | ( 'inh'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("inh");} )  
         | ( 'break'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("break");} )  
         | ( 'max'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("max");} )  
         | ( 'false'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("false");} )  
         | ( 'MCA'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("MCA");} )  
         | ( 'abstract'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("abstract");} )  
         | ( 'follow'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("follow");} )  
         | ( 'int'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("int");} )  
         | ( 'instanceof'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("instanceof");} )  
         | ( 'token'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("token");} )  
         | ( 'super'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("super");} )  
         | ( 'boolean'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("boolean");} )  
         | ( 'throw'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("throw");} )  
         | ( 'char'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("char");} )  
         | ( 'short'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("short");} )  
         | ( 'return'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("return");} )  
        )  
    )
    *
    (
        ( POINT   )  
        STAR {a.setStar(true);}
    )
    ?
    ( SEMI   )  
;




compilationunit_eof returns [mc.javadsl._ast.ASTCompilationUnit ret = null] :
    tmp = compilationunit  {$ret = $tmp.ret;} EOF ;

compilationunit returns [mc.javadsl._ast.ASTCompilationUnit ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTCompilationUnit a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTCompilationUnit();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=packagedeclaration {a.setPackageDeclaration(_localctx.tmp0.ret);}   
    )
    ?
    (
         tmp1=importdeclaration {a.getImportDeclarations().add(_localctx.tmp1.ret);}   
    )
    *
    (
         tmp2=typedeclaration {a.getTypeDeclarations().add(_localctx.tmp2.ret);}   
      |
        ( SEMI   )  
    )
    *
;




packagedeclaration_eof returns [mc.javadsl._ast.ASTPackageDeclaration ret = null] :
    tmp = packagedeclaration  {$ret = $tmp.ret;} EOF ;

packagedeclaration returns [mc.javadsl._ast.ASTPackageDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTPackageDeclaration a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTPackageDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=annotation {a.getModifiers().add(_localctx.tmp0.ret);}   
    )
    *
    ( 'package'   )  
    ( tmp1=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp1));})  
    (
        ( POINT   )  
        ( tmp2=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp2));})  
    )
    *
    ( SEMI   )  
;




importdeclaration_eof returns [mc.javadsl._ast.ASTImportDeclaration ret = null] :
    tmp = importdeclaration  {$ret = $tmp.ret;} EOF ;

importdeclaration returns [mc.javadsl._ast.ASTImportDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTImportDeclaration a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTImportDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'import'   )  
    (
        'static' {a.setIsStatic(true);}
    )
    ?
    ( tmp0=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp0));})  
    (
        ( POINT   )  
        ( tmp1=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp1));})  
    )
    *
    (
        ( POINT   )  
        STAR {a.setIsOnDemand(true);}
    )
    ?
    ( SEMI   )  
;




variabledeclaration_eof returns [mc.javadsl._ast.ASTVariableDeclaration ret = null] :
    tmp = variabledeclaration  {$ret = $tmp.ret;} EOF ;

variabledeclaration returns [mc.javadsl._ast.ASTVariableDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTVariableDeclaration a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTVariableDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=modifier {a.getModifiers().add(_localctx.tmp0.ret);}   
    )
    *
     tmp1=type {a.setType(_localctx.tmp1.ret);}   
     tmp2=variabledeclarator {a.getDeclarators().add(_localctx.tmp2.ret);}   
    (
        ( COMMA   )  
         tmp3=variabledeclarator {a.getDeclarators().add(_localctx.tmp3.ret);}   
    )
    *
;




variabledeclarationstatement_eof returns [mc.javadsl._ast.ASTVariableDeclarationStatement ret = null] :
    tmp = variabledeclarationstatement  {$ret = $tmp.ret;} EOF ;

variabledeclarationstatement returns [mc.javadsl._ast.ASTVariableDeclarationStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTVariableDeclarationStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTVariableDeclarationStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=variabledeclaration {a.setDeclaration(_localctx.tmp0.ret);}   
    ( SEMI   )  
;




variabledeclarationexpression_eof returns [mc.javadsl._ast.ASTVariableDeclarationExpression ret = null] :
    tmp = variabledeclarationexpression  {$ret = $tmp.ret;} EOF ;

variabledeclarationexpression returns [mc.javadsl._ast.ASTVariableDeclarationExpression ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTVariableDeclarationExpression a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTVariableDeclarationExpression();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=variabledeclaration {a.setDeclaration(_localctx.tmp0.ret);}   
;




primitivemodifier_eof returns [mc.javadsl._ast.ASTPrimitiveModifier ret = null] :
    tmp = primitivemodifier  {$ret = $tmp.ret;} EOF ;

primitivemodifier returns [mc.javadsl._ast.ASTPrimitiveModifier ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTPrimitiveModifier a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTPrimitiveModifier();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
    'private' {a.setModifier(mc.javadsl._ast.ASTConstantsJavaDSL.PRIVATE);}
    |
    'public' {a.setModifier(mc.javadsl._ast.ASTConstantsJavaDSL.PUBLIC);}
    |
    'protected' {a.setModifier(mc.javadsl._ast.ASTConstantsJavaDSL.PROTECTED);}
    |
    'static' {a.setModifier(mc.javadsl._ast.ASTConstantsJavaDSL.STATIC);}
    |
    'transient' {a.setModifier(mc.javadsl._ast.ASTConstantsJavaDSL.TRANSIENT);}
    |
    'final' {a.setModifier(mc.javadsl._ast.ASTConstantsJavaDSL.FINAL);}
    |
    'abstract' {a.setModifier(mc.javadsl._ast.ASTConstantsJavaDSL.ABSTRACT);}
    |
    'native' {a.setModifier(mc.javadsl._ast.ASTConstantsJavaDSL.NATIVE);}
    |
    'threadsafe' {a.setModifier(mc.javadsl._ast.ASTConstantsJavaDSL.THREADSAFE);}
    |
    'synchronized' {a.setModifier(mc.javadsl._ast.ASTConstantsJavaDSL.SYNCHRONIZED);}
    |
    'const' {a.setModifier(mc.javadsl._ast.ASTConstantsJavaDSL.CONST);}
    |
    'volatile' {a.setModifier(mc.javadsl._ast.ASTConstantsJavaDSL.VOLATILE);}
    |
    'strictfp' {a.setModifier(mc.javadsl._ast.ASTConstantsJavaDSL.STRICTFP);}
    )
;




enumdeclaration_eof returns [mc.javadsl._ast.ASTEnumDeclaration ret = null] :
    tmp = enumdeclaration  {$ret = $tmp.ret;} EOF ;

enumdeclaration returns [mc.javadsl._ast.ASTEnumDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTEnumDeclaration a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTEnumDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=modifier {a.getModifiers().add(_localctx.tmp0.ret);}   
    )
    *
    ( 'enum'   )  
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
    (
        ( 'implements'   )  
         tmp2=type {a.getImplementedTypes().add(_localctx.tmp2.ret);}   
        (
            ( COMMA   )  
             tmp3=type {a.getImplementedTypes().add(_localctx.tmp3.ret);}   
        )
        *
    )
    ?
    ( LCURLY   )  
    (
         tmp4=enumconstantdeclaration {a.getEnumConstantDeclarations().add(_localctx.tmp4.ret);}   
        (
            ( COMMA   )  
             tmp5=enumconstantdeclaration {a.getEnumConstantDeclarations().add(_localctx.tmp5.ret);}   
        )
        *
    )
    ?
    (
        ( COMMA   )  
    )
    ?
    (
        ( SEMI   )  
        (
             tmp6=memberdeclaration {a.getMemberDeclarations().add(_localctx.tmp6.ret);}   
          |
            ( SEMI   )  
        )
        *
    )
    ?
    ( RCURLY   )  
;




enumconstantdeclaration_eof returns [mc.javadsl._ast.ASTEnumConstantDeclaration ret = null] :
    tmp = enumconstantdeclaration  {$ret = $tmp.ret;} EOF ;

enumconstantdeclaration returns [mc.javadsl._ast.ASTEnumConstantDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTEnumConstantDeclaration a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTEnumConstantDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=annotation {a.getAnnotations().add(_localctx.tmp0.ret);}   
    )
    *
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
    (
        ( LPAREN   )  
        (
             tmp2=expression {a.getArguments().add(_localctx.tmp2.ret);}   
            (
                ( COMMA   )  
                 tmp3=expression {a.getArguments().add(_localctx.tmp3.ret);}   
            )
            *
        )
        ?
        ( RPAREN   )  
    )
    ?
    (
        ( LCURLY   )  
        (
             tmp4=memberdeclaration {a.getMemberDeclarations().add(_localctx.tmp4.ret);}   
          |
            ( SEMI   )  
        )
        *
        ( RCURLY   )  
        {a.getMemberDeclarations().set_Existent(true);}
    )
    ?
;




annotationtypedeclaration_eof returns [mc.javadsl._ast.ASTAnnotationTypeDeclaration ret = null] :
    tmp = annotationtypedeclaration  {$ret = $tmp.ret;} EOF ;

annotationtypedeclaration returns [mc.javadsl._ast.ASTAnnotationTypeDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTAnnotationTypeDeclaration a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTAnnotationTypeDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=modifier {a.getModifiers().add(_localctx.tmp0.ret);}   
    )
    *
    ( AT   )  
    ( 'interface'   )  
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
    (
        ( 'extends'   )  
         tmp2=type {a.setExtendedAnnotation(_localctx.tmp2.ret);}   
    )
    ?
    ( LCURLY   )  
    (
         tmp3=annotationmemberdeclaration {a.getMemberDeclarations().add(_localctx.tmp3.ret);}   
      |
        ( SEMI   )  
    )
    *
    ( RCURLY   )  
;




annotationmethoddeclaration_eof returns [mc.javadsl._ast.ASTAnnotationMethodDeclaration ret = null] :
    tmp = annotationmethoddeclaration  {$ret = $tmp.ret;} EOF ;

annotationmethoddeclaration returns [mc.javadsl._ast.ASTAnnotationMethodDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTAnnotationMethodDeclaration a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTAnnotationMethodDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=modifier {a.getModifiers().add(_localctx.tmp0.ret);}   
    )
    *
     tmp1=returntype {a.setReturnType(_localctx.tmp1.ret);}   
    ( tmp2=Name  {a.setName(convertName($tmp2));})  
    ( LPAREN   )  
    ( RPAREN   )  
    (
        ( 'default'   )  
         tmp3=annotationmembervalue {a.setDefaultValue(_localctx.tmp3.ret);}   
    )
    ?
    ( SEMI   )  
;




mappedmemberannotation_eof returns [mc.javadsl._ast.ASTMappedMemberAnnotation ret = null] :
    tmp = mappedmemberannotation  {$ret = $tmp.ret;} EOF ;

mappedmemberannotation returns [mc.javadsl._ast.ASTMappedMemberAnnotation ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTMappedMemberAnnotation a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTMappedMemberAnnotation();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( AT   )  
    ( tmp0=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp0));})  
    (
        ( POINT   )  
        ( tmp1=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp1));})  
    )
    *
    ( LPAREN   )  
     tmp2=annotationmembervaluepair {a.getMemberValues().add(_localctx.tmp2.ret);}   
    (
        ( COMMA   )  
         tmp3=annotationmembervaluepair {a.getMemberValues().add(_localctx.tmp3.ret);}   
    )
    *
    ( RPAREN   )  
;




singlememberannotation_eof returns [mc.javadsl._ast.ASTSingleMemberAnnotation ret = null] :
    tmp = singlememberannotation  {$ret = $tmp.ret;} EOF ;

singlememberannotation returns [mc.javadsl._ast.ASTSingleMemberAnnotation ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTSingleMemberAnnotation a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTSingleMemberAnnotation();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( AT   )  
    ( tmp0=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp0));})  
    (
        ( POINT   )  
        ( tmp1=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp1));})  
    )
    *
    ( LPAREN   )  
     tmp2=annotationmembervalue {a.setMemberValue(_localctx.tmp2.ret);}   
    ( RPAREN   )  
;




markerannotation_eof returns [mc.javadsl._ast.ASTMarkerAnnotation ret = null] :
    tmp = markerannotation  {$ret = $tmp.ret;} EOF ;

markerannotation returns [mc.javadsl._ast.ASTMarkerAnnotation ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTMarkerAnnotation a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTMarkerAnnotation();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( AT   )  
    ( tmp0=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp0));})  
    (
        ( POINT   )  
        ( tmp1=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp1));})  
    )
    *
    (
        ( LPAREN   )  
        ( RPAREN   )  
    )
    ?
;




annotationmembervaluepair_eof returns [mc.javadsl._ast.ASTAnnotationMemberValuePair ret = null] :
    tmp = annotationmembervaluepair  {$ret = $tmp.ret;} EOF ;

annotationmembervaluepair returns [mc.javadsl._ast.ASTAnnotationMemberValuePair ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTAnnotationMemberValuePair a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTAnnotationMemberValuePair();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setMemberName(convertName($tmp0));})  
    ( EQUALS   )  
     tmp1=annotationmembervalue {a.setValue(_localctx.tmp1.ret);}   
;




annotationmembervalue_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = annotationmembervalue  {$ret = $tmp.ret;} EOF ;

annotationmembervalue returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTAnnotationMemberValue a = null;
}

:
     tmp0=annotation {_localctx.ret = _localctx.tmp0.ret;}   
  |
     tmp1=conditionalexpression {_localctx.ret = _localctx.tmp1.ret;}   
  |
     tmp2=annotationmemberarrayinitializer {_localctx.ret = _localctx.tmp2.ret;}   
;




annotationmemberarrayinitializer_eof returns [mc.javadsl._ast.ASTAnnotationMemberArrayInitializer ret = null] :
    tmp = annotationmemberarrayinitializer  {$ret = $tmp.ret;} EOF ;

annotationmemberarrayinitializer returns [mc.javadsl._ast.ASTAnnotationMemberArrayInitializer ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTAnnotationMemberArrayInitializer a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTAnnotationMemberArrayInitializer();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( LCURLY   )  
    (
         tmp0=annotationmembervalue {a.getInitializers().add(_localctx.tmp0.ret);}   
        (
            ( COMMA   )  
             tmp1=annotationmembervalue {a.getInitializers().add(_localctx.tmp1.ret);}   
        )
        *
    )
    ?
    (
        ( COMMA   )  
    )
    ?
    ( RCURLY   )  
;




classdeclaration_eof returns [mc.javadsl._ast.ASTClassDeclaration ret = null] :
    tmp = classdeclaration  {$ret = $tmp.ret;} EOF ;

classdeclaration returns [mc.javadsl._ast.ASTClassDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTClassDeclaration a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTClassDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=modifier {a.getModifiers().add(_localctx.tmp0.ret);}   
    )
    *
    ( 'class'   )  
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
     tmp2=typeparameters {a.setTypeParameters(_localctx.tmp2.ret);}   
    (
        ( 'extends'   )  
         tmp3=type {a.setExtendedClass(_localctx.tmp3.ret);}   
    )
    ?
    (
        ( 'implements'   )  
         tmp4=type {a.getImplementedInterfaces().add(_localctx.tmp4.ret);}   
        (
            ( COMMA   )  
             tmp5=type {a.getImplementedInterfaces().add(_localctx.tmp5.ret);}   
        )
        *
    )
    ?
    ( LCURLY   )  
    (
         tmp6=memberdeclaration {a.getMemberDeclarations().add(_localctx.tmp6.ret);}   
      |
        ( SEMI   )  
    )
    *
    ( RCURLY   )  
;




interfacedeclaration_eof returns [mc.javadsl._ast.ASTInterfaceDeclaration ret = null] :
    tmp = interfacedeclaration  {$ret = $tmp.ret;} EOF ;

interfacedeclaration returns [mc.javadsl._ast.ASTInterfaceDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTInterfaceDeclaration a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTInterfaceDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=modifier {a.getModifiers().add(_localctx.tmp0.ret);}   
    )
    *
    ( 'interface'   )  
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
     tmp2=typeparameters {a.setTypeParameters(_localctx.tmp2.ret);}   
    (
        ( 'extends'   )  
         tmp3=type {a.getExtendedInterfaces().add(_localctx.tmp3.ret);}   
        (
            ( COMMA   )  
             tmp4=type {a.getExtendedInterfaces().add(_localctx.tmp4.ret);}   
        )
        *
    )
    ?
    ( LCURLY   )  
    (
         tmp5=memberdeclaration {a.getMemberDeclarations().add(_localctx.tmp5.ret);}   
      |
        ( SEMI   )  
    )
    *
    ( RCURLY   )  
;




memberdeclarations_eof returns [mc.javadsl._ast.ASTMemberDeclarations ret = null] :
    tmp = memberdeclarations  {$ret = $tmp.ret;} EOF ;

memberdeclarations returns [mc.javadsl._ast.ASTMemberDeclarations ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTMemberDeclarations a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTMemberDeclarations();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=memberdeclaration {a.getMemberDeclaration().add(_localctx.tmp0.ret);} ) * 
;




typeinitializer_eof returns [mc.javadsl._ast.ASTTypeInitializer ret = null] :
    tmp = typeinitializer  {$ret = $tmp.ret;} EOF ;

typeinitializer returns [mc.javadsl._ast.ASTTypeInitializer ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTTypeInitializer a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTTypeInitializer();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        'static' {a.setIsStatic(true);}
    )
    ?
     tmp0=blockstatement {a.setBody(_localctx.tmp0.ret);}   
;




constructordeclaration_eof returns [mc.javadsl._ast.ASTConstructorDeclaration ret = null] :
    tmp = constructordeclaration  {$ret = $tmp.ret;} EOF ;

constructordeclaration returns [mc.javadsl._ast.ASTConstructorDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTConstructorDeclaration a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTConstructorDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=modifier {a.getModifiers().add(_localctx.tmp0.ret);}   
    )
    *
     tmp1=typeparameters {a.setTypeParameters(_localctx.tmp1.ret);}   
    ( tmp2=Name  {a.setName(convertName($tmp2));})  
    ( LPAREN   )  
    (
         tmp3=parameterdeclaration {a.getParameters().add(_localctx.tmp3.ret);}   
        (
            ( COMMA   )  
             tmp4=parameterdeclaration {a.getParameters().add(_localctx.tmp4.ret);}   
        )
        *
    )
    ?
    ( RPAREN   )  
    (
        ( 'throws'   )  
         tmp5=qualifiedname {a.getThrowables().add(_localctx.tmp5.ret);}   
        (
            ( COMMA   )  
             tmp6=qualifiedname {a.getThrowables().add(_localctx.tmp6.ret);}   
        )
        *
    )
    ?
     tmp7=blockstatement {a.setBlock(_localctx.tmp7.ret);}   
;




methoddeclaration_eof returns [mc.javadsl._ast.ASTMethodDeclaration ret = null] :
    tmp = methoddeclaration  {$ret = $tmp.ret;} EOF ;

methoddeclaration returns [mc.javadsl._ast.ASTMethodDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTMethodDeclaration a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTMethodDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=modifier {a.getModifiers().add(_localctx.tmp0.ret);}   
    )
    *
     tmp1=typeparameters {a.setTypeParameters(_localctx.tmp1.ret);}   
     tmp2=returntype {a.setReturnType(_localctx.tmp2.ret);}   
    ( tmp3=Name  {a.setName(convertName($tmp3));})  
    ( LPAREN   )  
    (
         tmp4=parameterdeclaration {a.getParameters().add(_localctx.tmp4.ret);}   
        (
            ( COMMA   )  
             tmp5=parameterdeclaration {a.getParameters().add(_localctx.tmp5.ret);}   
        )
        *
    )
    ?
    ( RPAREN   )  
    (
        ( LBRACK   )  
        ( RBRACK   )  
        {a.setAdditionReturnTypeDimensions(a.getAdditionReturnTypeDimensions() + 1);}
    )
    *
    (
        ( 'throws'   )  
         tmp6=qualifiedname {a.getThrowables().add(_localctx.tmp6.ret);}   
        (
            ( COMMA   )  
             tmp7=qualifiedname {a.getThrowables().add(_localctx.tmp7.ret);}   
        )
        *
    )
    ?
    (
        ( SEMI   )  
      |
         tmp8=blockstatement {a.setBlock(_localctx.tmp8.ret);}   
    )

;




methods_eof returns [mc.javadsl._ast.ASTMethods ret = null] :
    tmp = methods  {$ret = $tmp.ret;} EOF ;

methods returns [mc.javadsl._ast.ASTMethods ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTMethods a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTMethods();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=methoddeclaration {a.getMethodDeclaration().add(_localctx.tmp0.ret);} ) + 
;




fielddeclaration_eof returns [mc.javadsl._ast.ASTFieldDeclaration ret = null] :
    tmp = fielddeclaration  {$ret = $tmp.ret;} EOF ;

fielddeclaration returns [mc.javadsl._ast.ASTFieldDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTFieldDeclaration a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTFieldDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=variabledeclaration {a.setDeclaration(_localctx.tmp0.ret);}   
    ( SEMI   )  
;




variabledeclarator_eof returns [mc.javadsl._ast.ASTVariableDeclarator ret = null] :
    tmp = variabledeclarator  {$ret = $tmp.ret;} EOF ;

variabledeclarator returns [mc.javadsl._ast.ASTVariableDeclarator ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTVariableDeclarator a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTVariableDeclarator();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    (
        ( LBRACK   )  
        ( RBRACK   )  
        {a.setAdditionalArrayDimensions(a.getAdditionalArrayDimensions() + 1);}
    )
    *
    (
        ( EQUALS   )  
         tmp1=initializer {a.setInitializer(_localctx.tmp1.ret);}   
    )
    ?
;




arrayinitializer_eof returns [mc.javadsl._ast.ASTArrayInitializer ret = null] :
    tmp = arrayinitializer  {$ret = $tmp.ret;} EOF ;

arrayinitializer returns [mc.javadsl._ast.ASTArrayInitializer ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTArrayInitializer a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTArrayInitializer();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( LCURLY   )  
    (
         tmp0=initializer {a.getInitializers().add(_localctx.tmp0.ret);}   
        (
            ( COMMA   )  
             tmp1=initializer {a.getInitializers().add(_localctx.tmp1.ret);}   
        )
        *
    )
    ?
    (
        ( COMMA   )  
    )
    ?
    ( RCURLY   )  
;




initializer_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = initializer  {$ret = $tmp.ret;} EOF ;

initializer returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTInitializer a = null;
}

:
     tmp0=expression {_localctx.ret = _localctx.tmp0.ret;}   
;




parameterdeclaration_eof returns [mc.javadsl._ast.ASTParameterDeclaration ret = null] :
    tmp = parameterdeclaration  {$ret = $tmp.ret;} EOF ;

parameterdeclaration returns [mc.javadsl._ast.ASTParameterDeclaration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTParameterDeclaration a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTParameterDeclaration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=modifier {a.getModifiers().add(_localctx.tmp0.ret);}   
    )
    *
     tmp1=type {a.setType(_localctx.tmp1.ret);}   
    (
        POINTPOINTPOINT {a.setEllipsis(true);}
    )
    ?
    ( tmp2=Name  {a.setName(convertName($tmp2));})  
    (
        ( LBRACK   )  
        ( RBRACK   )  
        {a.setAdditionalArrayDimensions(a.getAdditionalArrayDimensions() + 1);}
    )
    *
;




blockstatement_eof returns [mc.javadsl._ast.ASTBlockStatement ret = null] :
    tmp = blockstatement  {$ret = $tmp.ret;} EOF ;

blockstatement returns [mc.javadsl._ast.ASTBlockStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTBlockStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTBlockStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( LCURLY   )  
    (
         tmp0=statement {a.getStatements().add(_localctx.tmp0.ret);}   
    )
    *
    ( RCURLY   )  
;




statements_eof returns [mc.javadsl._ast.ASTStatements ret = null] :
    tmp = statements  {$ret = $tmp.ret;} EOF ;

statements returns [mc.javadsl._ast.ASTStatements ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTStatements a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTStatements();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=statement {a.getStatement().add(_localctx.tmp0.ret);} ) * 
;




expressionstatement_eof returns [mc.javadsl._ast.ASTExpressionStatement ret = null] :
    tmp = expressionstatement  {$ret = $tmp.ret;} EOF ;

expressionstatement returns [mc.javadsl._ast.ASTExpressionStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTExpressionStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTExpressionStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=expression {a.setExpression(_localctx.tmp0.ret);}   
    ( SEMI   )  
;




typedeclarationstatement_eof returns [mc.javadsl._ast.ASTTypeDeclarationStatement ret = null] :
    tmp = typedeclarationstatement  {$ret = $tmp.ret;} EOF ;

typedeclarationstatement returns [mc.javadsl._ast.ASTTypeDeclarationStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTTypeDeclarationStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTTypeDeclarationStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=typedeclaration {a.setTypeDeclaration(_localctx.tmp0.ret);}   
;




emptystatement_eof returns [mc.javadsl._ast.ASTEmptyStatement ret = null] :
    tmp = emptystatement  {$ret = $tmp.ret;} EOF ;

emptystatement returns [mc.javadsl._ast.ASTEmptyStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTEmptyStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTEmptyStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( SEMI   )  
;




continuestatement_eof returns [mc.javadsl._ast.ASTContinueStatement ret = null] :
    tmp = continuestatement  {$ret = $tmp.ret;} EOF ;

continuestatement returns [mc.javadsl._ast.ASTContinueStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTContinueStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTContinueStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'continue'   )  
    (
        ( tmp0=Name  {a.setLabel(convertName($tmp0));})  
    )
    ?
    ( SEMI   )  
;




switchdefaultstatement_eof returns [mc.javadsl._ast.ASTSwitchDefaultStatement ret = null] :
    tmp = switchdefaultstatement  {$ret = $tmp.ret;} EOF ;

switchdefaultstatement returns [mc.javadsl._ast.ASTSwitchDefaultStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTSwitchDefaultStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTSwitchDefaultStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'default'   )  
;




breakstatement_eof returns [mc.javadsl._ast.ASTBreakStatement ret = null] :
    tmp = breakstatement  {$ret = $tmp.ret;} EOF ;

breakstatement returns [mc.javadsl._ast.ASTBreakStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTBreakStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTBreakStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'break'   )  
    (
        ( tmp0=Name  {a.setLabel(convertName($tmp0));})  
    )
    ?
    ( SEMI   )  
;




returnstatement_eof returns [mc.javadsl._ast.ASTReturnStatement ret = null] :
    tmp = returnstatement  {$ret = $tmp.ret;} EOF ;

returnstatement returns [mc.javadsl._ast.ASTReturnStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTReturnStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTReturnStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'return'   )  
    (
         tmp0=expression {a.setExpression(_localctx.tmp0.ret);}   
    )
    ?
    ( SEMI   )  
;




switchstatement_eof returns [mc.javadsl._ast.ASTSwitchStatement ret = null] :
    tmp = switchstatement  {$ret = $tmp.ret;} EOF ;

switchstatement returns [mc.javadsl._ast.ASTSwitchStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTSwitchStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTSwitchStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'switch'   )  
    ( LPAREN   )  
     tmp0=expression {a.setExpression(_localctx.tmp0.ret);}   
    ( RPAREN   )  
    ( LCURLY   )  
    (
        (
            (
                 tmp1=casestatement {a.getStatements().add(_localctx.tmp1.ret);}   
              |
                 tmp2=switchdefaultstatement {a.getStatements().add(_localctx.tmp2.ret);}   
            )

            ( COLON   )  
        )
        +
        (
             tmp3=statement {a.getStatements().add(_localctx.tmp3.ret);}   
        )
        *
    )
    *
    ( RCURLY   )  
;




casestatement_eof returns [mc.javadsl._ast.ASTCaseStatement ret = null] :
    tmp = casestatement  {$ret = $tmp.ret;} EOF ;

casestatement returns [mc.javadsl._ast.ASTCaseStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTCaseStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTCaseStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'case'   )  
     tmp0=expression {a.setExpression(_localctx.tmp0.ret);}   
;




throwstatement_eof returns [mc.javadsl._ast.ASTThrowStatement ret = null] :
    tmp = throwstatement  {$ret = $tmp.ret;} EOF ;

throwstatement returns [mc.javadsl._ast.ASTThrowStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTThrowStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTThrowStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'throw'   )  
     tmp0=expression {a.setExpression(_localctx.tmp0.ret);}   
    ( SEMI   )  
;




assertstatement_eof returns [mc.javadsl._ast.ASTAssertStatement ret = null] :
    tmp = assertstatement  {$ret = $tmp.ret;} EOF ;

assertstatement returns [mc.javadsl._ast.ASTAssertStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTAssertStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTAssertStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'assert'   )  
     tmp0=expression {a.setAssertion(_localctx.tmp0.ret);}   
    (
        ( COLON   )  
         tmp1=expression {a.setMessage(_localctx.tmp1.ret);}   
    )
    ?
    ( SEMI   )  
;




whilestatement_eof returns [mc.javadsl._ast.ASTWhileStatement ret = null] :
    tmp = whilestatement  {$ret = $tmp.ret;} EOF ;

whilestatement returns [mc.javadsl._ast.ASTWhileStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTWhileStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTWhileStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'while'   )  
    ( LPAREN   )  
     tmp0=expression {a.setCondition(_localctx.tmp0.ret);}   
    ( RPAREN   )  
     tmp1=statement {a.setStatement(_localctx.tmp1.ret);}   
;




dowhilestatement_eof returns [mc.javadsl._ast.ASTDoWhileStatement ret = null] :
    tmp = dowhilestatement  {$ret = $tmp.ret;} EOF ;

dowhilestatement returns [mc.javadsl._ast.ASTDoWhileStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTDoWhileStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTDoWhileStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'do'   )  
     tmp0=statement {a.setStatement(_localctx.tmp0.ret);}   
    ( 'while'   )  
    ( LPAREN   )  
     tmp1=expression {a.setCondition(_localctx.tmp1.ret);}   
    ( RPAREN   )  
    ( SEMI   )  
;




foreachstatement_eof returns [mc.javadsl._ast.ASTForEachStatement ret = null] :
    tmp = foreachstatement  {$ret = $tmp.ret;} EOF ;

foreachstatement returns [mc.javadsl._ast.ASTForEachStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTForEachStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTForEachStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'for'   )  
    ( LPAREN   )  
     tmp0=parameterdeclaration {a.setVariableDeclaration(_localctx.tmp0.ret);}   
    ( COLON   )  
     tmp1=expression {a.setIterable(_localctx.tmp1.ret);}   
    ( RPAREN   )  
     tmp2=statement {a.setStatement(_localctx.tmp2.ret);}   
;




forstatement_eof returns [mc.javadsl._ast.ASTForStatement ret = null] :
    tmp = forstatement  {$ret = $tmp.ret;} EOF ;

forstatement returns [mc.javadsl._ast.ASTForStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTForStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTForStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'for'   )  
    ( LPAREN   )  
    (
         tmp0=variabledeclarationexpression {a.getInitializations().add(_localctx.tmp0.ret);}   
      |
         tmp1=expression {a.getInitializations().add(_localctx.tmp1.ret);}   
        (
            ( COMMA   )  
             tmp2=expression {a.getInitializations().add(_localctx.tmp2.ret);}   
        )
        *
    )
    ?
    ( SEMI   )  
    (
         tmp3=expression {a.setCondition(_localctx.tmp3.ret);}   
    )
    ?
    ( SEMI   )  
    (
         tmp4=expression {a.getUpdates().add(_localctx.tmp4.ret);}   
        (
            ( COMMA   )  
             tmp5=expression {a.getUpdates().add(_localctx.tmp5.ret);}   
        )
        *
    )
    ?
    ( RPAREN   )  
     tmp6=statement {a.setStatement(_localctx.tmp6.ret);}   
;




ifstatement_eof returns [mc.javadsl._ast.ASTIfStatement ret = null] :
    tmp = ifstatement  {$ret = $tmp.ret;} EOF ;

ifstatement returns [mc.javadsl._ast.ASTIfStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTIfStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTIfStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'if'   )  
    ( LPAREN   )  
     tmp0=expression {a.setCondition(_localctx.tmp0.ret);}   
    ( RPAREN   )  
     tmp1=statement {a.setSuccessStatement(_localctx.tmp1.ret);}   
    (
        ( 'else'   )  
         tmp2=statement {a.setOptionalElseStatement(_localctx.tmp2.ret);}   
    )
    ?
;




labeledstatement_eof returns [mc.javadsl._ast.ASTLabeledStatement ret = null] :
    tmp = labeledstatement  {$ret = $tmp.ret;} EOF ;

labeledstatement returns [mc.javadsl._ast.ASTLabeledStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTLabeledStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTLabeledStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setLabel(convertName($tmp0));})  
    ( COLON   )  
     tmp1=statement {a.setStatement(_localctx.tmp1.ret);}   
;




synchronizedstatement_eof returns [mc.javadsl._ast.ASTSynchronizedStatement ret = null] :
    tmp = synchronizedstatement  {$ret = $tmp.ret;} EOF ;

synchronizedstatement returns [mc.javadsl._ast.ASTSynchronizedStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTSynchronizedStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTSynchronizedStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'synchronized'   )  
    ( LPAREN   )  
     tmp0=expression {a.setExpression(_localctx.tmp0.ret);}   
    ( RPAREN   )  
     tmp1=blockstatement {a.setBlock(_localctx.tmp1.ret);}   
;




trystatement_eof returns [mc.javadsl._ast.ASTTryStatement ret = null] :
    tmp = trystatement  {$ret = $tmp.ret;} EOF ;

trystatement returns [mc.javadsl._ast.ASTTryStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTTryStatement a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTTryStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'try'   )  
     tmp0=blockstatement {a.setBlock(_localctx.tmp0.ret);}   
    (
         tmp1=catchclause {a.getCatchClause().add(_localctx.tmp1.ret);}   
    )
    *
    (
         tmp2=finallyclause {a.setFinallyClause(_localctx.tmp2.ret);}   
    )
    ?
;




finallyclause_eof returns [mc.javadsl._ast.ASTFinallyClause ret = null] :
    tmp = finallyclause  {$ret = $tmp.ret;} EOF ;

finallyclause returns [mc.javadsl._ast.ASTFinallyClause ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTFinallyClause a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTFinallyClause();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'finally'   )  
     tmp0=blockstatement {a.setBlock(_localctx.tmp0.ret);}   
;




catchclause_eof returns [mc.javadsl._ast.ASTCatchClause ret = null] :
    tmp = catchclause  {$ret = $tmp.ret;} EOF ;

catchclause returns [mc.javadsl._ast.ASTCatchClause ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTCatchClause a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTCatchClause();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'catch'   )  
    ( LPAREN   )  
     tmp0=parameterdeclaration {a.setExceptionVariable(_localctx.tmp0.ret);}   
    ( RPAREN   )  
     tmp1=blockstatement {a.setBlock(_localctx.tmp1.ret);}   
;




assignmentexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = assignmentexpression  {$ret = $tmp.ret;} EOF ;

assignmentexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTAssignmentExpression a = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
     tmp1=conditionalexpression {_localctx.ret = _localctx.tmp1.ret;}   
    (
        {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTAssignmentExpression();
        a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
        a.set_SourcePositionStart(startPosition);
        a.setLeftHand($ret); 
        $ret=a;}
        (
        EQUALS {a.setAssignmentOperator(mc.javadsl._ast.ASTConstantsJavaDSL.EQUALS);}
        |
        PLUSEQUALS {a.setAssignmentOperator(mc.javadsl._ast.ASTConstantsJavaDSL.PLUSEQUALS);}
        |
        MINUSEQUALS {a.setAssignmentOperator(mc.javadsl._ast.ASTConstantsJavaDSL.MINUSEQUALS);}
        |
        STAREQUALS {a.setAssignmentOperator(mc.javadsl._ast.ASTConstantsJavaDSL.STAREQUALS);}
        |
        SLASHEQUALS {a.setAssignmentOperator(mc.javadsl._ast.ASTConstantsJavaDSL.SLASHEQUALS);}
        |
        PERCENTEQUALS {a.setAssignmentOperator(mc.javadsl._ast.ASTConstantsJavaDSL.PERCENTEQUALS);}
        |
        GTGTEQUALS {a.setAssignmentOperator(mc.javadsl._ast.ASTConstantsJavaDSL.GTGTEQUALS);}
        |
        GTGTGTEQUALS {a.setAssignmentOperator(mc.javadsl._ast.ASTConstantsJavaDSL.GTGTGTEQUALS);}
        |
        LTLTEQUALS {a.setAssignmentOperator(mc.javadsl._ast.ASTConstantsJavaDSL.LTLTEQUALS);}
        |
        ANDEQUALS {a.setAssignmentOperator(mc.javadsl._ast.ASTConstantsJavaDSL.ANDEQUALS);}
        |
        ROOFEQUALS {a.setAssignmentOperator(mc.javadsl._ast.ASTConstantsJavaDSL.ROOFEQUALS);}
        |
        PIPEEQUALS {a.setAssignmentOperator(mc.javadsl._ast.ASTConstantsJavaDSL.PIPEEQUALS);}
        )
         tmp2=assignmentexpression {a.setRightHand(_localctx.tmp2.ret);}   
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )
    ?
;




conditionalexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = conditionalexpression  {$ret = $tmp.ret;} EOF ;

conditionalexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTConditionalExpression a = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
     tmp1=logicalorexpression {_localctx.ret = _localctx.tmp1.ret;}   
    (
        {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTConditionalExpression();
        a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
        a.set_SourcePositionStart(startPosition);
        a.setCondition($ret); 
        $ret=a;}
        ( QUESTION   )  
         tmp2=assignmentexpression {a.setIfTrue(_localctx.tmp2.ret);}   
        ( COLON   )  
         tmp3=conditionalexpression {a.setIfFalse(_localctx.tmp3.ret);}   
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )
    ?
;




logicalorexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = logicalorexpression  {$ret = $tmp.ret;} EOF ;

logicalorexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTInfixExpression a = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
     tmp1=logicalandexpression {_localctx.ret = _localctx.tmp1.ret;}   
    (
        {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTInfixExpression();
        a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
        a.set_SourcePositionStart(startPosition);
        a.setLeftOperand($ret); 
        $ret=a;}
        (
        PIPEPIPE {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.PIPEPIPE);}
        )
         tmp2=logicalandexpression {a.setRightOperand(_localctx.tmp2.ret);}   
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )
    *
;




logicalandexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = logicalandexpression  {$ret = $tmp.ret;} EOF ;

logicalandexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTInfixExpression a = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
     tmp1=bitwiseorexpression {_localctx.ret = _localctx.tmp1.ret;}   
    (
        {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTInfixExpression();
        a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
        a.set_SourcePositionStart(startPosition);
        a.setLeftOperand($ret); 
        $ret=a;}
        (
        ANDAND {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.ANDAND);}
        )
         tmp2=bitwiseorexpression {a.setRightOperand(_localctx.tmp2.ret);}   
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )
    *
;




bitwiseorexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = bitwiseorexpression  {$ret = $tmp.ret;} EOF ;

bitwiseorexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTInfixExpression a = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
     tmp1=xorexpression {_localctx.ret = _localctx.tmp1.ret;}   
    (
        {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTInfixExpression();
        a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
        a.set_SourcePositionStart(startPosition);
        a.setLeftOperand($ret); 
        $ret=a;}
        (
        PIPE {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.PIPE);}
        )
         tmp2=xorexpression {a.setRightOperand(_localctx.tmp2.ret);}   
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )
    *
;




xorexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = xorexpression  {$ret = $tmp.ret;} EOF ;

xorexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTInfixExpression a = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
     tmp1=bitwiseandexpression {_localctx.ret = _localctx.tmp1.ret;}   
    (
        {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTInfixExpression();
        a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
        a.set_SourcePositionStart(startPosition);
        a.setLeftOperand($ret); 
        $ret=a;}
        (
        ROOF {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.ROOF);}
        )
         tmp2=bitwiseandexpression {a.setRightOperand(_localctx.tmp2.ret);}   
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )
    *
;




bitwiseandexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = bitwiseandexpression  {$ret = $tmp.ret;} EOF ;

bitwiseandexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTInfixExpression a = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
     tmp1=equalityexpression {_localctx.ret = _localctx.tmp1.ret;}   
    (
        {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTInfixExpression();
        a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
        a.set_SourcePositionStart(startPosition);
        a.setLeftOperand($ret); 
        $ret=a;}
        (
        AND {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.AND);}
        )
         tmp2=equalityexpression {a.setRightOperand(_localctx.tmp2.ret);}   
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )
    *
;




equalityexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = equalityexpression  {$ret = $tmp.ret;} EOF ;

equalityexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTInfixExpression a = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
     tmp1=relationalexpression {_localctx.ret = _localctx.tmp1.ret;}   
    (
        {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTInfixExpression();
        a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
        a.set_SourcePositionStart(startPosition);
        a.setLeftOperand($ret); 
        $ret=a;}
        (
        EXCLAMATIONMARKEQUALS {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.EXCLAMATIONMARKEQUALS);}
        |
        EQUALSEQUALS {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.EQUALSEQUALS);}
        )
         tmp2=relationalexpression {a.setRightOperand(_localctx.tmp2.ret);}   
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )
    *
;




relationalexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = relationalexpression  {$ret = $tmp.ret;} EOF ;

relationalexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTInfixExpression a = null;
mc.javadsl._ast.ASTInstanceOfExpression inst = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
     tmp1=shiftexpression {_localctx.ret = _localctx.tmp1.ret;}   
    (
        (
            {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTInfixExpression();
            a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
            a.set_SourcePositionStart(startPosition);
            a.setLeftOperand($ret); 
            $ret=a;}
            (
            LT {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.LT);}
            |
            GT {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.GT);}
            |
            LTEQUALS {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.LTEQUALS);}
            |
            GTEQUALS {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.GTEQUALS);}
            )
             tmp2=shiftexpression {a.setRightOperand(_localctx.tmp2.ret);}   
        {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
        )
        *
      |
         tmp3=instanceofexpression {inst = _localctx.tmp3.ret;}   
        {inst.setExpression($ret);
        $ret = inst;}
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )

;




instanceofexpression_eof returns [mc.javadsl._ast.ASTInstanceOfExpression ret = null] :
    tmp = instanceofexpression  {$ret = $tmp.ret;} EOF ;

instanceofexpression returns [mc.javadsl._ast.ASTInstanceOfExpression ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTInstanceOfExpression a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTInstanceOfExpression();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'instanceof'   )  
     tmp0=type {a.setType(_localctx.tmp0.ret);}   
;




shiftexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = shiftexpression  {$ret = $tmp.ret;} EOF ;

shiftexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTInfixExpression a = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
     tmp1=additiveexpression {_localctx.ret = _localctx.tmp1.ret;}   
    (
        {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTInfixExpression();
        a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
        a.set_SourcePositionStart(startPosition);
        a.setLeftOperand($ret); 
        $ret=a;}
        (
        LTLT {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.LTLT);}
        |
        GTGT {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.GTGT);}
        |
        GTGTGT {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.GTGTGT);}
        )
         tmp2=additiveexpression {a.setRightOperand(_localctx.tmp2.ret);}   
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )
    *
;




additiveexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = additiveexpression  {$ret = $tmp.ret;} EOF ;

additiveexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTInfixExpression a = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
     tmp1=multiplicativeexpression {_localctx.ret = _localctx.tmp1.ret;}   
    (
        {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTInfixExpression();
        a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
        a.set_SourcePositionStart(startPosition);
        a.setLeftOperand($ret); 
        $ret=a;}
        (
        PLUS {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.PLUS);}
        |
        MINUS {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.MINUS);}
        )
         tmp2=multiplicativeexpression {a.setRightOperand(_localctx.tmp2.ret);}   
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )
    *
;




multiplicativeexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = multiplicativeexpression  {$ret = $tmp.ret;} EOF ;

multiplicativeexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTInfixExpression a = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
     tmp1=prefixexpression {_localctx.ret = _localctx.tmp1.ret;}   
    (
        {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTInfixExpression();
        a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
        a.set_SourcePositionStart(startPosition);
        a.setLeftOperand($ret); 
        $ret=a;}
        (
        STAR {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.STAR);}
        |
        SLASH {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.SLASH);}
        |
        PERCENT {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.PERCENT);}
        )
         tmp2=prefixexpression {a.setRightOperand(_localctx.tmp2.ret);}   
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )
    *
;




prefixexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = prefixexpression  {$ret = $tmp.ret;} EOF ;

prefixexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTPrefixExpression a = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
    (
        (
            {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTPrefixExpression();
            a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
            a.set_SourcePositionStart(startPosition);
            $ret=a;}
            (
            PLUSPLUS {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.PLUSPLUS);}
            |
            MINUSMINUS {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.MINUSMINUS);}
            |
            MINUS {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.MINUS);}
            |
            PLUS {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.PLUS);}
            |
            TILDE {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.TILDE);}
            |
            EXCLAMATIONMARK {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.EXCLAMATIONMARK);}
            )
             tmp1=prefixexpression {a.setExpression(_localctx.tmp1.ret);}   
        {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
        )

      |
         tmp2=castexpression {_localctx.ret = _localctx.tmp2.ret;}   
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )

;




castexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = castexpression  {$ret = $tmp.ret;} EOF ;

castexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTCastExpression a = null;
boolean tmp0=true;
boolean tmp1=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
    (
        (
            {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTCastExpression();
            a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
            a.set_SourcePositionStart(startPosition);
            $ret=a;}
            ( LPAREN   )  
             tmp2=primitivearraytype {a.setPrimTargetType(_localctx.tmp2.ret);}   
            ( RPAREN   )  
             tmp3=prefixexpression {a.setExpression(_localctx.tmp3.ret);}   
        {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
        )

      |
        (
            {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTCastExpression();
            a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
            a.set_SourcePositionStart(startPosition);
            $ret=a;}
            ( LPAREN   )  
             tmp4=complexarraytype {a.setTargetType(_localctx.tmp4.ret);}   
            ( RPAREN   )  
             tmp5=castexpression {a.setExpression(_localctx.tmp5.ret);}   
        {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
        )

      |
         tmp6=postfixexpression {_localctx.ret = _localctx.tmp6.ret;}   
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )

;




postfixexpression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = postfixexpression  {$ret = $tmp.ret;} EOF ;

postfixexpression returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTPostfixExpression a = null;
boolean tmp0=true;
mc.ast.SourcePosition startPosition = computeStartPosition(_input.LT(1));}

:
     tmp1=primary {_localctx.ret = _localctx.tmp1.ret;}   
    (
        {a=mc.javadsl._ast.JavaDSLNodeFactory.createASTPostfixExpression();
        a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
        a.set_SourcePositionStart(startPosition);
        a.setExpression($ret); 
        $ret=a;}
        (
        PLUSPLUS {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.PLUSPLUS);}
        |
        MINUSMINUS {a.setOperator(mc.javadsl._ast.ASTConstantsJavaDSL.MINUSMINUS);}
        )
    {if ( a!=null ) {a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}}
    )
    ?
;




constructorinvocation_eof returns [mc.javadsl._ast.ASTConstructorInvocation ret = null] :
    tmp = constructorinvocation  {$ret = $tmp.ret;} EOF ;

constructorinvocation returns [mc.javadsl._ast.ASTConstructorInvocation ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTConstructorInvocation a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTConstructorInvocation();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=typearguments {a.setTypeArguments(_localctx.tmp0.ret);}   
    )
    ?
    (
         tmp1=thisreference {a.setReference(_localctx.tmp1.ret);}   
      |
         tmp2=superreference {a.setReference(_localctx.tmp2.ret);}   
    )

    ( LPAREN   )  
    (
         tmp3=expression {a.getArguments().add(_localctx.tmp3.ret);}   
        (
            ( COMMA   )  
             tmp4=expression {a.getArguments().add(_localctx.tmp4.ret);}   
        )
        *
    )
    ?
    ( RPAREN   )  
;




qualifiedconstructorinvocation_eof returns [mc.javadsl._ast.ASTConstructorInvocation ret = null] :
    tmp = qualifiedconstructorinvocation  {$ret = $tmp.ret;} EOF ;

qualifiedconstructorinvocation returns [mc.javadsl._ast.ASTConstructorInvocation ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTConstructorInvocation a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTConstructorInvocation();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=typearguments {a.setTypeArguments(_localctx.tmp0.ret);}   
    )
    ?
     tmp1=superreference {a.setReference(_localctx.tmp1.ret);}   
    {_localctx.tmp1.ret.setQualification($ret// TODO: Find better solution
    );}
    ( LPAREN   )  
    (
         tmp2=expression {a.getArguments().add(_localctx.tmp2.ret);}   
        (
            ( COMMA   )  
             tmp3=expression {a.getArguments().add(_localctx.tmp3.ret);}   
        )
        *
    )
    ?
    ( RPAREN   )  
;




primary_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = primary  {$ret = $tmp.ret;} EOF ;

primary returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTPrimary a = null;
mc.javadsl._ast.ASTThisReference qtype = null;
de.monticore.types._ast.ASTTypeArguments typeArguments = null;
mc.javadsl._ast.ASTMethodInvocation qual = null;
mc.javadsl._ast.ASTFieldAccess type = null;
mc.javadsl._ast.ASTSuperReference superReference = null;
mc.javadsl._ast.ASTArrayAccessExpression access = null;
}

:
     tmp0=primarysuffix {_localctx.ret = _localctx.tmp0.ret;}   
    (
        ( POINT   )  
         tmp1=thisreference {qtype = _localctx.tmp1.ret;}   
        {qtype.setQualification($ret);
        $ret = qtype;}
      |
        ( POINT   )  
        (
            (
                 tmp2=typearguments {typeArguments = _localctx.tmp2.ret;}   
            )
            ?
             tmp3=methodinvocationwithsinglename {qual = _localctx.tmp3.ret;}   
            {qual.setQualification($ret);
            if (typeArguments != null) qual.setTypeArguments(typeArguments);
            $ret = qual;}
          |
             tmp4=fieldaccess {type = _localctx.tmp4.ret;}   
            {type.setQualification($ret);
            $ret = type;}
          |
             tmp5=qualifiedconstructorinvocation {_localctx.ret = _localctx.tmp5.ret;}   
          |
             tmp6=superreference {superReference = _localctx.tmp6.ret;}   
            ( POINT   )  
            (
                (
                     tmp7=typearguments {typeArguments = _localctx.tmp7.ret;}   
                )
                ?
                 tmp8=methodinvocationwithsinglename {qual = _localctx.tmp8.ret;}   
                {qual.setQualification($ret);
                if (typeArguments != null) qual.setTypeArguments(typeArguments);
                $ret = qual;}
              |
                 tmp9=fieldaccess {_localctx.ret = _localctx.tmp9.ret;}   
            )

          |
             tmp10=newexpression {_localctx.ret = _localctx.tmp10.ret;}   
        )

      |
         tmp11=arrayaccessexpression {access = _localctx.tmp11.ret;}   
        {access.setReceiver($ret);
        $ret = access;}
    )
    *
;




thisreference_eof returns [mc.javadsl._ast.ASTThisReference ret = null] :
    tmp = thisreference  {$ret = $tmp.ret;} EOF ;

thisreference returns [mc.javadsl._ast.ASTThisReference ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTThisReference a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTThisReference();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'this'   )  
;




fieldaccess_eof returns [mc.javadsl._ast.ASTFieldAccess ret = null] :
    tmp = fieldaccess  {$ret = $tmp.ret;} EOF ;

fieldaccess returns [mc.javadsl._ast.ASTFieldAccess ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTFieldAccess a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTFieldAccess();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
;




methodinvocationwithsinglename_eof returns [mc.javadsl._ast.ASTMethodInvocation ret = null] :
    tmp = methodinvocationwithsinglename  {$ret = $tmp.ret;} EOF ;

methodinvocationwithsinglename returns [mc.javadsl._ast.ASTMethodInvocation ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTMethodInvocation a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTMethodInvocation();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp0));})  
    ( LPAREN   )  
    (
         tmp1=expression {a.getArguments().add(_localctx.tmp1.ret);}   
        (
            ( COMMA   )  
             tmp2=expression {a.getArguments().add(_localctx.tmp2.ret);}   
        )
        *
    )
    ?
    ( RPAREN   )  
;




methodinvocationwithqualifiedname_eof returns [mc.javadsl._ast.ASTMethodInvocation ret = null] :
    tmp = methodinvocationwithqualifiedname  {$ret = $tmp.ret;} EOF ;

methodinvocationwithqualifiedname returns [mc.javadsl._ast.ASTMethodInvocation ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTMethodInvocation a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTMethodInvocation();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp0));})  
    (
        ( POINT   )  
        ( tmp1=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp1));})  
    )
    *
    ( LPAREN   )  
    (
         tmp2=expression {a.getArguments().add(_localctx.tmp2.ret);}   
        (
            ( COMMA   )  
             tmp3=expression {a.getArguments().add(_localctx.tmp3.ret);}   
        )
        *
    )
    ?
    ( RPAREN   )  
;




arrayaccessexpression_eof returns [mc.javadsl._ast.ASTArrayAccessExpression ret = null] :
    tmp = arrayaccessexpression  {$ret = $tmp.ret;} EOF ;

arrayaccessexpression returns [mc.javadsl._ast.ASTArrayAccessExpression ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTArrayAccessExpression a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTArrayAccessExpression();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( LBRACK   )  
     tmp0=expression {a.setComponent(_localctx.tmp0.ret);}   
    ( RBRACK   )  
;




primarysuffix_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = primarysuffix  {$ret = $tmp.ret;} EOF ;

primarysuffix returns [mc.javadsl._ast.ASTExpression ret = null]  
@init{// ret is normally returned, a can be instanciated later
mc.javadsl._ast.ASTPrimarySuffix a = null;
}

:
     tmp0=classaccess {_localctx.ret = _localctx.tmp0.ret;}   
  |
    (
         tmp1=methodinvocationwithqualifiedname {_localctx.ret = _localctx.tmp1.ret;}   
    )

  |
     tmp2=qualifiednameexpression {_localctx.ret = _localctx.tmp2.ret;}   
  |
     tmp3=literalexpression {_localctx.ret = _localctx.tmp3.ret;}   
  |
     tmp4=newexpression {_localctx.ret = _localctx.tmp4.ret;}   
  |
     tmp5=constructorinvocation {_localctx.ret = _localctx.tmp5.ret;}   
  |
     tmp6=thisreference {_localctx.ret = _localctx.tmp6.ret;}   
  |
     tmp7=superreference {_localctx.ret = _localctx.tmp7.ret;}   
  |
     tmp8=parenthesizedexpression {_localctx.ret = _localctx.tmp8.ret;}   
;




literalexpression_eof returns [mc.javadsl._ast.ASTLiteralExpression ret = null] :
    tmp = literalexpression  {$ret = $tmp.ret;} EOF ;

literalexpression returns [mc.javadsl._ast.ASTLiteralExpression ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTLiteralExpression a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTLiteralExpression();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=literal {a.setLiteral(_localctx.tmp0.ret);}   
;




qualifiednameexpression_eof returns [mc.javadsl._ast.ASTQualifiedNameExpression ret = null] :
    tmp = qualifiednameexpression  {$ret = $tmp.ret;} EOF ;

qualifiednameexpression returns [mc.javadsl._ast.ASTQualifiedNameExpression ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTQualifiedNameExpression a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTQualifiedNameExpression();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=qualifiedname {a.setQualifiedName(_localctx.tmp0.ret);}   
;




parenthesizedexpression_eof returns [mc.javadsl._ast.ASTParenthesizedExpression ret = null] :
    tmp = parenthesizedexpression  {$ret = $tmp.ret;} EOF ;

parenthesizedexpression returns [mc.javadsl._ast.ASTParenthesizedExpression ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTParenthesizedExpression a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTParenthesizedExpression();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( LPAREN   )  
     tmp0=assignmentexpression {a.setExpression(_localctx.tmp0.ret);}   
    ( RPAREN   )  
;




classaccess_eof returns [mc.javadsl._ast.ASTClassAccess ret = null] :
    tmp = classaccess  {$ret = $tmp.ret;} EOF ;

classaccess returns [mc.javadsl._ast.ASTClassAccess ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTClassAccess a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTClassAccess();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=type {a.setType(_localctx.tmp0.ret);}   
      |
         tmp1=voidtype {a.setVoidType(_localctx.tmp1.ret);}   
    )

    ( POINT   )  
    ( 'class'   )  
;




superreference_eof returns [mc.javadsl._ast.ASTSuperReference ret = null] :
    tmp = superreference  {$ret = $tmp.ret;} EOF ;

superreference returns [mc.javadsl._ast.ASTSuperReference ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTSuperReference a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTSuperReference();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'super'   )  
;




newexpression_eof returns [mc.javadsl._ast.ASTNewExpression ret = null] :
    tmp = newexpression  {$ret = $tmp.ret;} EOF ;

newexpression returns [mc.javadsl._ast.ASTNewExpression ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTNewExpression a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTNewExpression();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'new'   )  
    (
         tmp0=typearguments {a.setTypeArgs(_localctx.tmp0.ret);}   
    )
    ?
    (
         tmp1=complexreferencetype {a.setType(_localctx.tmp1.ret);}   
      |
         tmp2=primitivetype {a.setPrimtype(_localctx.tmp2.ret);}   
    )

    (
         tmp3=instantiation {a.setInstantiation(_localctx.tmp3.ret);}   
    )

;




classinstantiation_eof returns [mc.javadsl._ast.ASTClassInstantiation ret = null] :
    tmp = classinstantiation  {$ret = $tmp.ret;} EOF ;

classinstantiation returns [mc.javadsl._ast.ASTClassInstantiation ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTClassInstantiation a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTClassInstantiation();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( LPAREN   )  
    (
         tmp0=expression {a.getConstructorArguments().add(_localctx.tmp0.ret);}   
        (
            ( COMMA   )  
             tmp1=expression {a.getConstructorArguments().add(_localctx.tmp1.ret);}   
        )
        *
    )
    ?
    ( RPAREN   )  
    (
        ( LCURLY   )  
        (
             tmp2=memberdeclaration {a.getMemberDeclarations().add(_localctx.tmp2.ret);}   
          |
            ( SEMI   )  
        )
        *
        ( RCURLY   )  
        {a.getMemberDeclarations().set_Existent(true);}
    )
    ?
;




arrayinstantiation_eof returns [mc.javadsl._ast.ASTArrayInstantiation ret = null] :
    tmp = arrayinstantiation  {$ret = $tmp.ret;} EOF ;

arrayinstantiation returns [mc.javadsl._ast.ASTArrayInstantiation ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
mc.javadsl._ast.ASTArrayInstantiation a = null;
a=mc.javadsl._ast.JavaDSLNodeFactory.createASTArrayInstantiation();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        (
            ( LBRACK   )  
            (
                ( tmp0=expression {a.getSizes().add(_localctx.tmp0.ret);} ) ? 
            )

            ( RBRACK   )  
            {a.setDims(a.getDims() + 1);}
        )
        +
      |
        (
            ( LBRACK   )  
            ( RBRACK   )  
            {a.setDims(a.getDims() + 1);}
        )
        +
         tmp1=arrayinitializer {a.setInitializer(_localctx.tmp1.ret);}   
    )

;




mcgrammar_eof returns [de.monticore.grammar._ast.ASTMCGrammar ret = null] :
    tmp = mcgrammar  {$ret = $tmp.ret;} EOF ;

mcgrammar returns [de.monticore.grammar._ast.ASTMCGrammar ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTMCGrammar a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTMCGrammar();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);

int _mccountergrammarOption=0;}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));
if (!checkMax(_mccountergrammarOption,1)) { String message = "Invalid maximal occurence for grammarOption in rule MCGrammar : Should be 1 but is "+_mccountergrammarOption+"!";
de.se_rwth.commons.logging.Log.error(message);setErrors(true);}
}
:
    (
        ( 'package'   )  
        (
            ( tmp0=Name  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add(convertName($tmp0));}
            /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, protected, continue, else, options, catch, if, case, new, init, void, package, static, method, byte, double, antlr, finally, this, strictfp, throws, lexer, push, enum, external, parser, null, extends, once, transient, astimplements, true, final, try, returns, implements, private, import, const, concept, for, syn, global, interface, long, switch, pop, default, min, public, native, assert, class, EOF, inh, break, max, false, MCA, volatile, abstract, follow, int, instanceof, token, super, boolean, throw, char, short, threadsafe, return] */
             | ( 'astscript'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("astscript");} )  
             | ( 'synchronized'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("synchronized");} )  
             | ( 'ast'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("ast");} )  
             | ( 'do'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("do");} )  
             | ( 'while'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("while");} )  
             | ( 'float'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("float");} )  
             | ( 'astextends'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("astextends");} )  
             | ( 'grammar'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("grammar");} )  
             | ( 'java'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("java");} )  
             | ( 'protected'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("protected");} )  
             | ( 'continue'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("continue");} )  
             | ( 'else'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("else");} )  
             | ( 'options'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("options");} )  
             | ( 'catch'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("catch");} )  
             | ( 'if'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("if");} )  
             | ( 'case'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("case");} )  
             | ( 'new'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("new");} )  
             | ( 'init'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("init");} )  
             | ( 'void'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("void");} )  
             | ( 'package'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("package");} )  
             | ( 'static'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("static");} )  
             | ( 'method'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("method");} )  
             | ( 'byte'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("byte");} )  
             | ( 'double'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("double");} )  
             | ( 'antlr'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("antlr");} )  
             | ( 'finally'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("finally");} )  
             | ( 'this'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("this");} )  
             | ( 'strictfp'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("strictfp");} )  
             | ( 'throws'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("throws");} )  
             | ( 'lexer'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("lexer");} )  
             | ( 'push'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("push");} )  
             | ( 'enum'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("enum");} )  
             | ( 'external'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("external");} )  
             | ( 'parser'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("parser");} )  
             | ( 'null'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("null");} )  
             | ( 'extends'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("extends");} )  
             | ( 'once'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("once");} )  
             | ( 'transient'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("transient");} )  
             | ( 'astimplements'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("astimplements");} )  
             | ( 'true'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("true");} )  
             | ( 'final'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("final");} )  
             | ( 'try'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("try");} )  
             | ( 'returns'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("returns");} )  
             | ( 'implements'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("implements");} )  
             | ( 'private'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("private");} )  
             | ( 'import'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("import");} )  
             | ( 'const'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("const");} )  
             | ( 'concept'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("concept");} )  
             | ( 'for'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("for");} )  
             | ( 'syn'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("syn");} )  
             | ( 'global'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("global");} )  
             | ( 'interface'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("interface");} )  
             | ( 'long'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("long");} )  
             | ( 'switch'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("switch");} )  
             | ( 'pop'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("pop");} )  
             | ( 'default'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("default");} )  
             | ( 'min'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("min");} )  
             | ( 'public'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("public");} )  
             | ( 'native'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("native");} )  
             | ( 'assert'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("assert");} )  
             | ( 'class'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("class");} )  
             | ( 'EOF'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("EOF");} )  
             | ( 'inh'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("inh");} )  
             | ( 'break'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("break");} )  
             | ( 'max'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("max");} )  
             | ( 'false'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("false");} )  
             | ( 'MCA'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("MCA");} )  
             | ( 'volatile'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("volatile");} )  
             | ( 'abstract'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("abstract");} )  
             | ( 'follow'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("follow");} )  
             | ( 'int'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("int");} )  
             | ( 'instanceof'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("instanceof");} )  
             | ( 'token'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("token");} )  
             | ( 'super'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("super");} )  
             | ( 'boolean'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("boolean");} )  
             | ( 'throw'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("throw");} )  
             | ( 'char'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("char");} )  
             | ( 'short'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("short");} )  
             | ( 'threadsafe'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("threadsafe");} )  
             | ( 'return'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("return");} )  
            )  
            (
                ( POINT   )  
                ( tmp1=Name  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add(convertName($tmp1));}
                /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, protected, continue, else, options, catch, if, case, new, init, void, package, static, method, byte, double, antlr, finally, this, strictfp, throws, lexer, push, enum, external, parser, null, extends, once, transient, astimplements, true, final, try, returns, implements, private, import, const, concept, for, syn, global, interface, long, switch, pop, default, min, public, native, assert, class, EOF, inh, break, max, false, MCA, volatile, abstract, follow, int, instanceof, token, super, boolean, throw, char, short, threadsafe, return] */
                 | ( 'astscript'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("astscript");} )  
                 | ( 'synchronized'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("synchronized");} )  
                 | ( 'ast'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("ast");} )  
                 | ( 'do'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("do");} )  
                 | ( 'while'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("while");} )  
                 | ( 'float'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("float");} )  
                 | ( 'astextends'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("astextends");} )  
                 | ( 'grammar'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("grammar");} )  
                 | ( 'java'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("java");} )  
                 | ( 'protected'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("protected");} )  
                 | ( 'continue'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("continue");} )  
                 | ( 'else'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("else");} )  
                 | ( 'options'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("options");} )  
                 | ( 'catch'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("catch");} )  
                 | ( 'if'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("if");} )  
                 | ( 'case'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("case");} )  
                 | ( 'new'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("new");} )  
                 | ( 'init'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("init");} )  
                 | ( 'void'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("void");} )  
                 | ( 'package'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("package");} )  
                 | ( 'static'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("static");} )  
                 | ( 'method'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("method");} )  
                 | ( 'byte'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("byte");} )  
                 | ( 'double'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("double");} )  
                 | ( 'antlr'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("antlr");} )  
                 | ( 'finally'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("finally");} )  
                 | ( 'this'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("this");} )  
                 | ( 'strictfp'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("strictfp");} )  
                 | ( 'throws'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("throws");} )  
                 | ( 'lexer'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("lexer");} )  
                 | ( 'push'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("push");} )  
                 | ( 'enum'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("enum");} )  
                 | ( 'external'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("external");} )  
                 | ( 'parser'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("parser");} )  
                 | ( 'null'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("null");} )  
                 | ( 'extends'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("extends");} )  
                 | ( 'once'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("once");} )  
                 | ( 'transient'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("transient");} )  
                 | ( 'astimplements'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("astimplements");} )  
                 | ( 'true'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("true");} )  
                 | ( 'final'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("final");} )  
                 | ( 'try'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("try");} )  
                 | ( 'returns'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("returns");} )  
                 | ( 'implements'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("implements");} )  
                 | ( 'private'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("private");} )  
                 | ( 'import'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("import");} )  
                 | ( 'const'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("const");} )  
                 | ( 'concept'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("concept");} )  
                 | ( 'for'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("for");} )  
                 | ( 'syn'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("syn");} )  
                 | ( 'global'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("global");} )  
                 | ( 'interface'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("interface");} )  
                 | ( 'long'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("long");} )  
                 | ( 'switch'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("switch");} )  
                 | ( 'pop'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("pop");} )  
                 | ( 'default'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("default");} )  
                 | ( 'min'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("min");} )  
                 | ( 'public'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("public");} )  
                 | ( 'native'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("native");} )  
                 | ( 'assert'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("assert");} )  
                 | ( 'class'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("class");} )  
                 | ( 'EOF'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("EOF");} )  
                 | ( 'inh'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("inh");} )  
                 | ( 'break'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("break");} )  
                 | ( 'max'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("max");} )  
                 | ( 'false'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("false");} )  
                 | ( 'MCA'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("MCA");} )  
                 | ( 'volatile'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("volatile");} )  
                 | ( 'abstract'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("abstract");} )  
                 | ( 'follow'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("follow");} )  
                 | ( 'int'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("int");} )  
                 | ( 'instanceof'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("instanceof");} )  
                 | ( 'token'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("token");} )  
                 | ( 'super'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("super");} )  
                 | ( 'boolean'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("boolean");} )  
                 | ( 'throw'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("throw");} )  
                 | ( 'char'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("char");} )  
                 | ( 'short'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("short");} )  
                 | ( 'threadsafe'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("threadsafe");} )  
                 | ( 'return'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("return");} )  
                )  
            )
            *
        )

        ( SEMI   )  
    )
    ?
    (
         tmp2=mcimportstatement {a.getImportStatements().add(_localctx.tmp2.ret);}   
    )
    *
    (
        'component' {a.setComponent(true);}
    )
    ?
    ( 'grammar'   )  
    ( tmp3=Name  {a.setName(convertName($tmp3));})  
    (
        ( 'extends'   )  
        (
             tmp4=grammarreference {a.getSupergrammar().add(_localctx.tmp4.ret);}   
            (
                ( COMMA   )  
                 tmp5=grammarreference {a.getSupergrammar().add(_localctx.tmp5.ret);}   
            )
            *
        )

    )
    ?
    ( LCURLY   )  
    (
         tmp6=grammaroption {a.setGrammarOption(_localctx.tmp6.ret);_mccountergrammarOption++;}   
      |
         tmp7=lexprod {a.getLexProd().add(_localctx.tmp7.ret);}   
      |
         tmp8=classprod {a.getClassProd().add(_localctx.tmp8.ret);}   
      |
         tmp9=enumprod {a.getEnumProd().add(_localctx.tmp9.ret);}   
      |
         tmp10=externalprod {a.getExternalProd().add(_localctx.tmp10.ret);}   
      |
         tmp11=interfaceprod {a.getInterfaceProd().add(_localctx.tmp11.ret);}   
      |
         tmp12=abstractprod {a.getAbstractProd().add(_localctx.tmp12.ret);}   
      |
         tmp13=astrule {a.getASTRule().add(_localctx.tmp13.ret);}   
      |
         tmp14=concept {a.getConcept().add(_localctx.tmp14.ret);}   
    )
    *
    ( RCURLY   )  
;




mcimportstatement_eof returns [de.monticore.grammar._ast.ASTMCImportStatement ret = null] :
    tmp = mcimportstatement  {$ret = $tmp.ret;} EOF ;

mcimportstatement returns [de.monticore.grammar._ast.ASTMCImportStatement ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTMCImportStatement a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTMCImportStatement();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'import'   )  
    ( tmp0=Name  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add(convertName($tmp0));}
    /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, protected, continue, else, options, catch, if, case, new, init, void, package, static, method, byte, double, antlr, finally, this, strictfp, throws, lexer, push, enum, external, parser, null, extends, once, transient, astimplements, true, final, try, returns, implements, private, import, const, concept, for, syn, global, interface, long, switch, pop, default, min, public, native, assert, class, EOF, inh, break, max, false, MCA, volatile, abstract, follow, int, instanceof, token, super, component, boolean, throw, char, short, threadsafe, return] */
     | ( 'astscript'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("astscript");} )  
     | ( 'synchronized'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("synchronized");} )  
     | ( 'ast'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("ast");} )  
     | ( 'do'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("do");} )  
     | ( 'while'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("while");} )  
     | ( 'float'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("float");} )  
     | ( 'astextends'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("astextends");} )  
     | ( 'grammar'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("grammar");} )  
     | ( 'java'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("java");} )  
     | ( 'protected'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("protected");} )  
     | ( 'continue'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("continue");} )  
     | ( 'else'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("else");} )  
     | ( 'options'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("options");} )  
     | ( 'catch'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("catch");} )  
     | ( 'if'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("if");} )  
     | ( 'case'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("case");} )  
     | ( 'new'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("new");} )  
     | ( 'init'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("init");} )  
     | ( 'void'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("void");} )  
     | ( 'package'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("package");} )  
     | ( 'static'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("static");} )  
     | ( 'method'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("method");} )  
     | ( 'byte'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("byte");} )  
     | ( 'double'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("double");} )  
     | ( 'antlr'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("antlr");} )  
     | ( 'finally'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("finally");} )  
     | ( 'this'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("this");} )  
     | ( 'strictfp'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("strictfp");} )  
     | ( 'throws'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("throws");} )  
     | ( 'lexer'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("lexer");} )  
     | ( 'push'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("push");} )  
     | ( 'enum'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("enum");} )  
     | ( 'external'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("external");} )  
     | ( 'parser'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("parser");} )  
     | ( 'null'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("null");} )  
     | ( 'extends'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("extends");} )  
     | ( 'once'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("once");} )  
     | ( 'transient'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("transient");} )  
     | ( 'astimplements'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("astimplements");} )  
     | ( 'true'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("true");} )  
     | ( 'final'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("final");} )  
     | ( 'try'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("try");} )  
     | ( 'returns'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("returns");} )  
     | ( 'implements'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("implements");} )  
     | ( 'private'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("private");} )  
     | ( 'import'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("import");} )  
     | ( 'const'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("const");} )  
     | ( 'concept'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("concept");} )  
     | ( 'for'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("for");} )  
     | ( 'syn'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("syn");} )  
     | ( 'global'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("global");} )  
     | ( 'interface'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("interface");} )  
     | ( 'long'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("long");} )  
     | ( 'switch'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("switch");} )  
     | ( 'pop'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("pop");} )  
     | ( 'default'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("default");} )  
     | ( 'min'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("min");} )  
     | ( 'public'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("public");} )  
     | ( 'native'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("native");} )  
     | ( 'assert'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("assert");} )  
     | ( 'class'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("class");} )  
     | ( 'EOF'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("EOF");} )  
     | ( 'inh'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("inh");} )  
     | ( 'break'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("break");} )  
     | ( 'max'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("max");} )  
     | ( 'false'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("false");} )  
     | ( 'MCA'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("MCA");} )  
     | ( 'volatile'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("volatile");} )  
     | ( 'abstract'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("abstract");} )  
     | ( 'follow'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("follow");} )  
     | ( 'int'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("int");} )  
     | ( 'instanceof'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("instanceof");} )  
     | ( 'token'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("token");} )  
     | ( 'super'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("super");} )  
     | ( 'component'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("component");} )  
     | ( 'boolean'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("boolean");} )  
     | ( 'throw'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("throw");} )  
     | ( 'char'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("char");} )  
     | ( 'short'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("short");} )  
     | ( 'threadsafe'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("threadsafe");} )  
     | ( 'return'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("return");} )  
    )  
    (
        ( POINT   )  
        ( tmp1=Name  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add(convertName($tmp1));}
        /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, protected, continue, else, options, catch, if, case, new, init, void, package, static, method, byte, double, antlr, finally, this, strictfp, throws, lexer, push, enum, external, parser, null, extends, once, transient, astimplements, true, final, try, returns, implements, private, import, const, concept, for, syn, global, interface, long, switch, pop, default, min, public, native, assert, class, EOF, inh, break, max, false, MCA, volatile, abstract, follow, int, instanceof, token, super, component, boolean, throw, char, short, threadsafe, return] */
         | ( 'astscript'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("astscript");} )  
         | ( 'synchronized'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("synchronized");} )  
         | ( 'ast'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("ast");} )  
         | ( 'do'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("do");} )  
         | ( 'while'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("while");} )  
         | ( 'float'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("float");} )  
         | ( 'astextends'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("astextends");} )  
         | ( 'grammar'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("grammar");} )  
         | ( 'java'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("java");} )  
         | ( 'protected'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("protected");} )  
         | ( 'continue'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("continue");} )  
         | ( 'else'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("else");} )  
         | ( 'options'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("options");} )  
         | ( 'catch'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("catch");} )  
         | ( 'if'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("if");} )  
         | ( 'case'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("case");} )  
         | ( 'new'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("new");} )  
         | ( 'init'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("init");} )  
         | ( 'void'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("void");} )  
         | ( 'package'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("package");} )  
         | ( 'static'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("static");} )  
         | ( 'method'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("method");} )  
         | ( 'byte'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("byte");} )  
         | ( 'double'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("double");} )  
         | ( 'antlr'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("antlr");} )  
         | ( 'finally'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("finally");} )  
         | ( 'this'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("this");} )  
         | ( 'strictfp'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("strictfp");} )  
         | ( 'throws'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("throws");} )  
         | ( 'lexer'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("lexer");} )  
         | ( 'push'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("push");} )  
         | ( 'enum'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("enum");} )  
         | ( 'external'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("external");} )  
         | ( 'parser'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("parser");} )  
         | ( 'null'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("null");} )  
         | ( 'extends'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("extends");} )  
         | ( 'once'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("once");} )  
         | ( 'transient'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("transient");} )  
         | ( 'astimplements'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("astimplements");} )  
         | ( 'true'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("true");} )  
         | ( 'final'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("final");} )  
         | ( 'try'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("try");} )  
         | ( 'returns'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("returns");} )  
         | ( 'implements'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("implements");} )  
         | ( 'private'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("private");} )  
         | ( 'import'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("import");} )  
         | ( 'const'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("const");} )  
         | ( 'concept'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("concept");} )  
         | ( 'for'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("for");} )  
         | ( 'syn'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("syn");} )  
         | ( 'global'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("global");} )  
         | ( 'interface'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("interface");} )  
         | ( 'long'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("long");} )  
         | ( 'switch'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("switch");} )  
         | ( 'pop'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("pop");} )  
         | ( 'default'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("default");} )  
         | ( 'min'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("min");} )  
         | ( 'public'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("public");} )  
         | ( 'native'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("native");} )  
         | ( 'assert'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("assert");} )  
         | ( 'class'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("class");} )  
         | ( 'EOF'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("EOF");} )  
         | ( 'inh'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("inh");} )  
         | ( 'break'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("break");} )  
         | ( 'max'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("max");} )  
         | ( 'false'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("false");} )  
         | ( 'MCA'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("MCA");} )  
         | ( 'volatile'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("volatile");} )  
         | ( 'abstract'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("abstract");} )  
         | ( 'follow'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("follow");} )  
         | ( 'int'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("int");} )  
         | ( 'instanceof'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("instanceof");} )  
         | ( 'token'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("token");} )  
         | ( 'super'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("super");} )  
         | ( 'component'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("component");} )  
         | ( 'boolean'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("boolean");} )  
         | ( 'throw'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("throw");} )  
         | ( 'char'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("char");} )  
         | ( 'short'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("short");} )  
         | ( 'threadsafe'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("threadsafe");} )  
         | ( 'return'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("return");} )  
        )  
    )
    *
    (
        ( POINT   )  
        STAR {a.setStar(true);}
    )
    ?
    ( SEMI   )  
;




grammarreference_eof returns [de.monticore.grammar._ast.ASTGrammarReference ret = null] :
    tmp = grammarreference  {$ret = $tmp.ret;} EOF ;

grammarreference returns [de.monticore.grammar._ast.ASTGrammarReference ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTGrammarReference a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTGrammarReference();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        ( tmp0=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp0));}
        /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, protected, continue, else, options, catch, if, case, new, init, void, package, static, method, byte, double, antlr, finally, this, strictfp, throws, lexer, push, enum, external, parser, null, extends, once, transient, astimplements, true, final, try, returns, implements, private, import, const, concept, for, syn, global, interface, long, switch, pop, default, min, public, native, assert, class, EOF, inh, break, max, false, MCA, volatile, abstract, follow, int, instanceof, token, super, component, boolean, throw, char, short, threadsafe, return] */
         | ( 'astscript'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astscript");} )  
         | ( 'synchronized'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("synchronized");} )  
         | ( 'ast'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("ast");} )  
         | ( 'do'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("do");} )  
         | ( 'while'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("while");} )  
         | ( 'float'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("float");} )  
         | ( 'astextends'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astextends");} )  
         | ( 'grammar'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("grammar");} )  
         | ( 'java'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("java");} )  
         | ( 'protected'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("protected");} )  
         | ( 'continue'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("continue");} )  
         | ( 'else'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("else");} )  
         | ( 'options'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("options");} )  
         | ( 'catch'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("catch");} )  
         | ( 'if'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("if");} )  
         | ( 'case'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("case");} )  
         | ( 'new'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("new");} )  
         | ( 'init'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("init");} )  
         | ( 'void'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("void");} )  
         | ( 'package'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("package");} )  
         | ( 'static'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("static");} )  
         | ( 'method'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("method");} )  
         | ( 'byte'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("byte");} )  
         | ( 'double'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("double");} )  
         | ( 'antlr'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("antlr");} )  
         | ( 'finally'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("finally");} )  
         | ( 'this'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("this");} )  
         | ( 'strictfp'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("strictfp");} )  
         | ( 'throws'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("throws");} )  
         | ( 'lexer'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("lexer");} )  
         | ( 'push'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("push");} )  
         | ( 'enum'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("enum");} )  
         | ( 'external'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("external");} )  
         | ( 'parser'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("parser");} )  
         | ( 'null'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("null");} )  
         | ( 'extends'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("extends");} )  
         | ( 'once'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("once");} )  
         | ( 'transient'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("transient");} )  
         | ( 'astimplements'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astimplements");} )  
         | ( 'true'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("true");} )  
         | ( 'final'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("final");} )  
         | ( 'try'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("try");} )  
         | ( 'returns'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("returns");} )  
         | ( 'implements'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("implements");} )  
         | ( 'private'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("private");} )  
         | ( 'import'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("import");} )  
         | ( 'const'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("const");} )  
         | ( 'concept'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("concept");} )  
         | ( 'for'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("for");} )  
         | ( 'syn'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("syn");} )  
         | ( 'global'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("global");} )  
         | ( 'interface'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("interface");} )  
         | ( 'long'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("long");} )  
         | ( 'switch'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("switch");} )  
         | ( 'pop'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("pop");} )  
         | ( 'default'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("default");} )  
         | ( 'min'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("min");} )  
         | ( 'public'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("public");} )  
         | ( 'native'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("native");} )  
         | ( 'assert'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("assert");} )  
         | ( 'class'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("class");} )  
         | ( 'EOF'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("EOF");} )  
         | ( 'inh'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("inh");} )  
         | ( 'break'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("break");} )  
         | ( 'max'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("max");} )  
         | ( 'false'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("false");} )  
         | ( 'MCA'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("MCA");} )  
         | ( 'volatile'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("volatile");} )  
         | ( 'abstract'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("abstract");} )  
         | ( 'follow'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("follow");} )  
         | ( 'int'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("int");} )  
         | ( 'instanceof'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("instanceof");} )  
         | ( 'token'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("token");} )  
         | ( 'super'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("super");} )  
         | ( 'component'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("component");} )  
         | ( 'boolean'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("boolean");} )  
         | ( 'throw'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("throw");} )  
         | ( 'char'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("char");} )  
         | ( 'short'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("short");} )  
         | ( 'threadsafe'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("threadsafe");} )  
         | ( 'return'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("return");} )  
        )  
        (
            ( POINT   )  
            ( tmp1=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp1));}
            /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, protected, continue, else, options, catch, if, case, new, init, void, package, static, method, byte, double, antlr, finally, this, strictfp, throws, lexer, push, enum, external, parser, null, extends, once, transient, astimplements, true, final, try, returns, implements, private, import, const, concept, for, syn, global, interface, long, switch, pop, default, min, public, native, assert, class, EOF, inh, break, max, false, MCA, volatile, abstract, follow, int, instanceof, token, super, component, boolean, throw, char, short, threadsafe, return] */
             | ( 'astscript'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astscript");} )  
             | ( 'synchronized'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("synchronized");} )  
             | ( 'ast'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("ast");} )  
             | ( 'do'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("do");} )  
             | ( 'while'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("while");} )  
             | ( 'float'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("float");} )  
             | ( 'astextends'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astextends");} )  
             | ( 'grammar'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("grammar");} )  
             | ( 'java'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("java");} )  
             | ( 'protected'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("protected");} )  
             | ( 'continue'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("continue");} )  
             | ( 'else'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("else");} )  
             | ( 'options'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("options");} )  
             | ( 'catch'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("catch");} )  
             | ( 'if'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("if");} )  
             | ( 'case'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("case");} )  
             | ( 'new'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("new");} )  
             | ( 'init'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("init");} )  
             | ( 'void'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("void");} )  
             | ( 'package'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("package");} )  
             | ( 'static'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("static");} )  
             | ( 'method'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("method");} )  
             | ( 'byte'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("byte");} )  
             | ( 'double'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("double");} )  
             | ( 'antlr'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("antlr");} )  
             | ( 'finally'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("finally");} )  
             | ( 'this'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("this");} )  
             | ( 'strictfp'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("strictfp");} )  
             | ( 'throws'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("throws");} )  
             | ( 'lexer'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("lexer");} )  
             | ( 'push'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("push");} )  
             | ( 'enum'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("enum");} )  
             | ( 'external'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("external");} )  
             | ( 'parser'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("parser");} )  
             | ( 'null'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("null");} )  
             | ( 'extends'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("extends");} )  
             | ( 'once'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("once");} )  
             | ( 'transient'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("transient");} )  
             | ( 'astimplements'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astimplements");} )  
             | ( 'true'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("true");} )  
             | ( 'final'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("final");} )  
             | ( 'try'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("try");} )  
             | ( 'returns'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("returns");} )  
             | ( 'implements'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("implements");} )  
             | ( 'private'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("private");} )  
             | ( 'import'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("import");} )  
             | ( 'const'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("const");} )  
             | ( 'concept'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("concept");} )  
             | ( 'for'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("for");} )  
             | ( 'syn'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("syn");} )  
             | ( 'global'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("global");} )  
             | ( 'interface'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("interface");} )  
             | ( 'long'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("long");} )  
             | ( 'switch'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("switch");} )  
             | ( 'pop'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("pop");} )  
             | ( 'default'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("default");} )  
             | ( 'min'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("min");} )  
             | ( 'public'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("public");} )  
             | ( 'native'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("native");} )  
             | ( 'assert'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("assert");} )  
             | ( 'class'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("class");} )  
             | ( 'EOF'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("EOF");} )  
             | ( 'inh'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("inh");} )  
             | ( 'break'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("break");} )  
             | ( 'max'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("max");} )  
             | ( 'false'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("false");} )  
             | ( 'MCA'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("MCA");} )  
             | ( 'volatile'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("volatile");} )  
             | ( 'abstract'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("abstract");} )  
             | ( 'follow'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("follow");} )  
             | ( 'int'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("int");} )  
             | ( 'instanceof'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("instanceof");} )  
             | ( 'token'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("token");} )  
             | ( 'super'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("super");} )  
             | ( 'component'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("component");} )  
             | ( 'boolean'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("boolean");} )  
             | ( 'throw'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("throw");} )  
             | ( 'char'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("char");} )  
             | ( 'short'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("short");} )  
             | ( 'threadsafe'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("threadsafe");} )  
             | ( 'return'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("return");} )  
            )  
        )
        *
    )

;




grammaroption_eof returns [de.monticore.grammar._ast.ASTGrammarOption ret = null] :
    tmp = grammaroption  {$ret = $tmp.ret;} EOF ;

grammaroption returns [de.monticore.grammar._ast.ASTGrammarOption ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTGrammarOption a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTGrammarOption();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'options'   )  
    ( LCURLY   )  
    (
         tmp0=followoption {a.getFollowOption().add(_localctx.tmp0.ret);}   
      |
         tmp1=antlroption {a.getAntlrOption().add(_localctx.tmp1.ret);}   
    )
    *
    ( RCURLY   )  
;




followoption_eof returns [de.monticore.grammar._ast.ASTFollowOption ret = null] :
    tmp = followoption  {$ret = $tmp.ret;} EOF ;

followoption returns [de.monticore.grammar._ast.ASTFollowOption ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTFollowOption a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTFollowOption();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'follow'   )  
    ( tmp0=Name  {a.setProdName(convertName($tmp0));})  
     tmp1=alt {a.setAlt(_localctx.tmp1.ret);}   
    ( SEMI   )  
;




antlroption_eof returns [de.monticore.grammar._ast.ASTAntlrOption ret = null] :
    tmp = antlroption  {$ret = $tmp.ret;} EOF ;

antlroption returns [de.monticore.grammar._ast.ASTAntlrOption ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTAntlrOption a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTAntlrOption();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    (
        ( EQUALS   )  
        ( tmp1=Name  {a.setValue(convertName($tmp1));})  
      |
        ( EQUALS   )  
        ( tmp2=String  {a.setValue(convertString($tmp2));})  
    )
    ?
;




lexprod_eof returns [de.monticore.grammar._ast.ASTLexProd ret = null] :
    tmp = lexprod  {$ret = $tmp.ret;} EOF ;

lexprod returns [de.monticore.grammar._ast.ASTLexProd ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTLexProd a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTLexProd();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        'fragment' {a.setFragment(true);}
      |
        'comment' {a.setComment(true);}
    )
    *
    (
        ( 'token'   )  
    )

    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    (
        (
             tmp1=lexoption {a.setLexOption(_localctx.tmp1.ret);}   
            (
                ( LCURLY   )  
                 tmp2=action {a.setInitAction(_localctx.tmp2.ret);}   
                ( RCURLY   )  
            )
            ?
          |
            ( LCURLY   )  
             tmp3=action {a.setInitAction(_localctx.tmp3.ret);}   
            ( RCURLY   )  
        )
        ?
        ( EQUALS   )  
        (
             tmp4=lexalt {a.getAlts().add(_localctx.tmp4.ret);}   
            (
                ( PIPE   )  
                 tmp5=lexalt {a.getAlts().add(_localctx.tmp5.ret);}   
            )
            *
        )

        (
            ( COLON   )  
            (
                ( LCURLY   )  
                 tmp6=action {a.setEndAction(_localctx.tmp6.ret);}   
                ( RCURLY   )  
            )
            ?
            (
                ( tmp7=Name  {a.setVariable(convertName($tmp7));}
                /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, protected, continue, else, options, catch, if, case, new, init, void, package, static, method, byte, double, antlr, finally, this, strictfp, throws, lexer, push, enum, external, parser, null, extends, once, transient, astimplements, true, final, try, returns, implements, private, import, const, concept, for, syn, global, interface, long, switch, pop, default, min, public, native, assert, class, EOF, inh, break, max, false, MCA, volatile, abstract, follow, int, instanceof, token, super, component, fragment, boolean, throw, char, short, comment, threadsafe, return] */
                 | ( 'astscript'  {a.setVariable(new String("astscript").intern());} )  
                 | ( 'synchronized'  {a.setVariable(new String("synchronized").intern());} )  
                 | ( 'ast'  {a.setVariable(new String("ast").intern());} )  
                 | ( 'do'  {a.setVariable(new String("do").intern());} )  
                 | ( 'while'  {a.setVariable(new String("while").intern());} )  
                 | ( 'float'  {a.setVariable(new String("float").intern());} )  
                 | ( 'astextends'  {a.setVariable(new String("astextends").intern());} )  
                 | ( 'grammar'  {a.setVariable(new String("grammar").intern());} )  
                 | ( 'java'  {a.setVariable(new String("java").intern());} )  
                 | ( 'protected'  {a.setVariable(new String("protected").intern());} )  
                 | ( 'continue'  {a.setVariable(new String("continue").intern());} )  
                 | ( 'else'  {a.setVariable(new String("else").intern());} )  
                 | ( 'options'  {a.setVariable(new String("options").intern());} )  
                 | ( 'catch'  {a.setVariable(new String("catch").intern());} )  
                 | ( 'if'  {a.setVariable(new String("if").intern());} )  
                 | ( 'case'  {a.setVariable(new String("case").intern());} )  
                 | ( 'new'  {a.setVariable(new String("new").intern());} )  
                 | ( 'init'  {a.setVariable(new String("init").intern());} )  
                 | ( 'void'  {a.setVariable(new String("void").intern());} )  
                 | ( 'package'  {a.setVariable(new String("package").intern());} )  
                 | ( 'static'  {a.setVariable(new String("static").intern());} )  
                 | ( 'method'  {a.setVariable(new String("method").intern());} )  
                 | ( 'byte'  {a.setVariable(new String("byte").intern());} )  
                 | ( 'double'  {a.setVariable(new String("double").intern());} )  
                 | ( 'antlr'  {a.setVariable(new String("antlr").intern());} )  
                 | ( 'finally'  {a.setVariable(new String("finally").intern());} )  
                 | ( 'this'  {a.setVariable(new String("this").intern());} )  
                 | ( 'strictfp'  {a.setVariable(new String("strictfp").intern());} )  
                 | ( 'throws'  {a.setVariable(new String("throws").intern());} )  
                 | ( 'lexer'  {a.setVariable(new String("lexer").intern());} )  
                 | ( 'push'  {a.setVariable(new String("push").intern());} )  
                 | ( 'enum'  {a.setVariable(new String("enum").intern());} )  
                 | ( 'external'  {a.setVariable(new String("external").intern());} )  
                 | ( 'parser'  {a.setVariable(new String("parser").intern());} )  
                 | ( 'null'  {a.setVariable(new String("null").intern());} )  
                 | ( 'extends'  {a.setVariable(new String("extends").intern());} )  
                 | ( 'once'  {a.setVariable(new String("once").intern());} )  
                 | ( 'transient'  {a.setVariable(new String("transient").intern());} )  
                 | ( 'astimplements'  {a.setVariable(new String("astimplements").intern());} )  
                 | ( 'true'  {a.setVariable(new String("true").intern());} )  
                 | ( 'final'  {a.setVariable(new String("final").intern());} )  
                 | ( 'try'  {a.setVariable(new String("try").intern());} )  
                 | ( 'returns'  {a.setVariable(new String("returns").intern());} )  
                 | ( 'implements'  {a.setVariable(new String("implements").intern());} )  
                 | ( 'private'  {a.setVariable(new String("private").intern());} )  
                 | ( 'import'  {a.setVariable(new String("import").intern());} )  
                 | ( 'const'  {a.setVariable(new String("const").intern());} )  
                 | ( 'concept'  {a.setVariable(new String("concept").intern());} )  
                 | ( 'for'  {a.setVariable(new String("for").intern());} )  
                 | ( 'syn'  {a.setVariable(new String("syn").intern());} )  
                 | ( 'global'  {a.setVariable(new String("global").intern());} )  
                 | ( 'interface'  {a.setVariable(new String("interface").intern());} )  
                 | ( 'long'  {a.setVariable(new String("long").intern());} )  
                 | ( 'switch'  {a.setVariable(new String("switch").intern());} )  
                 | ( 'pop'  {a.setVariable(new String("pop").intern());} )  
                 | ( 'default'  {a.setVariable(new String("default").intern());} )  
                 | ( 'min'  {a.setVariable(new String("min").intern());} )  
                 | ( 'public'  {a.setVariable(new String("public").intern());} )  
                 | ( 'native'  {a.setVariable(new String("native").intern());} )  
                 | ( 'assert'  {a.setVariable(new String("assert").intern());} )  
                 | ( 'class'  {a.setVariable(new String("class").intern());} )  
                 | ( 'EOF'  {a.setVariable(new String("EOF").intern());} )  
                 | ( 'inh'  {a.setVariable(new String("inh").intern());} )  
                 | ( 'break'  {a.setVariable(new String("break").intern());} )  
                 | ( 'max'  {a.setVariable(new String("max").intern());} )  
                 | ( 'false'  {a.setVariable(new String("false").intern());} )  
                 | ( 'MCA'  {a.setVariable(new String("MCA").intern());} )  
                 | ( 'volatile'  {a.setVariable(new String("volatile").intern());} )  
                 | ( 'abstract'  {a.setVariable(new String("abstract").intern());} )  
                 | ( 'follow'  {a.setVariable(new String("follow").intern());} )  
                 | ( 'int'  {a.setVariable(new String("int").intern());} )  
                 | ( 'instanceof'  {a.setVariable(new String("instanceof").intern());} )  
                 | ( 'token'  {a.setVariable(new String("token").intern());} )  
                 | ( 'super'  {a.setVariable(new String("super").intern());} )  
                 | ( 'component'  {a.setVariable(new String("component").intern());} )  
                 | ( 'fragment'  {a.setVariable(new String("fragment").intern());} )  
                 | ( 'boolean'  {a.setVariable(new String("boolean").intern());} )  
                 | ( 'throw'  {a.setVariable(new String("throw").intern());} )  
                 | ( 'char'  {a.setVariable(new String("char").intern());} )  
                 | ( 'short'  {a.setVariable(new String("short").intern());} )  
                 | ( 'comment'  {a.setVariable(new String("comment").intern());} )  
                 | ( 'threadsafe'  {a.setVariable(new String("threadsafe").intern());} )  
                 | ( 'return'  {a.setVariable(new String("return").intern());} )  
                )  
                (
                    ( MINUSGT   )  
                    (
                        ( tmp8=Name  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add(convertName($tmp8));}
                        /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, protected, continue, else, options, catch, if, case, new, init, void, package, static, method, byte, double, antlr, finally, this, strictfp, throws, lexer, push, enum, external, parser, null, extends, once, transient, astimplements, true, final, try, returns, implements, private, import, const, concept, for, syn, global, interface, long, switch, pop, default, min, public, native, assert, class, EOF, inh, break, max, false, MCA, volatile, abstract, follow, int, instanceof, token, super, component, fragment, boolean, throw, char, short, comment, threadsafe, return] */
                         | ( 'astscript'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("astscript");} )  
                         | ( 'synchronized'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("synchronized");} )  
                         | ( 'ast'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("ast");} )  
                         | ( 'do'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("do");} )  
                         | ( 'while'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("while");} )  
                         | ( 'float'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("float");} )  
                         | ( 'astextends'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("astextends");} )  
                         | ( 'grammar'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("grammar");} )  
                         | ( 'java'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("java");} )  
                         | ( 'protected'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("protected");} )  
                         | ( 'continue'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("continue");} )  
                         | ( 'else'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("else");} )  
                         | ( 'options'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("options");} )  
                         | ( 'catch'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("catch");} )  
                         | ( 'if'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("if");} )  
                         | ( 'case'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("case");} )  
                         | ( 'new'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("new");} )  
                         | ( 'init'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("init");} )  
                         | ( 'void'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("void");} )  
                         | ( 'package'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("package");} )  
                         | ( 'static'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("static");} )  
                         | ( 'method'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("method");} )  
                         | ( 'byte'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("byte");} )  
                         | ( 'double'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("double");} )  
                         | ( 'antlr'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("antlr");} )  
                         | ( 'finally'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("finally");} )  
                         | ( 'this'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("this");} )  
                         | ( 'strictfp'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("strictfp");} )  
                         | ( 'throws'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("throws");} )  
                         | ( 'lexer'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("lexer");} )  
                         | ( 'push'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("push");} )  
                         | ( 'enum'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("enum");} )  
                         | ( 'external'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("external");} )  
                         | ( 'parser'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("parser");} )  
                         | ( 'null'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("null");} )  
                         | ( 'extends'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("extends");} )  
                         | ( 'once'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("once");} )  
                         | ( 'transient'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("transient");} )  
                         | ( 'astimplements'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("astimplements");} )  
                         | ( 'true'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("true");} )  
                         | ( 'final'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("final");} )  
                         | ( 'try'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("try");} )  
                         | ( 'returns'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("returns");} )  
                         | ( 'implements'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("implements");} )  
                         | ( 'private'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("private");} )  
                         | ( 'import'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("import");} )  
                         | ( 'const'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("const");} )  
                         | ( 'concept'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("concept");} )  
                         | ( 'for'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("for");} )  
                         | ( 'syn'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("syn");} )  
                         | ( 'global'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("global");} )  
                         | ( 'interface'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("interface");} )  
                         | ( 'long'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("long");} )  
                         | ( 'switch'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("switch");} )  
                         | ( 'pop'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("pop");} )  
                         | ( 'default'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("default");} )  
                         | ( 'min'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("min");} )  
                         | ( 'public'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("public");} )  
                         | ( 'native'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("native");} )  
                         | ( 'assert'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("assert");} )  
                         | ( 'class'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("class");} )  
                         | ( 'EOF'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("EOF");} )  
                         | ( 'inh'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("inh");} )  
                         | ( 'break'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("break");} )  
                         | ( 'max'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("max");} )  
                         | ( 'false'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("false");} )  
                         | ( 'MCA'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("MCA");} )  
                         | ( 'volatile'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("volatile");} )  
                         | ( 'abstract'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("abstract");} )  
                         | ( 'follow'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("follow");} )  
                         | ( 'int'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("int");} )  
                         | ( 'instanceof'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("instanceof");} )  
                         | ( 'token'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("token");} )  
                         | ( 'super'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("super");} )  
                         | ( 'component'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("component");} )  
                         | ( 'fragment'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("fragment");} )  
                         | ( 'boolean'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("boolean");} )  
                         | ( 'throw'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("throw");} )  
                         | ( 'char'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("char");} )  
                         | ( 'short'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("short");} )  
                         | ( 'comment'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("comment");} )  
                         | ( 'threadsafe'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("threadsafe");} )  
                         | ( 'return'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("return");} )  
                        )  
                        (
                            ( POINT   )  
                            ( tmp9=Name  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add(convertName($tmp9));}
                            /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, protected, continue, else, options, catch, if, case, new, init, void, package, static, method, byte, double, antlr, finally, this, strictfp, throws, lexer, push, enum, external, parser, null, extends, once, transient, astimplements, true, final, try, returns, implements, private, import, const, concept, for, syn, global, interface, long, switch, pop, default, min, public, native, assert, class, EOF, inh, break, max, false, MCA, volatile, abstract, follow, int, instanceof, token, super, component, fragment, boolean, throw, char, short, comment, threadsafe, return] */
                             | ( 'astscript'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("astscript");} )  
                             | ( 'synchronized'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("synchronized");} )  
                             | ( 'ast'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("ast");} )  
                             | ( 'do'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("do");} )  
                             | ( 'while'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("while");} )  
                             | ( 'float'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("float");} )  
                             | ( 'astextends'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("astextends");} )  
                             | ( 'grammar'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("grammar");} )  
                             | ( 'java'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("java");} )  
                             | ( 'protected'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("protected");} )  
                             | ( 'continue'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("continue");} )  
                             | ( 'else'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("else");} )  
                             | ( 'options'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("options");} )  
                             | ( 'catch'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("catch");} )  
                             | ( 'if'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("if");} )  
                             | ( 'case'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("case");} )  
                             | ( 'new'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("new");} )  
                             | ( 'init'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("init");} )  
                             | ( 'void'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("void");} )  
                             | ( 'package'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("package");} )  
                             | ( 'static'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("static");} )  
                             | ( 'method'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("method");} )  
                             | ( 'byte'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("byte");} )  
                             | ( 'double'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("double");} )  
                             | ( 'antlr'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("antlr");} )  
                             | ( 'finally'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("finally");} )  
                             | ( 'this'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("this");} )  
                             | ( 'strictfp'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("strictfp");} )  
                             | ( 'throws'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("throws");} )  
                             | ( 'lexer'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("lexer");} )  
                             | ( 'push'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("push");} )  
                             | ( 'enum'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("enum");} )  
                             | ( 'external'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("external");} )  
                             | ( 'parser'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("parser");} )  
                             | ( 'null'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("null");} )  
                             | ( 'extends'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("extends");} )  
                             | ( 'once'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("once");} )  
                             | ( 'transient'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("transient");} )  
                             | ( 'astimplements'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("astimplements");} )  
                             | ( 'true'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("true");} )  
                             | ( 'final'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("final");} )  
                             | ( 'try'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("try");} )  
                             | ( 'returns'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("returns");} )  
                             | ( 'implements'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("implements");} )  
                             | ( 'private'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("private");} )  
                             | ( 'import'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("import");} )  
                             | ( 'const'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("const");} )  
                             | ( 'concept'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("concept");} )  
                             | ( 'for'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("for");} )  
                             | ( 'syn'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("syn");} )  
                             | ( 'global'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("global");} )  
                             | ( 'interface'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("interface");} )  
                             | ( 'long'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("long");} )  
                             | ( 'switch'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("switch");} )  
                             | ( 'pop'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("pop");} )  
                             | ( 'default'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("default");} )  
                             | ( 'min'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("min");} )  
                             | ( 'public'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("public");} )  
                             | ( 'native'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("native");} )  
                             | ( 'assert'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("assert");} )  
                             | ( 'class'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("class");} )  
                             | ( 'EOF'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("EOF");} )  
                             | ( 'inh'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("inh");} )  
                             | ( 'break'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("break");} )  
                             | ( 'max'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("max");} )  
                             | ( 'false'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("false");} )  
                             | ( 'MCA'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("MCA");} )  
                             | ( 'volatile'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("volatile");} )  
                             | ( 'abstract'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("abstract");} )  
                             | ( 'follow'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("follow");} )  
                             | ( 'int'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("int");} )  
                             | ( 'instanceof'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("instanceof");} )  
                             | ( 'token'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("token");} )  
                             | ( 'super'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("super");} )  
                             | ( 'component'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("component");} )  
                             | ( 'fragment'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("fragment");} )  
                             | ( 'boolean'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("boolean");} )  
                             | ( 'throw'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("throw");} )  
                             | ( 'char'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("char");} )  
                             | ( 'short'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("short");} )  
                             | ( 'comment'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("comment");} )  
                             | ( 'threadsafe'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("threadsafe");} )  
                             | ( 'return'  {if (a.getType()==null){a.setType(new java.util.ArrayList());};  a.getType().add("return");} )  
                            )  
                        )
                        *
                    )

                    (
                        ( COLON   )  
                        ( LCURLY   )  
                         tmp10=action {a.setBlock(_localctx.tmp10.ret);}   
                        ( RCURLY   )  
                    )
                    ?
                )
                ?
            )
            ?
        )
        ?
    )

    ( SEMI   )  
;




enumprod_eof returns [de.monticore.grammar._ast.ASTEnumProd ret = null] :
    tmp = enumprod  {$ret = $tmp.ret;} EOF ;

enumprod returns [de.monticore.grammar._ast.ASTEnumProd ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTEnumProd a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTEnumProd();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'enum'   )  
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    ( EQUALS   )  
     tmp1=constant {a.getConstant().add(_localctx.tmp1.ret);}   
    (
        ( PIPE   )  
         tmp2=constant {a.getConstant().add(_localctx.tmp2.ret);}   
    )
    *
    ( SEMI   )  
;




externalprod_eof returns [de.monticore.grammar._ast.ASTExternalProd ret = null] :
    tmp = externalprod  {$ret = $tmp.ret;} EOF ;

externalprod returns [de.monticore.grammar._ast.ASTExternalProd ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTExternalProd a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTExternalProd();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'external'   )  
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    ( tmp1=generictype {a.setGenericType(_localctx.tmp1.ret);} ) ? 
    ( SEMI   )  
;




interfaceprod_eof returns [de.monticore.grammar._ast.ASTInterfaceProd ret = null] :
    tmp = interfaceprod  {$ret = $tmp.ret;} EOF ;

interfaceprod returns [de.monticore.grammar._ast.ASTInterfaceProd ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTInterfaceProd a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTInterfaceProd();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'interface'   )  
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    (
        ( 'extends'   )  
        (
             tmp1=rulereferencewithpredicates {a.getSuperInterfaceRule().add(_localctx.tmp1.ret);}   
            (
                ( COMMA   )  
                 tmp2=rulereferencewithpredicates {a.getSuperInterfaceRule().add(_localctx.tmp2.ret);}   
            )
            *
        )

      |
        ( 'astextends'   )  
        (
             tmp3=generictype {a.getASTSuperInterface().add(_localctx.tmp3.ret);}   
            (
                ( COMMA   )  
                 tmp4=generictype {a.getASTSuperInterface().add(_localctx.tmp4.ret);}   
            )
            *
        )

    )
    *
    ( SEMI   )  
;




abstractprod_eof returns [de.monticore.grammar._ast.ASTAbstractProd ret = null] :
    tmp = abstractprod  {$ret = $tmp.ret;} EOF ;

abstractprod returns [de.monticore.grammar._ast.ASTAbstractProd ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTAbstractProd a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTAbstractProd();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'abstract'   )  
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    (
        ( 'extends'   )  
        (
             tmp1=rulereferencewithpredicates {a.getSuperRule().add(_localctx.tmp1.ret);}   
            (
                ( COMMA   )  
                 tmp2=rulereferencewithpredicates {a.getSuperRule().add(_localctx.tmp2.ret);}   
            )
            *
        )

      |
        ( 'implements'   )  
        (
             tmp3=rulereferencewithpredicates {a.getSuperInterfaceRule().add(_localctx.tmp3.ret);}   
            (
                ( COMMA   )  
                 tmp4=rulereferencewithpredicates {a.getSuperInterfaceRule().add(_localctx.tmp4.ret);}   
            )
            *
        )

      |
        ( 'astextends'   )  
        (
             tmp5=generictype {a.getASTSuperClass().add(_localctx.tmp5.ret);}   
            (
                ( COMMA   )  
                 tmp6=generictype {a.getASTSuperClass().add(_localctx.tmp6.ret);}   
            )
            *
        )

      |
        ( 'astimplements'   )  
        (
             tmp7=generictype {a.getASTSuperInterface().add(_localctx.tmp7.ret);}   
            (
                ( COMMA   )  
                 tmp8=generictype {a.getASTSuperInterface().add(_localctx.tmp8.ret);}   
            )
            *
        )

    )
    *
    ( SEMI   )  
;




classprod_eof returns [de.monticore.grammar._ast.ASTClassProd ret = null] :
    tmp = classprod  {$ret = $tmp.ret;} EOF ;

classprod returns [de.monticore.grammar._ast.ASTClassProd ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTClassProd a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTClassProd();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    (
        ( COLON   )  
        ( tmp1=Name  {a.setType(convertName($tmp1));})  
    )
    ?
    (
        ( 'extends'   )  
        (
             tmp2=rulereference {a.getSuperRule().add(_localctx.tmp2.ret);}   
            (
                ( COMMA   )  
                 tmp3=rulereference {a.getSuperRule().add(_localctx.tmp3.ret);}   
            )
            *
        )

      |
        ( 'implements'   )  
        (
             tmp4=rulereference {a.getSuperInterfaceRule().add(_localctx.tmp4.ret);}   
            (
                ( COMMA   )  
                 tmp5=rulereference {a.getSuperInterfaceRule().add(_localctx.tmp5.ret);}   
            )
            *
        )

      |
        ( 'astextends'   )  
        (
             tmp6=generictype {a.getASTSuperClass().add(_localctx.tmp6.ret);}   
            (
                ( COMMA   )  
                 tmp7=generictype {a.getASTSuperClass().add(_localctx.tmp7.ret);}   
            )
            *
        )

      |
        ( 'astimplements'   )  
        (
             tmp8=generictype {a.getASTSuperInterface().add(_localctx.tmp8.ret);}   
            (
                ( COMMA   )  
                 tmp9=generictype {a.getASTSuperInterface().add(_localctx.tmp9.ret);}   
            )
            *
        )

    )
    *
    (
        ( 'returns'   )  
        ( tmp10=Name  {a.setReturnType(convertName($tmp10));})  
    )
    ?
    (
        ( LCURLY   )  
         tmp11=action {a.setAction(_localctx.tmp11.ret);}   
        ( RCURLY   )  
    )
    ?
    (
        ( EQUALS   )  
        (
             tmp12=alt {a.getAlts().add(_localctx.tmp12.ret);}   
            (
                ( PIPE   )  
                 tmp13=alt {a.getAlts().add(_localctx.tmp13.ret);}   
            )
            *
        )

    )
    ?
    ( SEMI   )  
;




card_eof returns [de.monticore.grammar._ast.ASTCard ret = null] :
    tmp = card  {$ret = $tmp.ret;} EOF ;

card returns [de.monticore.grammar._ast.ASTCard ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTCard a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTCard();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        STAR {a.setUnbounded(true);}
      |
        ( 'min'   )  
        ( EQUALS   )  
        ( tmp0=Num_Int  {a.setMin(convertNum_Int($tmp0));})  
        (
            ( 'max'   )  
            ( EQUALS   )  
            (
                ( tmp1=Num_Int  {a.setMax(convertNum_Int($tmp1));})  
              |
                ( STAR  {a.setMax(new String("*").intern());} )  
            )

        )
        ?
      |
        ( 'max'   )  
        ( EQUALS   )  
        (
            ( tmp2=Num_Int  {a.setMax(convertNum_Int($tmp2));})  
          |
            ( STAR  {a.setMax(new String("*").intern());} )  
        )

    )

;




rulereference_eof returns [de.monticore.grammar._ast.ASTRuleReference ret = null] :
    tmp = rulereference  {$ret = $tmp.ret;} EOF ;

rulereference returns [de.monticore.grammar._ast.ASTRuleReference ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTRuleReference a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTRuleReference();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
;




rulereferencewithpredicates_eof returns [de.monticore.grammar._ast.ASTRuleReference ret = null] :
    tmp = rulereferencewithpredicates  {$ret = $tmp.ret;} EOF ;

rulereferencewithpredicates returns [de.monticore.grammar._ast.ASTRuleReference ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTRuleReference a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTRuleReference();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=semanticpredicateoraction {a.setSemanticpredicateOrAction(_localctx.tmp0.ret);} ) ? 
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
;




alt_eof returns [de.monticore.grammar._ast.ASTAlt ret = null] :
    tmp = alt  {$ret = $tmp.ret;} EOF ;

alt returns [de.monticore.grammar._ast.ASTAlt ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTAlt a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTAlt();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=rulecomponent {a.getComponents().add(_localctx.tmp0.ret);} ) * 
;




nonterminalseparator_eof returns [de.monticore.grammar._ast.ASTNonTerminalSeparator ret = null] :
    tmp = nonterminalseparator  {$ret = $tmp.ret;} EOF ;

nonterminalseparator returns [de.monticore.grammar._ast.ASTNonTerminalSeparator ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTNonTerminalSeparator a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTNonTerminalSeparator();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        ( tmp0=Name  {a.setVariableName(convertName($tmp0));})  
        ( EQUALS   )  
      |
        ( tmp1=Name  {a.setUsageName(convertName($tmp1));})  
        ( COLON   )  
    )
    ?
    ( LPAREN   )  
    ( tmp2=Name  {a.setName(convertName($tmp2));})  
    (
        AND {a.setPlusKeywords(true);}
    )
    ?
    ( PIPEPIPE   )  
    ( tmp3=String  {a.setSeparator(convertString($tmp3));})  
    ( RPAREN   )  
    (
    STAR {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.STAR);}
    |
    PLUS {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.PLUS);}
    )
;




block_eof returns [de.monticore.grammar._ast.ASTBlock ret = null] :
    tmp = block  {$ret = $tmp.ret;} EOF ;

block returns [de.monticore.grammar._ast.ASTBlock ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTBlock a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTBlock();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( LPAREN   )  
    (
        (
             tmp0=option {a.setOption(_localctx.tmp0.ret);}   
            (
                ( 'init'   )  
                ( LCURLY   )  
                 tmp1=action {a.setInitAction(_localctx.tmp1.ret);}   
                ( RCURLY   )  
            )
            ?
          |
            ( 'init'   )  
            ( LCURLY   )  
             tmp2=action {a.setInitAction(_localctx.tmp2.ret);}   
            ( RCURLY   )  
        )

        ( COLON   )  
    )
    ?
    (
         tmp3=alt {a.getAlts().add(_localctx.tmp3.ret);}   
        (
            ( PIPE   )  
             tmp4=alt {a.getAlts().add(_localctx.tmp4.ret);}   
        )
        *
    )

    ( RPAREN   )  
    (
        (
        QUESTION {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.QUESTION);}
        |
        STAR {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.STAR);}
        |
        PLUS {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.PLUS);}
        )
    )
    ?
;




option_eof returns [de.monticore.grammar._ast.ASTOption ret = null] :
    tmp = option  {$ret = $tmp.ret;} EOF ;

option returns [de.monticore.grammar._ast.ASTOption ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTOption a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTOption();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'options'   )  
    ( LCURLY   )  
    ( tmp0=optionvalue {a.getOptionValue().add(_localctx.tmp0.ret);} ) + 
    ( RCURLY   )  
;




optionvalue_eof returns [de.monticore.grammar._ast.ASTOptionValue ret = null] :
    tmp = optionvalue  {$ret = $tmp.ret;} EOF ;

optionvalue returns [de.monticore.grammar._ast.ASTOptionValue ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTOptionValue a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTOptionValue();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setKey(convertName($tmp0));})  
    ( EQUALS   )  
    ( tmp1=Name  {a.setValue(convertName($tmp1));})  
    ( SEMI   )  
;




nonterminal_eof returns [de.monticore.grammar._ast.ASTNonTerminal ret = null] :
    tmp = nonterminal  {$ret = $tmp.ret;} EOF ;

nonterminal returns [de.monticore.grammar._ast.ASTNonTerminal ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTNonTerminal a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTNonTerminal();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        ( tmp0=Name  {a.setVariableName(convertName($tmp0));})  
        ( EQUALS   )  
      |
        ( tmp1=Name  {a.setUsageName(convertName($tmp1));})  
        ( COLON   )  
    )
    ?
    ( tmp2=Name  {a.setName(convertName($tmp2));})  
    (
        AND {a.setPlusKeywords(true);}
      |
        (
        QUESTION {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.QUESTION);}
        |
        STAR {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.STAR);}
        |
        PLUS {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.PLUS);}
        )
    )
    *
;




terminal_eof returns [de.monticore.grammar._ast.ASTTerminal ret = null] :
    tmp = terminal  {$ret = $tmp.ret;} EOF ;

terminal returns [de.monticore.grammar._ast.ASTTerminal ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTTerminal a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTTerminal();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        ( tmp0=Name  {a.setVariableName(convertName($tmp0));})  
        ( EQUALS   )  
      |
        ( tmp1=Name  {a.setUsageName(convertName($tmp1));})  
        ( COLON   )  
    )
    ?
    ( tmp2=String  {a.setName(convertString($tmp2));})  
    (
        (
        QUESTION {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.QUESTION);}
        |
        STAR {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.STAR);}
        |
        PLUS {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.PLUS);}
        )
    )
    *
;




constant_eof returns [de.monticore.grammar._ast.ASTConstant ret = null] :
    tmp = constant  {$ret = $tmp.ret;} EOF ;

constant returns [de.monticore.grammar._ast.ASTConstant ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTConstant a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTConstant();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        ( tmp0=Name  {a.setHumanName(convertName($tmp0));})  
        ( COLON   )  
    )
    ?
    ( tmp1=String  {a.setName(convertString($tmp1));})  
;




constantgroup_eof returns [de.monticore.grammar._ast.ASTConstantGroup ret = null] :
    tmp = constantgroup  {$ret = $tmp.ret;} EOF ;

constantgroup returns [de.monticore.grammar._ast.ASTConstantGroup ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTConstantGroup a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTConstantGroup();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        ( tmp0=Name  {a.setVariableName(convertName($tmp0));})  
        ( EQUALS   )  
      |
        ( tmp1=Name  {a.setUsageName(convertName($tmp1));})  
        ( COLON   )  
    )
    ?
    ( LBRACK   )  
     tmp2=constant {a.getConstant().add(_localctx.tmp2.ret);}   
    (
        ( PIPE   )  
         tmp3=constant {a.getConstant().add(_localctx.tmp3.ret);}   
    )
    *
    ( RBRACK   )  
    (
        (
        QUESTION {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.QUESTION);}
        |
        STAR {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.STAR);}
        |
        PLUS {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.PLUS);}
        )
    )
    ?
;




eof_eof returns [de.monticore.grammar._ast.ASTEof ret = null] :
    tmp = eof  {$ret = $tmp.ret;} EOF ;

eof returns [de.monticore.grammar._ast.ASTEof ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTEof a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTEof();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'EOF'   )  
;




mcanything_eof returns [de.monticore.grammar._ast.ASTMCAnything ret = null] :
    tmp = mcanything  {$ret = $tmp.ret;} EOF ;

mcanything returns [de.monticore.grammar._ast.ASTMCAnything ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTMCAnything a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTMCAnything();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'MCA'   )  
;




anything_eof returns [de.monticore.grammar._ast.ASTAnything ret = null] :
    tmp = anything  {$ret = $tmp.ret;} EOF ;

anything returns [de.monticore.grammar._ast.ASTAnything ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTAnything a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTAnything();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( POINT   )  
;




semanticpredicateoraction_eof returns [de.monticore.grammar._ast.ASTSemanticpredicateOrAction ret = null] :
    tmp = semanticpredicateoraction  {$ret = $tmp.ret;} EOF ;

semanticpredicateoraction returns [de.monticore.grammar._ast.ASTSemanticpredicateOrAction ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTSemanticpredicateOrAction a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTSemanticpredicateOrAction();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        ( 'astscript'   )  
        ( LCURLY   )  
         tmp0=astscript {a.setASTScript(_localctx.tmp0.ret);}   
        ( RCURLY   )  
    )

  |
    (
        ( LCURLY   )  
         tmp1=expressionpredicate {a.setText(_localctx.tmp1.ret);}   
        ( RCURLY   )  
        QUESTION {a.setPredicate(true);}
      |
        ( LCURLY   )  
         tmp2=action {a.setText(_localctx.tmp2.ret);}   
        ( RCURLY   )  
    )

;




concept_eof returns [de.monticore.grammar._ast.ASTConcept ret = null] :
    tmp = concept  {$ret = $tmp.ret;} EOF ;

concept returns [de.monticore.grammar._ast.ASTConcept ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTConcept a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTConcept();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'concept'   )  
    ( tmp0=Name  {a.setName(convertName($tmp0));}
    /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, protected, continue, else, options, catch, if, case, new, init, void, package, static, method, byte, double, antlr, finally, this, strictfp, throws, lexer, push, enum, external, parser, null, extends, once, transient, astimplements, true, final, try, returns, implements, private, import, const, concept, for, syn, global, interface, long, switch, pop, default, min, public, native, assert, class, EOF, inh, break, max, false, MCA, volatile, abstract, follow, int, instanceof, token, super, component, fragment, boolean, throw, char, short, comment, threadsafe, return] */
     | ( 'astscript'  {a.setName(new String("astscript").intern());} )  
     | ( 'synchronized'  {a.setName(new String("synchronized").intern());} )  
     | ( 'ast'  {a.setName(new String("ast").intern());} )  
     | ( 'do'  {a.setName(new String("do").intern());} )  
     | ( 'while'  {a.setName(new String("while").intern());} )  
     | ( 'float'  {a.setName(new String("float").intern());} )  
     | ( 'astextends'  {a.setName(new String("astextends").intern());} )  
     | ( 'grammar'  {a.setName(new String("grammar").intern());} )  
     | ( 'java'  {a.setName(new String("java").intern());} )  
     | ( 'protected'  {a.setName(new String("protected").intern());} )  
     | ( 'continue'  {a.setName(new String("continue").intern());} )  
     | ( 'else'  {a.setName(new String("else").intern());} )  
     | ( 'options'  {a.setName(new String("options").intern());} )  
     | ( 'catch'  {a.setName(new String("catch").intern());} )  
     | ( 'if'  {a.setName(new String("if").intern());} )  
     | ( 'case'  {a.setName(new String("case").intern());} )  
     | ( 'new'  {a.setName(new String("new").intern());} )  
     | ( 'init'  {a.setName(new String("init").intern());} )  
     | ( 'void'  {a.setName(new String("void").intern());} )  
     | ( 'package'  {a.setName(new String("package").intern());} )  
     | ( 'static'  {a.setName(new String("static").intern());} )  
     | ( 'method'  {a.setName(new String("method").intern());} )  
     | ( 'byte'  {a.setName(new String("byte").intern());} )  
     | ( 'double'  {a.setName(new String("double").intern());} )  
     | ( 'antlr'  {a.setName(new String("antlr").intern());} )  
     | ( 'finally'  {a.setName(new String("finally").intern());} )  
     | ( 'this'  {a.setName(new String("this").intern());} )  
     | ( 'strictfp'  {a.setName(new String("strictfp").intern());} )  
     | ( 'throws'  {a.setName(new String("throws").intern());} )  
     | ( 'lexer'  {a.setName(new String("lexer").intern());} )  
     | ( 'push'  {a.setName(new String("push").intern());} )  
     | ( 'enum'  {a.setName(new String("enum").intern());} )  
     | ( 'external'  {a.setName(new String("external").intern());} )  
     | ( 'parser'  {a.setName(new String("parser").intern());} )  
     | ( 'null'  {a.setName(new String("null").intern());} )  
     | ( 'extends'  {a.setName(new String("extends").intern());} )  
     | ( 'once'  {a.setName(new String("once").intern());} )  
     | ( 'transient'  {a.setName(new String("transient").intern());} )  
     | ( 'astimplements'  {a.setName(new String("astimplements").intern());} )  
     | ( 'true'  {a.setName(new String("true").intern());} )  
     | ( 'final'  {a.setName(new String("final").intern());} )  
     | ( 'try'  {a.setName(new String("try").intern());} )  
     | ( 'returns'  {a.setName(new String("returns").intern());} )  
     | ( 'implements'  {a.setName(new String("implements").intern());} )  
     | ( 'private'  {a.setName(new String("private").intern());} )  
     | ( 'import'  {a.setName(new String("import").intern());} )  
     | ( 'const'  {a.setName(new String("const").intern());} )  
     | ( 'concept'  {a.setName(new String("concept").intern());} )  
     | ( 'for'  {a.setName(new String("for").intern());} )  
     | ( 'syn'  {a.setName(new String("syn").intern());} )  
     | ( 'global'  {a.setName(new String("global").intern());} )  
     | ( 'interface'  {a.setName(new String("interface").intern());} )  
     | ( 'long'  {a.setName(new String("long").intern());} )  
     | ( 'switch'  {a.setName(new String("switch").intern());} )  
     | ( 'pop'  {a.setName(new String("pop").intern());} )  
     | ( 'default'  {a.setName(new String("default").intern());} )  
     | ( 'min'  {a.setName(new String("min").intern());} )  
     | ( 'public'  {a.setName(new String("public").intern());} )  
     | ( 'native'  {a.setName(new String("native").intern());} )  
     | ( 'assert'  {a.setName(new String("assert").intern());} )  
     | ( 'class'  {a.setName(new String("class").intern());} )  
     | ( 'EOF'  {a.setName(new String("EOF").intern());} )  
     | ( 'inh'  {a.setName(new String("inh").intern());} )  
     | ( 'break'  {a.setName(new String("break").intern());} )  
     | ( 'max'  {a.setName(new String("max").intern());} )  
     | ( 'false'  {a.setName(new String("false").intern());} )  
     | ( 'MCA'  {a.setName(new String("MCA").intern());} )  
     | ( 'volatile'  {a.setName(new String("volatile").intern());} )  
     | ( 'abstract'  {a.setName(new String("abstract").intern());} )  
     | ( 'follow'  {a.setName(new String("follow").intern());} )  
     | ( 'int'  {a.setName(new String("int").intern());} )  
     | ( 'instanceof'  {a.setName(new String("instanceof").intern());} )  
     | ( 'token'  {a.setName(new String("token").intern());} )  
     | ( 'super'  {a.setName(new String("super").intern());} )  
     | ( 'component'  {a.setName(new String("component").intern());} )  
     | ( 'fragment'  {a.setName(new String("fragment").intern());} )  
     | ( 'boolean'  {a.setName(new String("boolean").intern());} )  
     | ( 'throw'  {a.setName(new String("throw").intern());} )  
     | ( 'char'  {a.setName(new String("char").intern());} )  
     | ( 'short'  {a.setName(new String("short").intern());} )  
     | ( 'comment'  {a.setName(new String("comment").intern());} )  
     | ( 'threadsafe'  {a.setName(new String("threadsafe").intern());} )  
     | ( 'return'  {a.setName(new String("return").intern());} )  
    )  
     tmp1=mcconcept {a.setConcept(_localctx.tmp1.ret);}   
;




testprod_eof returns [de.monticore.grammar._ast.ASTClassProd ret = null] :
    tmp = testprod  {$ret = $tmp.ret;} EOF ;

testprod returns [de.monticore.grammar._ast.ASTClassProd ret = null]  
@init{// ret is normally returned, a can be instanciated later
de.monticore.grammar._ast.ASTTestProd a = null;
}

:
     tmp0=classprod {_localctx.ret = _localctx.tmp0.ret;}   
    EOF
;




astrule_eof returns [de.monticore.grammar._ast.ASTASTRule ret = null] :
    tmp = astrule  {$ret = $tmp.ret;} EOF ;

astrule returns [de.monticore.grammar._ast.ASTASTRule ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTASTRule a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTASTRule();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'ast'   )  
    ( tmp0=Name  {a.setType(convertName($tmp0));})  
    (
        ( 'astextends'   )  
        (
             tmp1=generictype {a.getASTSuperClass().add(_localctx.tmp1.ret);}   
            (
                ( COMMA   )  
                 tmp2=generictype {a.getASTSuperClass().add(_localctx.tmp2.ret);}   
            )
            *
        )

      |
        ( 'astimplements'   )  
        (
             tmp3=generictype {a.getASTSuperInterface().add(_localctx.tmp3.ret);}   
            (
                ( COMMA   )  
                 tmp4=generictype {a.getASTSuperInterface().add(_localctx.tmp4.ret);}   
            )
            *
        )

    )
    *
    (
        ( EQUALS   )  
        (
             tmp5=method {a.getMethod().add(_localctx.tmp5.ret);}   
          |
             tmp6=attributeinast {a.getAttributeInAST().add(_localctx.tmp6.ret);}   
        )
        *
    )
    ?
    ( SEMI   )  
;




method_eof returns [de.monticore.grammar._ast.ASTMethod ret = null] :
    tmp = method  {$ret = $tmp.ret;} EOF ;

method returns [de.monticore.grammar._ast.ASTMethod ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTMethod a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTMethod();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'method'   )  
    (
        'public' {a.setPublic(true);}
      |
        'private' {a.setPrivate(true);}
      |
        'protected' {a.setProtected(true);}
    )
    ?
    (
        'final' {a.setFinal(true);}
    )
    ?
    (
        'static' {a.setStatic(true);}
    )
    ?
     tmp0=generictype {a.setReturnType(_localctx.tmp0.ret);}   
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
    ( LPAREN   )  
    (
         tmp2=methodparameter {a.getMethodParameter().add(_localctx.tmp2.ret);}   
        (
            ( COMMA   )  
             tmp3=methodparameter {a.getMethodParameter().add(_localctx.tmp3.ret);}   
        )
        *
    )
    ?
    ( RPAREN   )  
    (
        ( 'throws'   )  
        (
             tmp4=generictype {a.getExceptions().add(_localctx.tmp4.ret);}   
            (
                ( COMMA   )  
                 tmp5=generictype {a.getExceptions().add(_localctx.tmp5.ret);}   
            )
            *
        )

    )
    ?
    ( LCURLY   )  
     tmp6=action {a.setBody(_localctx.tmp6.ret);}   
    ( RCURLY   )  
;




methodparameter_eof returns [de.monticore.grammar._ast.ASTMethodParameter ret = null] :
    tmp = methodparameter  {$ret = $tmp.ret;} EOF ;

methodparameter returns [de.monticore.grammar._ast.ASTMethodParameter ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTMethodParameter a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTMethodParameter();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=generictype {a.setType(_localctx.tmp0.ret);}   
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
;




attributeinast_eof returns [de.monticore.grammar._ast.ASTAttributeInAST ret = null] :
    tmp = attributeinast  {$ret = $tmp.ret;} EOF ;

attributeinast returns [de.monticore.grammar._ast.ASTAttributeInAST ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTAttributeInAST a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTAttributeInAST();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        ( tmp0=Name  {a.setName(convertName($tmp0));})  
        ( COLON   )  
    )
    ?
    (
        LEXSYM0 {a.setUnordered(true);}
    )
    ?
     tmp1=generictype {a.setGenericType(_localctx.tmp1.ret);}   
    (
        STAR {a.setIterated(true);}
    )
    ?
    ( tmp2=card {a.setCard(_localctx.tmp2.ret);} ) ? 
    (
        'derived' {a.setDerived(true);}
        ( LCURLY   )  
         tmp3=action {a.setBody(_localctx.tmp3.ret);}   
        ( RCURLY   )  
    )
    ?
;




generictype_eof returns [de.monticore.grammar._ast.ASTGenericType ret = null] :
    tmp = generictype  {$ret = $tmp.ret;} EOF ;

generictype returns [de.monticore.grammar._ast.ASTGenericType ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTGenericType a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTGenericType();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( SLASH   ) ? 
    (
        ( tmp0=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp0));}
        /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, protected, continue, else, options, catch, if, case, new, init, void, package, static, method, byte, double, antlr, finally, this, strictfp, throws, lexer, push, enum, external, parser, null, extends, once, transient, astimplements, true, final, try, returns, implements, private, import, const, concept, for, syn, global, interface, long, switch, pop, default, min, public, native, assert, class, EOF, derived, inh, break, max, false, MCA, volatile, abstract, follow, int, instanceof, token, super, component, fragment, boolean, throw, char, short, comment, threadsafe, return] */
         | ( 'astscript'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astscript");} )  
         | ( 'synchronized'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("synchronized");} )  
         | ( 'ast'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("ast");} )  
         | ( 'do'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("do");} )  
         | ( 'while'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("while");} )  
         | ( 'float'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("float");} )  
         | ( 'astextends'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astextends");} )  
         | ( 'grammar'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("grammar");} )  
         | ( 'java'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("java");} )  
         | ( 'protected'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("protected");} )  
         | ( 'continue'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("continue");} )  
         | ( 'else'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("else");} )  
         | ( 'options'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("options");} )  
         | ( 'catch'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("catch");} )  
         | ( 'if'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("if");} )  
         | ( 'case'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("case");} )  
         | ( 'new'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("new");} )  
         | ( 'init'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("init");} )  
         | ( 'void'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("void");} )  
         | ( 'package'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("package");} )  
         | ( 'static'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("static");} )  
         | ( 'method'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("method");} )  
         | ( 'byte'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("byte");} )  
         | ( 'double'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("double");} )  
         | ( 'antlr'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("antlr");} )  
         | ( 'finally'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("finally");} )  
         | ( 'this'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("this");} )  
         | ( 'strictfp'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("strictfp");} )  
         | ( 'throws'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("throws");} )  
         | ( 'lexer'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("lexer");} )  
         | ( 'push'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("push");} )  
         | ( 'enum'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("enum");} )  
         | ( 'external'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("external");} )  
         | ( 'parser'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("parser");} )  
         | ( 'null'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("null");} )  
         | ( 'extends'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("extends");} )  
         | ( 'once'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("once");} )  
         | ( 'transient'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("transient");} )  
         | ( 'astimplements'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astimplements");} )  
         | ( 'true'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("true");} )  
         | ( 'final'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("final");} )  
         | ( 'try'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("try");} )  
         | ( 'returns'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("returns");} )  
         | ( 'implements'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("implements");} )  
         | ( 'private'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("private");} )  
         | ( 'import'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("import");} )  
         | ( 'const'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("const");} )  
         | ( 'concept'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("concept");} )  
         | ( 'for'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("for");} )  
         | ( 'syn'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("syn");} )  
         | ( 'global'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("global");} )  
         | ( 'interface'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("interface");} )  
         | ( 'long'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("long");} )  
         | ( 'switch'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("switch");} )  
         | ( 'pop'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("pop");} )  
         | ( 'default'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("default");} )  
         | ( 'min'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("min");} )  
         | ( 'public'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("public");} )  
         | ( 'native'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("native");} )  
         | ( 'assert'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("assert");} )  
         | ( 'class'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("class");} )  
         | ( 'EOF'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("EOF");} )  
         | ( 'derived'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("derived");} )  
         | ( 'inh'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("inh");} )  
         | ( 'break'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("break");} )  
         | ( 'max'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("max");} )  
         | ( 'false'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("false");} )  
         | ( 'MCA'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("MCA");} )  
         | ( 'volatile'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("volatile");} )  
         | ( 'abstract'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("abstract");} )  
         | ( 'follow'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("follow");} )  
         | ( 'int'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("int");} )  
         | ( 'instanceof'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("instanceof");} )  
         | ( 'token'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("token");} )  
         | ( 'super'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("super");} )  
         | ( 'component'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("component");} )  
         | ( 'fragment'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("fragment");} )  
         | ( 'boolean'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("boolean");} )  
         | ( 'throw'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("throw");} )  
         | ( 'char'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("char");} )  
         | ( 'short'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("short");} )  
         | ( 'comment'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("comment");} )  
         | ( 'threadsafe'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("threadsafe");} )  
         | ( 'return'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("return");} )  
        )  
        (
            ( POINT   )  
            ( tmp1=Name  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add(convertName($tmp1));}
            /* Automatically added keywords [astscript, synchronized, ast, do, while, float, astextends, grammar, java, protected, continue, else, options, catch, if, case, new, init, void, package, static, method, byte, double, antlr, finally, this, strictfp, throws, lexer, push, enum, external, parser, null, extends, once, transient, astimplements, true, final, try, returns, implements, private, import, const, concept, for, syn, global, interface, long, switch, pop, default, min, public, native, assert, class, EOF, derived, inh, break, max, false, MCA, volatile, abstract, follow, int, instanceof, token, super, component, fragment, boolean, throw, char, short, comment, threadsafe, return] */
             | ( 'astscript'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astscript");} )  
             | ( 'synchronized'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("synchronized");} )  
             | ( 'ast'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("ast");} )  
             | ( 'do'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("do");} )  
             | ( 'while'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("while");} )  
             | ( 'float'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("float");} )  
             | ( 'astextends'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astextends");} )  
             | ( 'grammar'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("grammar");} )  
             | ( 'java'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("java");} )  
             | ( 'protected'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("protected");} )  
             | ( 'continue'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("continue");} )  
             | ( 'else'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("else");} )  
             | ( 'options'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("options");} )  
             | ( 'catch'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("catch");} )  
             | ( 'if'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("if");} )  
             | ( 'case'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("case");} )  
             | ( 'new'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("new");} )  
             | ( 'init'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("init");} )  
             | ( 'void'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("void");} )  
             | ( 'package'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("package");} )  
             | ( 'static'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("static");} )  
             | ( 'method'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("method");} )  
             | ( 'byte'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("byte");} )  
             | ( 'double'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("double");} )  
             | ( 'antlr'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("antlr");} )  
             | ( 'finally'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("finally");} )  
             | ( 'this'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("this");} )  
             | ( 'strictfp'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("strictfp");} )  
             | ( 'throws'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("throws");} )  
             | ( 'lexer'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("lexer");} )  
             | ( 'push'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("push");} )  
             | ( 'enum'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("enum");} )  
             | ( 'external'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("external");} )  
             | ( 'parser'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("parser");} )  
             | ( 'null'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("null");} )  
             | ( 'extends'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("extends");} )  
             | ( 'once'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("once");} )  
             | ( 'transient'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("transient");} )  
             | ( 'astimplements'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("astimplements");} )  
             | ( 'true'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("true");} )  
             | ( 'final'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("final");} )  
             | ( 'try'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("try");} )  
             | ( 'returns'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("returns");} )  
             | ( 'implements'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("implements");} )  
             | ( 'private'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("private");} )  
             | ( 'import'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("import");} )  
             | ( 'const'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("const");} )  
             | ( 'concept'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("concept");} )  
             | ( 'for'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("for");} )  
             | ( 'syn'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("syn");} )  
             | ( 'global'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("global");} )  
             | ( 'interface'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("interface");} )  
             | ( 'long'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("long");} )  
             | ( 'switch'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("switch");} )  
             | ( 'pop'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("pop");} )  
             | ( 'default'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("default");} )  
             | ( 'min'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("min");} )  
             | ( 'public'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("public");} )  
             | ( 'native'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("native");} )  
             | ( 'assert'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("assert");} )  
             | ( 'class'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("class");} )  
             | ( 'EOF'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("EOF");} )  
             | ( 'derived'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("derived");} )  
             | ( 'inh'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("inh");} )  
             | ( 'break'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("break");} )  
             | ( 'max'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("max");} )  
             | ( 'false'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("false");} )  
             | ( 'MCA'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("MCA");} )  
             | ( 'volatile'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("volatile");} )  
             | ( 'abstract'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("abstract");} )  
             | ( 'follow'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("follow");} )  
             | ( 'int'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("int");} )  
             | ( 'instanceof'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("instanceof");} )  
             | ( 'token'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("token");} )  
             | ( 'super'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("super");} )  
             | ( 'component'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("component");} )  
             | ( 'fragment'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("fragment");} )  
             | ( 'boolean'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("boolean");} )  
             | ( 'throw'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("throw");} )  
             | ( 'char'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("char");} )  
             | ( 'short'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("short");} )  
             | ( 'comment'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("comment");} )  
             | ( 'threadsafe'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("threadsafe");} )  
             | ( 'return'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("return");} )  
            )  
        )
        *
    )

    (
        ( LT   )  
        (
             tmp2=generictype {a.getGenericType().add(_localctx.tmp2.ret);}   
            (
                ( COMMA   )  
                 tmp3=generictype {a.getGenericType().add(_localctx.tmp3.ret);}   
            )
            *
        )

        ( GT   )  
    )
    ?
    {a.setDimension(0);}
    (
        ( LBRACK   )  
        ( RBRACK   )  
        {a.setDimension(a.getDimension() + 1);}
    )
    *
;




lexalt_eof returns [de.monticore.grammar._ast.ASTLexAlt ret = null] :
    tmp = lexalt  {$ret = $tmp.ret;} EOF ;

lexalt returns [de.monticore.grammar._ast.ASTLexAlt ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTLexAlt a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTLexAlt();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=lexcomponent {a.getLexComponents().add(_localctx.tmp0.ret);}   
    )
    *
;




lexblock_eof returns [de.monticore.grammar._ast.ASTLexBlock ret = null] :
    tmp = lexblock  {$ret = $tmp.ret;} EOF ;

lexblock returns [de.monticore.grammar._ast.ASTLexBlock ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTLexBlock a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTLexBlock();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        TILDE {a.setNegate(true);}
    )
    ?
    ( LPAREN   )  
    (
        (
             tmp0=lexoption {a.setOption(_localctx.tmp0.ret);}   
            (
                ( 'init'   )  
                ( LCURLY   )  
                 tmp1=action {a.setInitAction(_localctx.tmp1.ret);}   
                ( RCURLY   )  
            )
            ?
          |
            ( 'init'   )  
            ( LCURLY   )  
             tmp2=action {a.setInitAction(_localctx.tmp2.ret);}   
            ( RCURLY   )  
        )

        ( COLON   )  
    )
    ?
    (
         tmp3=lexalt {a.getLexAlts().add(_localctx.tmp3.ret);}   
        (
            ( PIPE   )  
             tmp4=lexalt {a.getLexAlts().add(_localctx.tmp4.ret);}   
        )
        *
    )

    ( RPAREN   )  
    (
        (
        QUESTION {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.QUESTION);}
        |
        STAR {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.STAR);}
        |
        PLUS {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.PLUS);}
        )
    )
    ?
;




lexcharrange_eof returns [de.monticore.grammar._ast.ASTLexCharRange ret = null] :
    tmp = lexcharrange  {$ret = $tmp.ret;} EOF ;

lexcharrange returns [de.monticore.grammar._ast.ASTLexCharRange ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTLexCharRange a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTLexCharRange();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        TILDE {a.setNegate(true);}
    )
    ?
    ( tmp0=Char  {a.setLowerChar(convertChar($tmp0));})  
    ( POINTPOINT   )  
    ( tmp1=Char  {a.setUpperChar(convertChar($tmp1));})  
;




lexchar_eof returns [de.monticore.grammar._ast.ASTLexChar ret = null] :
    tmp = lexchar  {$ret = $tmp.ret;} EOF ;

lexchar returns [de.monticore.grammar._ast.ASTLexChar ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTLexChar a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTLexChar();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        TILDE {a.setNegate(true);}
    )
    ?
    ( tmp0=Char  {a.setChar(convertChar($tmp0));})  
;




lexstring_eof returns [de.monticore.grammar._ast.ASTLexString ret = null] :
    tmp = lexstring  {$ret = $tmp.ret;} EOF ;

lexstring returns [de.monticore.grammar._ast.ASTLexString ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTLexString a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTLexString();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=String  {a.setString(convertString($tmp0));})  
;




lexactionorpredicate_eof returns [de.monticore.grammar._ast.ASTLexActionOrPredicate ret = null] :
    tmp = lexactionorpredicate  {$ret = $tmp.ret;} EOF ;

lexactionorpredicate returns [de.monticore.grammar._ast.ASTLexActionOrPredicate ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTLexActionOrPredicate a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTLexActionOrPredicate();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        ( 'astscript'   )  
        ( LCURLY   )  
         tmp0=astscript {a.setASTScript(_localctx.tmp0.ret);}   
        ( RCURLY   )  
    )

  |
    (
        ( LCURLY   )  
         tmp1=expressionpredicate {a.setText(_localctx.tmp1.ret);}   
        ( RCURLY   )  
        QUESTION {a.setPredicate(true);}
    )

;




lexnonterminal_eof returns [de.monticore.grammar._ast.ASTLexNonTerminal ret = null] :
    tmp = lexnonterminal  {$ret = $tmp.ret;} EOF ;

lexnonterminal returns [de.monticore.grammar._ast.ASTLexNonTerminal ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTLexNonTerminal a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTLexNonTerminal();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        ( tmp0=Name  {a.setVariable(convertName($tmp0));})  
        ( EQUALS   )  
    )
    ?
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
;




lexsimpleiteration_eof returns [de.monticore.grammar._ast.ASTLexSimpleIteration ret = null] :
    tmp = lexsimpleiteration  {$ret = $tmp.ret;} EOF ;

lexsimpleiteration returns [de.monticore.grammar._ast.ASTLexSimpleIteration ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTLexSimpleIteration a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTLexSimpleIteration();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=lexstring {a.setLexString(_localctx.tmp0.ret);}   
      |
         tmp1=lexchar {a.setLexChar(_localctx.tmp1.ret);}   
    )

    (
    QUESTION {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.QUESTION);}
    |
    STAR {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.STAR);}
    |
    PLUS {a.setIteration(de.monticore.grammar._ast.ASTConstantsGrammar.PLUS);}
    )
;




lexoption_eof returns [de.monticore.grammar._ast.ASTLexOption ret = null] :
    tmp = lexoption  {$ret = $tmp.ret;} EOF ;

lexoption returns [de.monticore.grammar._ast.ASTLexOption ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTLexOption a = null;
a=de.monticore.grammar._ast.GrammarNodeFactory.createASTLexOption();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'options'   )  
    ( LCURLY   )  
    ( tmp0=Name  {a.setID(convertName($tmp0));})  
    ( EQUALS   )  
    ( tmp1=Name  {a.setValue(convertName($tmp1));})  
    ( SEMI   )  
    ( RCURLY   )  
;




expressionpredicate_eof returns [de.monticore.grammar._ast.ASTExpressionPredicate ret = null] :
    tmp = expressionpredicate  {$ret = $tmp.ret;} EOF ;

expressionpredicate returns [de.monticore.grammar._ast.ASTExpressionPredicate ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTExpressionPredicate a = null;
a=de.monticore.grammar._ast.Grammar_WithConceptsNodeFactory.createASTExpressionPredicate();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=expression {a.setExpression(_localctx.tmp0.ret);}   
;




astscript_eof returns [de.monticore.grammar._ast.ASTASTScript ret = null] :
    tmp = astscript  {$ret = $tmp.ret;} EOF ;

astscript returns [de.monticore.grammar._ast.ASTASTScript ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTASTScript a = null;
a=de.monticore.grammar._ast.Grammar_WithConceptsNodeFactory.createASTASTScript();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=mcstatement {a.setMCStatement(_localctx.tmp0.ret);}   
;




mcconcept_eof returns [de.monticore.grammar._ast.ASTMCConcept ret = null] :
    tmp = mcconcept  {$ret = $tmp.ret;} EOF ;

mcconcept returns [de.monticore.grammar._ast.ASTMCConcept ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.grammar._ast.ASTMCConcept a = null;
a=de.monticore.grammar._ast.Grammar_WithConceptsNodeFactory.createASTMCConcept();
a.setLanguageComponent(de.monticore.grammar._ast.ASTConstantsGrammar_WithConcepts.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
         tmp0=conceptantlr {a.setConceptAntlr(_localctx.tmp0.ret);}   
      |
         tmp1=conceptattributes {a.setConceptAttributes(_localctx.tmp1.ret);}   
    )

;




scriptstatement_eof returns [de.monticore.grammar.astscript._ast.ASTScriptStatement ret = null] :
    tmp = scriptstatement  {$ret = $tmp.ret;} EOF ;


scriptstatement returns [de.monticore.grammar.astscript._ast.ASTScriptStatement ret]: (
tmp0=constructor
{$ret=$tmp0.ret;}
 | 
tmp1=assignment
{$ret=$tmp1.ret;}
 | 
tmp2=pushstatement
{$ret=$tmp2.ret;}
 | 
tmp3=popstatement
{$ret=$tmp3.ret;}
);


nonconstructorstatement_eof returns [de.monticore.grammar.astscript._ast.ASTNonConstructorStatement ret = null] :
    tmp = nonconstructorstatement  {$ret = $tmp.ret;} EOF ;


nonconstructorstatement returns [de.monticore.grammar.astscript._ast.ASTNonConstructorStatement ret]: (
tmp0=assignment
{$ret=$tmp0.ret;}
);


referenceinattributes_eof returns [de.monticore.grammar.concepts.attributes._ast.ASTReferenceInAttributes ret = null] :
    tmp = referenceinattributes  {$ret = $tmp.ret;} EOF ;


referenceinattributes returns [de.monticore.grammar.concepts.attributes._ast.ASTReferenceInAttributes ret]: (
tmp0=rulereferenceinattribute
{$ret=$tmp0.ret;}
 | 
tmp1=externalattributetype
{$ret=$tmp1.ret;}
);


literal_eof returns [de.monticore.literals._ast.ASTLiteral ret = null] :
    tmp = literal  {$ret = $tmp.ret;} EOF ;


literal returns [de.monticore.literals._ast.ASTLiteral ret]: (
tmp0=nullliteral
{$ret=$tmp0.ret;}
 | 
tmp1=booleanliteral
{$ret=$tmp1.ret;}
 | 
tmp2=charliteral
{$ret=$tmp2.ret;}
 | 
tmp3=stringliteral
{$ret=$tmp3.ret;}
 | 
tmp4=numericliteral
{$ret=$tmp4.ret;}
);


signedliteral_eof returns [de.monticore.literals._ast.ASTSignedLiteral ret = null] :
    tmp = signedliteral  {$ret = $tmp.ret;} EOF ;


signedliteral returns [de.monticore.literals._ast.ASTSignedLiteral ret]: (
tmp0=nullliteral
{$ret=$tmp0.ret;}
 | 
tmp1=booleanliteral
{$ret=$tmp1.ret;}
 | 
tmp2=charliteral
{$ret=$tmp2.ret;}
 | 
tmp3=stringliteral
{$ret=$tmp3.ret;}
 | 
tmp4=signednumericliteral
{$ret=$tmp4.ret;}
);


numericliteral_eof returns [de.monticore.literals._ast.ASTNumericLiteral ret = null] :
    tmp = numericliteral  {$ret = $tmp.ret;} EOF ;


numericliteral returns [de.monticore.literals._ast.ASTNumericLiteral ret]: (
tmp0=intliteral
{$ret=$tmp0.ret;}
 | 
tmp1=longliteral
{$ret=$tmp1.ret;}
 | 
tmp2=floatliteral
{$ret=$tmp2.ret;}
 | 
tmp3=doubleliteral
{$ret=$tmp3.ret;}
);


signednumericliteral_eof returns [de.monticore.literals._ast.ASTSignedNumericLiteral ret = null] :
    tmp = signednumericliteral  {$ret = $tmp.ret;} EOF ;


signednumericliteral returns [de.monticore.literals._ast.ASTSignedNumericLiteral ret]: (
tmp0=signedintliteral
{$ret=$tmp0.ret;}
 | 
tmp1=signedlongliteral
{$ret=$tmp1.ret;}
 | 
tmp2=signedfloatliteral
{$ret=$tmp2.ret;}
 | 
tmp3=signeddoubleliteral
{$ret=$tmp3.ret;}
);


arraytype_eof returns [de.monticore.types._ast.ASTArrayType ret = null] :
    tmp = arraytype  {$ret = $tmp.ret;} EOF ;


arraytype returns [de.monticore.types._ast.ASTArrayType ret]: (
tmp0=complexarraytype
{$ret=$tmp0.ret;}
 | 
tmp1=primitivearraytype
{$ret=$tmp1.ret;}
);


type_eof returns [de.monticore.types._ast.ASTType ret = null] :
    tmp = type  {$ret = $tmp.ret;} EOF ;


type returns [de.monticore.types._ast.ASTType ret]: (
tmp0=complexarraytype
{$ret=$tmp0.ret;}
 | 
tmp1=primitivearraytype
{$ret=$tmp1.ret;}
 | 
tmp2=primitivetype
{$ret=$tmp2.ret;}
 | 
tmp3=simplereferencetype
{$ret=$tmp3.ret;}
 | 
tmp4=complexreferencetype
{$ret=$tmp4.ret;}
);


referencetype_eof returns [de.monticore.types._ast.ASTReferenceType ret = null] :
    tmp = referencetype  {$ret = $tmp.ret;} EOF ;


referencetype returns [de.monticore.types._ast.ASTReferenceType ret]: (
tmp0=simplereferencetype
{$ret=$tmp0.ret;}
 | 
tmp1=complexreferencetype
{$ret=$tmp1.ret;}
);


typeargument_eof returns [de.monticore.types._ast.ASTTypeArgument ret = null] :
    tmp = typeargument  {$ret = $tmp.ret;} EOF ;


typeargument returns [de.monticore.types._ast.ASTTypeArgument ret]: (
tmp0=wildcardtype
{$ret=$tmp0.ret;}
 | 
tmp1=type
{$ret=$tmp1.ret;}
);


returntype_eof returns [de.monticore.types._ast.ASTReturnType ret = null] :
    tmp = returntype  {$ret = $tmp.ret;} EOF ;


returntype returns [de.monticore.types._ast.ASTReturnType ret]: (
tmp0=voidtype
{$ret=$tmp0.ret;}
 | 
tmp1=type
{$ret=$tmp1.ret;}
);


typedeclaration_eof returns [mc.javadsl._ast.ASTTypeDeclaration ret = null] :
    tmp = typedeclaration  {$ret = $tmp.ret;} EOF ;


typedeclaration returns [mc.javadsl._ast.ASTTypeDeclaration ret]: (
tmp0=enumdeclaration
{$ret=$tmp0.ret;}
 | 
tmp1=annotationtypedeclaration
{$ret=$tmp1.ret;}
 | 
tmp2=classdeclaration
{$ret=$tmp2.ret;}
 | 
tmp3=interfacedeclaration
{$ret=$tmp3.ret;}
);


modifier_eof returns [mc.javadsl._ast.ASTModifier ret = null] :
    tmp = modifier  {$ret = $tmp.ret;} EOF ;


modifier returns [mc.javadsl._ast.ASTModifier ret]: (
tmp0=primitivemodifier
{$ret=$tmp0.ret;}
 | 
tmp1=annotation
{$ret=$tmp1.ret;}
);


annotationmemberdeclaration_eof returns [mc.javadsl._ast.ASTAnnotationMemberDeclaration ret = null] :
    tmp = annotationmemberdeclaration  {$ret = $tmp.ret;} EOF ;


annotationmemberdeclaration returns [mc.javadsl._ast.ASTAnnotationMemberDeclaration ret]: (
tmp0=annotationmethoddeclaration
{$ret=$tmp0.ret;}
 | 
tmp1=fielddeclaration
{$ret=$tmp1.ret;}
 | 
tmp2=typedeclaration
{$ret=$tmp2.ret;}
);


annotation_eof returns [mc.javadsl._ast.ASTAnnotation ret = null] :
    tmp = annotation  {$ret = $tmp.ret;} EOF ;


annotation returns [mc.javadsl._ast.ASTAnnotation ret]: (
tmp0=mappedmemberannotation
{$ret=$tmp0.ret;}
 | 
tmp1=singlememberannotation
{$ret=$tmp1.ret;}
 | 
tmp2=markerannotation
{$ret=$tmp2.ret;}
);


memberdeclaration_eof returns [mc.javadsl._ast.ASTMemberDeclaration ret = null] :
    tmp = memberdeclaration  {$ret = $tmp.ret;} EOF ;


memberdeclaration returns [mc.javadsl._ast.ASTMemberDeclaration ret]: (
tmp0=typeinitializer
{$ret=$tmp0.ret;}
 | 
tmp1=constructordeclaration
{$ret=$tmp1.ret;}
 | 
tmp2=methoddeclaration
{$ret=$tmp2.ret;}
 | 
tmp3=fielddeclaration
{$ret=$tmp3.ret;}
 | 
tmp4=typedeclaration
{$ret=$tmp4.ret;}
);


statement_eof returns [mc.javadsl._ast.ASTStatement ret = null] :
    tmp = statement  {$ret = $tmp.ret;} EOF ;


statement returns [mc.javadsl._ast.ASTStatement ret]: (
tmp0=variabledeclarationstatement
{$ret=$tmp0.ret;}
 | 
tmp1=blockstatement
{$ret=$tmp1.ret;}
 | 
tmp2=expressionstatement
{$ret=$tmp2.ret;}
 | 
tmp3=typedeclarationstatement
{$ret=$tmp3.ret;}
 | 
tmp4=emptystatement
{$ret=$tmp4.ret;}
 | 
tmp5=continuestatement
{$ret=$tmp5.ret;}
 | 
tmp6=breakstatement
{$ret=$tmp6.ret;}
 | 
tmp7=returnstatement
{$ret=$tmp7.ret;}
 | 
tmp8=switchstatement
{$ret=$tmp8.ret;}
 | 
tmp9=throwstatement
{$ret=$tmp9.ret;}
 | 
tmp10=assertstatement
{$ret=$tmp10.ret;}
 | 
tmp11=whilestatement
{$ret=$tmp11.ret;}
 | 
tmp12=dowhilestatement
{$ret=$tmp12.ret;}
 | 
tmp13=foreachstatement
{$ret=$tmp13.ret;}
 | 
tmp14=forstatement
{$ret=$tmp14.ret;}
 | 
tmp15=ifstatement
{$ret=$tmp15.ret;}
 | 
tmp16=labeledstatement
{$ret=$tmp16.ret;}
 | 
tmp17=synchronizedstatement
{$ret=$tmp17.ret;}
 | 
tmp18=trystatement
{$ret=$tmp18.ret;}
);


expression_eof returns [mc.javadsl._ast.ASTExpression ret = null] :
    tmp = expression  {$ret = $tmp.ret;} EOF ;


expression returns [mc.javadsl._ast.ASTExpression ret]: (
tmp0=variabledeclarationexpression
{$ret=$tmp0.ret;}
 | 
tmp1=annotationmemberarrayinitializer
{$ret=$tmp1.ret;}
 | 
tmp2=arrayinitializer
{$ret=$tmp2.ret;}
 | 
tmp3=assignmentexpression
{$ret=$tmp3.ret;}
 | 
tmp4=conditionalexpression
{$ret=$tmp4.ret;}
 | 
tmp5=logicalorexpression
{$ret=$tmp5.ret;}
 | 
tmp6=logicalandexpression
{$ret=$tmp6.ret;}
 | 
tmp7=bitwiseorexpression
{$ret=$tmp7.ret;}
 | 
tmp8=xorexpression
{$ret=$tmp8.ret;}
 | 
tmp9=bitwiseandexpression
{$ret=$tmp9.ret;}
 | 
tmp10=equalityexpression
{$ret=$tmp10.ret;}
 | 
tmp11=relationalexpression
{$ret=$tmp11.ret;}
 | 
tmp12=instanceofexpression
{$ret=$tmp12.ret;}
 | 
tmp13=shiftexpression
{$ret=$tmp13.ret;}
 | 
tmp14=additiveexpression
{$ret=$tmp14.ret;}
 | 
tmp15=multiplicativeexpression
{$ret=$tmp15.ret;}
 | 
tmp16=prefixexpression
{$ret=$tmp16.ret;}
 | 
tmp17=castexpression
{$ret=$tmp17.ret;}
 | 
tmp18=postfixexpression
{$ret=$tmp18.ret;}
 | 
tmp19=constructorinvocation
{$ret=$tmp19.ret;}
 | 
tmp20=qualifiedconstructorinvocation
{$ret=$tmp20.ret;}
 | 
tmp21=thisreference
{$ret=$tmp21.ret;}
 | 
tmp22=fieldaccess
{$ret=$tmp22.ret;}
 | 
tmp23=methodinvocationwithsinglename
{$ret=$tmp23.ret;}
 | 
tmp24=methodinvocationwithqualifiedname
{$ret=$tmp24.ret;}
 | 
tmp25=arrayaccessexpression
{$ret=$tmp25.ret;}
 | 
tmp26=literalexpression
{$ret=$tmp26.ret;}
 | 
tmp27=qualifiednameexpression
{$ret=$tmp27.ret;}
 | 
tmp28=parenthesizedexpression
{$ret=$tmp28.ret;}
 | 
tmp29=classaccess
{$ret=$tmp29.ret;}
 | 
tmp30=superreference
{$ret=$tmp30.ret;}
 | 
tmp31=newexpression
{$ret=$tmp31.ret;}
);


instantiation_eof returns [mc.javadsl._ast.ASTInstantiation ret = null] :
    tmp = instantiation  {$ret = $tmp.ret;} EOF ;


instantiation returns [mc.javadsl._ast.ASTInstantiation ret]: (
tmp0=classinstantiation
{$ret=$tmp0.ret;}
 | 
tmp1=arrayinstantiation
{$ret=$tmp1.ret;}
);


prod_eof returns [de.monticore.grammar._ast.ASTProd ret = null] :
    tmp = prod  {$ret = $tmp.ret;} EOF ;


prod returns [de.monticore.grammar._ast.ASTProd ret]: (
tmp0=lexprod
{$ret=$tmp0.ret;}
 | 
tmp1=enumprod
{$ret=$tmp1.ret;}
 | 
tmp2=externalprod
{$ret=$tmp2.ret;}
 | 
tmp3=abstractprod
{$ret=$tmp3.ret;}
 | 
tmp4=parserprod
{$ret=$tmp4.ret;}
);


parserprod_eof returns [de.monticore.grammar._ast.ASTParserProd ret = null] :
    tmp = parserprod  {$ret = $tmp.ret;} EOF ;


parserprod returns [de.monticore.grammar._ast.ASTParserProd ret]: (
tmp0=interfaceprod
{$ret=$tmp0.ret;}
 | 
tmp1=classprod
{$ret=$tmp1.ret;}
);


rulecomponent_eof returns [de.monticore.grammar._ast.ASTRuleComponent ret = null] :
    tmp = rulecomponent  {$ret = $tmp.ret;} EOF ;


rulecomponent returns [de.monticore.grammar._ast.ASTRuleComponent ret]: (
tmp0=nonterminalseparator
{$ret=$tmp0.ret;}
 | 
tmp1=block
{$ret=$tmp1.ret;}
 | 
tmp2=nonterminal
{$ret=$tmp2.ret;}
 | 
tmp3=terminal
{$ret=$tmp3.ret;}
 | 
tmp4=constantgroup
{$ret=$tmp4.ret;}
 | 
tmp5=eof
{$ret=$tmp5.ret;}
 | 
tmp6=mcanything
{$ret=$tmp6.ret;}
 | 
tmp7=anything
{$ret=$tmp7.ret;}
 | 
tmp8=semanticpredicateoraction
{$ret=$tmp8.ret;}
);


iterminal_eof returns [de.monticore.grammar._ast.ASTITerminal ret = null] :
    tmp = iterminal  {$ret = $tmp.ret;} EOF ;


iterminal returns [de.monticore.grammar._ast.ASTITerminal ret]: (
tmp0=terminal
{$ret=$tmp0.ret;}
 | 
tmp1=constant
{$ret=$tmp1.ret;}
);


lexcomponent_eof returns [de.monticore.grammar._ast.ASTLexComponent ret = null] :
    tmp = lexcomponent  {$ret = $tmp.ret;} EOF ;


lexcomponent returns [de.monticore.grammar._ast.ASTLexComponent ret]: (
tmp0=lexblock
{$ret=$tmp0.ret;}
 | 
tmp1=lexcharrange
{$ret=$tmp1.ret;}
 | 
tmp2=lexchar
{$ret=$tmp2.ret;}
 | 
tmp3=lexstring
{$ret=$tmp3.ret;}
 | 
tmp4=lexactionorpredicate
{$ret=$tmp4.ret;}
 | 
tmp5=lexnonterminal
{$ret=$tmp5.ret;}
 | 
tmp6=lexsimpleiteration
{$ret=$tmp6.ret;}
);
ROOF : '^';
SLASHEQUALS : '/=';
RBRACK : ']';
LBRACK : '[';
GTGT : '>>';
GTEQUALS : '>=';
POINTPOINTPOINT : '...';
ANDAND : '&&';
PIPEPIPE : '||';
PERCENTEQUALS : '%=';
AT : '@';
PLUSEQUALS : '+=';
QUESTION : '?';
GT : '>';
EQUALS : '=';
LT : '<';
SEMI : ';';
COLON : ':';
GTGTGTEQUALS : '>>>=';
EQUALSEQUALS : '==';
POINTPOINT : '..';
SLASH : '/';
PLUSPLUS : '++';
POINT : '.';
MINUS : '-';
COMMA : ',';
PLUS : '+';
STAR : '*';
RPAREN : ')';
LPAREN : '(';
EXCLAMATIONMARKEQUALS : '!=';
AND : '&';
PERCENT : '%';
STAREQUALS : '*=';
EXCLAMATIONMARK : '!';
MINUSGT : '->';
TILDE : '~';
MINUSEQUALS : '-=';
ROOFEQUALS : '^=';
RCURLY : '}';
PIPE : '|';
LCURLY : '{';
LEXSYM0 : '<<unordered>>';
LTEQUALS : '<=';
LTLT : '<<';
LTLTEQUALS : '<<=';
MINUSMINUS : '--';
PIPEEQUALS : '|=';
GTGTGT : '>>>';
GTGTEQUALS : '>>=';
ANDEQUALS : '&=';
Name :
    ('a'..'z' |
    'A'..'Z' |
    '_' |
    '$' )
    ('a'..'z' |
    'A'..'Z' |
    '_' |
    '0'..'9' |
    '$' )*
;

fragment NEWLINE :
    ('\r' '\n' |
    '\r' |
    '\n' )
;

WS :
    (' ' |
    '\t' |
    '\r' '\n' |
    '\r' |
    '\n' )
  {_channel = HIDDEN;}
;

SL_COMMENT :
      '//' (~('\n' |
      '\r' )
    )*
    ('\n' |
      '\r' ('\n' )?
    )?
  {_channel = HIDDEN;
  if (getCompiler() != null) {
    mc.ast.Comment _comment = new mc.ast.Comment(getText());
    _comment.set_SourcePositionStart(new mc.ast.SourcePosition(getLine(), getCharPositionInLine()));
    _comment.set_SourcePositionEnd(getCompiler().computeEndPosition(getToken()));
    getCompiler().addComment(_comment);
  }}
;

ML_COMMENT :
    '/*' ({_input.LA(2) != '/'}?'*' |
     NEWLINE |
      ~('*' |
      '\n' |
      '\r' )
    )*
  '*/' {_channel = HIDDEN;
  if (getCompiler() != null) {
    mc.ast.Comment _comment = new mc.ast.Comment(getText());
    _comment.set_SourcePositionStart(new mc.ast.SourcePosition(getLine(), getCharPositionInLine()));
    _comment.set_SourcePositionEnd(getCompiler().computeEndPosition(getToken()));
    getCompiler().addComment(_comment);
  }}
;

Num_Int :
   DecimalIntegerLiteral |
   HexIntegerLiteral |
   OctalIntegerLiteral |
 BinaryIntegerLiteral ;

Num_Long :
    ( DecimalIntegerLiteral  IntegerTypeSuffix )
  |
    ( HexIntegerLiteral  IntegerTypeSuffix )
  |
    ( OctalIntegerLiteral  IntegerTypeSuffix )
  |
    ( BinaryIntegerLiteral  IntegerTypeSuffix )
;

fragment DecimalIntegerLiteral :
 DecimalNumeral ;

fragment HexIntegerLiteral :
 HexNumeral ;

fragment OctalIntegerLiteral :
 OctalNumeral ;

fragment BinaryIntegerLiteral :
 BinaryNumeral ;

fragment IntegerTypeSuffix :
  'l' |
'L' ;

fragment DecimalNumeral :
  '0' |
       NonZeroDigit (( Digits )?
    |
     Underscores  Digits )
;

fragment Digits :
       Digit (( DigitOrUnderscore )*
     Digit )?
;

fragment Digit :
  '0' |
 NonZeroDigit ;

fragment NonZeroDigit :
'1'..'9' ;

fragment DigitOrUnderscore :
   Digit |
'_' ;

fragment Underscores :
    ('_' )+
;

fragment HexNumeral :
    '0' ('x' |
    'X' )
 HexDigits ;

fragment HexDigits :
       HexDigit (( HexDigitOrUnderscore )*
     HexDigit )?
;

fragment HexDigit :
  '0'..'9' |
  'a'..'f' |
'A'..'F' ;

fragment HexDigitOrUnderscore :
   HexDigit |
'_' ;

fragment OctalNumeral :
    '0' ( Underscores )?
 OctalDigits ;

fragment OctalDigits :
       OctalDigit (( OctalDigitOrUnderscore )*
     OctalDigit )?
;

fragment OctalDigit :
'0'..'7' ;

fragment OctalDigitOrUnderscore :
   OctalDigit |
'_' ;

fragment BinaryNumeral :
    '0' ('b' |
    'B' )
 BinaryDigits ;

fragment BinaryDigits :
       BinaryDigit (( BinaryDigitOrUnderscore )*
     BinaryDigit )?
;

fragment BinaryDigit :
  '0' |
'1' ;

fragment BinaryDigitOrUnderscore :
   BinaryDigit |
'_' ;

Num_Float :
   DecimalFloatingPointLiteral |
 HexadecimalFloatingPointLiteral ;

Num_Double :
   DecimalDoublePointLiteral |
 HexadecimalDoublePointLiteral ;

fragment DecimalDoublePointLiteral :
     Digits '.' ( Digits )?
    ( ExponentPart )?
    ( DoubleTypeSuffix )?
  |
    '.'  Digits ( ExponentPart )?
    ( DoubleTypeSuffix )?
  |
     Digits  ExponentPart ( DoubleTypeSuffix )?
  |
 Digits  DoubleTypeSuffix ;

fragment DecimalFloatingPointLiteral :
     Digits '.' ( Digits )?
    ( ExponentPart )?
    ( FloatTypeSuffix )
  |
    '.'  Digits ( ExponentPart )?
    ( FloatTypeSuffix )
  |
     Digits  ExponentPart ( FloatTypeSuffix )
  |
 Digits  FloatTypeSuffix ;

fragment ExponentPart :
 ExponentIndicator  SignedInteger ;

fragment ExponentIndicator :
  'e' |
'E' ;

fragment SignedInteger :
    ( Sign )?
 Digits ;

fragment Sign :
  '+' |
'-' ;

fragment FloatTypeSuffix :
  'f' |
'F' ;

fragment DoubleTypeSuffix :
  'd' |
'D' ;

fragment HexadecimalDoublePointLiteral :
     HexSignificand  BinaryExponent ( DoubleTypeSuffix )?
;

fragment HexadecimalFloatingPointLiteral :
     HexSignificand  BinaryExponent ( FloatTypeSuffix )
;

fragment HexSignificand :
     HexNumeral ('.' )?
  |
    '0' ('x' |
    'X' )
    ( HexDigits )?
'.'  HexDigits ;

fragment BinaryExponent :
 BinaryExponentIndicator  SignedInteger ;

fragment BinaryExponentIndicator :
  'p' |
'P' ;

Char :
    '\'' ( SingleCharacter |
     EscapeSequence )
  '\'' {setText(getText().substring(1, getText().length() - 1));}
;

fragment SingleCharacter :
    ~('\'' )
;

String :
    '"' ( StringCharacters )?
  '"' {setText(getText().substring(1, getText().length() - 1));}
;

fragment StringCharacters :
    ( StringCharacter )+
;

fragment StringCharacter :
    ~('"' )
  |
 EscapeSequence ;

fragment EscapeSequence :
    '\\' ('b' |
    't' |
    'n' |
    'f' |
    'r' |
    '"' |
    '\'' |
    '\\' )
  |
   OctalEscape |
 UnicodeEscape ;

fragment OctalEscape :
  '\\'  OctalDigit |
  '\\'  OctalDigit  OctalDigit |
'\\'  ZeroToThree  OctalDigit  OctalDigit ;

fragment UnicodeEscape :
'\\' 'u'  HexDigit  HexDigit  HexDigit  HexDigit ;

fragment ZeroToThree :
'0'..'3' ;

