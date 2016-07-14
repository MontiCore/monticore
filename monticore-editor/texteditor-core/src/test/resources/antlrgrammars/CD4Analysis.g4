grammar CD4Analysis;
 @parser::header {
package de.cd4analysis._parser;
import mc.antlr4.MCParser;
}
@lexer::header {
package de.cd4analysis._parser;
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

  public int cmpCounter = 0;// convert function for Digits
private String convertDigits(Token t)  {
    return t.getText().intern();
}

// convert function for OctalNumeral
private String convertOctalNumeral(Token t)  {
    return t.getText().intern();
}

// convert function for String
private String convertString(Token t)  {
    return t.getText().intern();
}

// convert function for BinaryNumeral
private String convertBinaryNumeral(Token t)  {
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

// convert function for ExponentIndicator
private String convertExponentIndicator(Token t)  {
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

// convert function for Char
private String convertChar(Token t)  {
    return t.getText().intern();
}

// convert function for BinaryDigits
private String convertBinaryDigits(Token t)  {
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

// convert function for DigitOrUnderscore
private String convertDigitOrUnderscore(Token t)  {
    return t.getText().intern();
}

// convert function for HexSignificand
private String convertHexSignificand(Token t)  {
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

// convert function for HexNumeral
private String convertHexNumeral(Token t)  {
    return t.getText().intern();
}

// convert function for BinaryIntegerLiteral
private String convertBinaryIntegerLiteral(Token t)  {
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

// convert function for NEWLINE
private String convertNEWLINE(Token t)  {
    return t.getText().intern();
}

// convert function for NonZeroDigit
private String convertNonZeroDigit(Token t)  {
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

// convert function for ZeroToThree
private String convertZeroToThree(Token t)  {
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

// convert function for DecimalNumeral
private String convertDecimalNumeral(Token t)  {
    return t.getText().intern();
}

// convert function for EscapeSequence
private String convertEscapeSequence(Token t)  {
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

// convert function for DecimalDoublePointLiteral
private String convertDecimalDoublePointLiteral(Token t)  {
    return t.getText().intern();
}

// convert function for HexDigitOrUnderscore
private String convertHexDigitOrUnderscore(Token t)  {
    return t.getText().intern();
}

// convert function for ML_COMMENT
private String convertML_COMMENT(Token t)  {
    return t.getText().intern();
}

// convert function for BinaryDigitOrUnderscore
private String convertBinaryDigitOrUnderscore(Token t)  {
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


nullliteral_eof returns [de.monticore.literals._ast.ASTNullLiteral ret = null] :
    tmp = nullliteral  {$ret = $tmp.ret;} EOF ;

nullliteral returns [de.monticore.literals._ast.ASTNullLiteral ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.monticore.literals._ast.ASTNullLiteral a = null;
a=de.monticore.literals._ast.LiteralsNodeFactory.createASTNullLiteral();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add(convertName($tmp0));}
    /* Automatically added keywords [implements, void, package, import, false, interface, throws, enum, super, classdiagram, null, extends, true, class] */
     | ( 'implements'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("implements");} )  
     | ( 'void'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("void");} )  
     | ( 'package'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("package");} )  
     | ( 'import'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("import");} )  
     | ( 'false'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("false");} )  
     | ( 'interface'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("interface");} )  
     | ( 'throws'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("throws");} )  
     | ( 'enum'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("enum");} )  
     | ( 'super'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("super");} )  
     | ( 'classdiagram'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("classdiagram");} )  
     | ( 'null'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("null");} )  
     | ( 'extends'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("extends");} )  
     | ( 'true'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("true");} )  
     | ( 'class'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("class");} )  
    )  
    (
        ( POINT   )  
        ( tmp1=Name  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add(convertName($tmp1));}
        /* Automatically added keywords [implements, void, package, import, false, interface, throws, enum, super, classdiagram, null, extends, true, class] */
         | ( 'implements'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("implements");} )  
         | ( 'void'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("void");} )  
         | ( 'package'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("package");} )  
         | ( 'import'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("import");} )  
         | ( 'false'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("false");} )  
         | ( 'interface'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("interface");} )  
         | ( 'throws'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("throws");} )  
         | ( 'enum'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("enum");} )  
         | ( 'super'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("super");} )  
         | ( 'classdiagram'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("classdiagram");} )  
         | ( 'null'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("null");} )  
         | ( 'extends'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("extends");} )  
         | ( 'true'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("true");} )  
         | ( 'class'  {if (a.getParts()==null){a.setParts(new java.util.ArrayList());};  a.getParts().add("class");} )  
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
        /* Automatically added keywords [implements, void, package, import, byte, double, false, interface, throws, float, enum, int, long, super, classdiagram, boolean, null, extends, true, char, short, class] */
         | ( 'implements'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("implements");} )  
         | ( 'void'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("void");} )  
         | ( 'package'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("package");} )  
         | ( 'import'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("import");} )  
         | ( 'byte'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("byte");} )  
         | ( 'double'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("double");} )  
         | ( 'false'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("false");} )  
         | ( 'interface'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("interface");} )  
         | ( 'throws'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("throws");} )  
         | ( 'float'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("float");} )  
         | ( 'enum'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("enum");} )  
         | ( 'int'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("int");} )  
         | ( 'long'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("long");} )  
         | ( 'super'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("super");} )  
         | ( 'classdiagram'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("classdiagram");} )  
         | ( 'boolean'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("boolean");} )  
         | ( 'null'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("null");} )  
         | ( 'extends'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("extends");} )  
         | ( 'true'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("true");} )  
         | ( 'char'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("char");} )  
         | ( 'short'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("short");} )  
         | ( 'class'  {if (a.getName()==null){a.setName(new java.util.ArrayList());};  a.getName().add("class");} )  
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
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
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'import'   )  
    ( tmp0=Name  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add(convertName($tmp0));}
    /* Automatically added keywords [implements, void, package, import, byte, double, false, interface, throws, float, enum, int, long, super, classdiagram, boolean, null, extends, true, char, short, class] */
     | ( 'implements'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("implements");} )  
     | ( 'void'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("void");} )  
     | ( 'package'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("package");} )  
     | ( 'import'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("import");} )  
     | ( 'byte'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("byte");} )  
     | ( 'double'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("double");} )  
     | ( 'false'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("false");} )  
     | ( 'interface'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("interface");} )  
     | ( 'throws'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("throws");} )  
     | ( 'float'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("float");} )  
     | ( 'enum'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("enum");} )  
     | ( 'int'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("int");} )  
     | ( 'long'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("long");} )  
     | ( 'super'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("super");} )  
     | ( 'classdiagram'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("classdiagram");} )  
     | ( 'boolean'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("boolean");} )  
     | ( 'null'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("null");} )  
     | ( 'extends'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("extends");} )  
     | ( 'true'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("true");} )  
     | ( 'char'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("char");} )  
     | ( 'short'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("short");} )  
     | ( 'class'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("class");} )  
    )  
    (
        ( POINT   )  
        ( tmp1=Name  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add(convertName($tmp1));}
        /* Automatically added keywords [implements, void, package, import, byte, double, false, interface, throws, float, enum, int, long, super, classdiagram, boolean, null, extends, true, char, short, class] */
         | ( 'implements'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("implements");} )  
         | ( 'void'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("void");} )  
         | ( 'package'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("package");} )  
         | ( 'import'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("import");} )  
         | ( 'byte'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("byte");} )  
         | ( 'double'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("double");} )  
         | ( 'false'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("false");} )  
         | ( 'interface'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("interface");} )  
         | ( 'throws'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("throws");} )  
         | ( 'float'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("float");} )  
         | ( 'enum'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("enum");} )  
         | ( 'int'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("int");} )  
         | ( 'long'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("long");} )  
         | ( 'super'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("super");} )  
         | ( 'classdiagram'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("classdiagram");} )  
         | ( 'boolean'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("boolean");} )  
         | ( 'null'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("null");} )  
         | ( 'extends'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("extends");} )  
         | ( 'true'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("true");} )  
         | ( 'char'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("char");} )  
         | ( 'short'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("short");} )  
         | ( 'class'  {if (a.getImportList()==null){a.setImportList(new java.util.ArrayList());};  a.getImportList().add("class");} )  
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




cdcompilationunit_eof returns [de.cd4analysis._ast.ASTCDCompilationUnit ret = null] :
    tmp = cdcompilationunit  {$ret = $tmp.ret;} EOF ;

cdcompilationunit returns [de.cd4analysis._ast.ASTCDCompilationUnit ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCDCompilationUnit a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCDCompilationUnit();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    (
        ( 'package'   )  
        (
            ( tmp0=Name  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add(convertName($tmp0));}
            /* Automatically added keywords [implements, void, package, import, byte, double, false, interface, throws, float, enum, int, long, super, classdiagram, boolean, null, extends, true, char, short, class] */
             | ( 'implements'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("implements");} )  
             | ( 'void'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("void");} )  
             | ( 'package'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("package");} )  
             | ( 'import'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("import");} )  
             | ( 'byte'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("byte");} )  
             | ( 'double'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("double");} )  
             | ( 'false'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("false");} )  
             | ( 'interface'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("interface");} )  
             | ( 'throws'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("throws");} )  
             | ( 'float'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("float");} )  
             | ( 'enum'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("enum");} )  
             | ( 'int'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("int");} )  
             | ( 'long'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("long");} )  
             | ( 'super'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("super");} )  
             | ( 'classdiagram'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("classdiagram");} )  
             | ( 'boolean'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("boolean");} )  
             | ( 'null'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("null");} )  
             | ( 'extends'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("extends");} )  
             | ( 'true'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("true");} )  
             | ( 'char'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("char");} )  
             | ( 'short'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("short");} )  
             | ( 'class'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("class");} )  
            )  
            (
                ( POINT   )  
                ( tmp1=Name  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add(convertName($tmp1));}
                /* Automatically added keywords [implements, void, package, import, byte, double, false, interface, throws, float, enum, int, long, super, classdiagram, boolean, null, extends, true, char, short, class] */
                 | ( 'implements'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("implements");} )  
                 | ( 'void'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("void");} )  
                 | ( 'package'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("package");} )  
                 | ( 'import'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("import");} )  
                 | ( 'byte'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("byte");} )  
                 | ( 'double'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("double");} )  
                 | ( 'false'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("false");} )  
                 | ( 'interface'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("interface");} )  
                 | ( 'throws'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("throws");} )  
                 | ( 'float'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("float");} )  
                 | ( 'enum'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("enum");} )  
                 | ( 'int'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("int");} )  
                 | ( 'long'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("long");} )  
                 | ( 'super'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("super");} )  
                 | ( 'classdiagram'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("classdiagram");} )  
                 | ( 'boolean'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("boolean");} )  
                 | ( 'null'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("null");} )  
                 | ( 'extends'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("extends");} )  
                 | ( 'true'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("true");} )  
                 | ( 'char'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("char");} )  
                 | ( 'short'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("short");} )  
                 | ( 'class'  {if (a.getPackage()==null){a.setPackage(new java.util.ArrayList());};  a.getPackage().add("class");} )  
                )  
            )
            *
        )

        ( SEMI   )  
    )
    ?
    (
         tmp2=importstatement {a.getImportStatements().add(_localctx.tmp2.ret);}   
    )
    *
     tmp3=cddefinition {a.setCDDefinition(_localctx.tmp3.ret);}   
;




cddefinition_eof returns [de.cd4analysis._ast.ASTCDDefinition ret = null] :
    tmp = cddefinition  {$ret = $tmp.ret;} EOF ;

cddefinition returns [de.cd4analysis._ast.ASTCDDefinition ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCDDefinition a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCDDefinition();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( 'classdiagram'   )  
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    ( LCURLY   )  
    (
         tmp1=cdclass {a.getCDClasses().add(_localctx.tmp1.ret);}   
      |
         tmp2=cdinterface {a.getCDInterfaces().add(_localctx.tmp2.ret);}   
      |
         tmp3=cdenum {a.getCDEnums().add(_localctx.tmp3.ret);}   
      |
         tmp4=cdassociation {a.getCDAssociations().add(_localctx.tmp4.ret);}   
    )
    *
    ( RCURLY   )  
;




cdclass_eof returns [de.cd4analysis._ast.ASTCDClass ret = null] :
    tmp = cdclass  {$ret = $tmp.ret;} EOF ;

cdclass returns [de.cd4analysis._ast.ASTCDClass ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCDClass a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCDClass();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=modifier {a.setModifier(_localctx.tmp0.ret);} ) ? 
    ( 'class'   )  
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
    (
        ( 'extends'   )  
         tmp2=referencetype {a.setSuperclass(_localctx.tmp2.ret);}   
    )
    ?
    (
        ( 'implements'   )  
        (
             tmp3=referencetype {a.getInterfaces().add(_localctx.tmp3.ret);}   
            (
                ( COMMA   )  
                 tmp4=referencetype {a.getInterfaces().add(_localctx.tmp4.ret);}   
            )
            *
        )

    )
    ?
    (
        ( LCURLY   )  
        (
             tmp5=cdattribute {a.getCDAttributes().add(_localctx.tmp5.ret);}   
          |
             tmp6=cdconstructor {a.getCDConstructors().add(_localctx.tmp6.ret);}   
          |
             tmp7=cdmethod {a.getCDMethods().add(_localctx.tmp7.ret);}   
        )
        *
        ( RCURLY   )  
      |
        ( SEMI   )  
    )

;




cdinterface_eof returns [de.cd4analysis._ast.ASTCDInterface ret = null] :
    tmp = cdinterface  {$ret = $tmp.ret;} EOF ;

cdinterface returns [de.cd4analysis._ast.ASTCDInterface ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCDInterface a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCDInterface();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=modifier {a.setModifier(_localctx.tmp0.ret);} ) ? 
    ( 'interface'   )  
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
    (
        ( 'extends'   )  
        (
             tmp2=referencetype {a.getInterfaces().add(_localctx.tmp2.ret);}   
            (
                ( COMMA   )  
                 tmp3=referencetype {a.getInterfaces().add(_localctx.tmp3.ret);}   
            )
            *
        )

    )
    ?
    (
        ( LCURLY   )  
        (
             tmp4=cdattribute {a.getCDAttributes().add(_localctx.tmp4.ret);}   
          |
             tmp5=cdmethod {a.getCDMethods().add(_localctx.tmp5.ret);}   
        )
        *
        ( RCURLY   )  
      |
        ( SEMI   )  
    )

;




cdenum_eof returns [de.cd4analysis._ast.ASTCDEnum ret = null] :
    tmp = cdenum  {$ret = $tmp.ret;} EOF ;

cdenum returns [de.cd4analysis._ast.ASTCDEnum ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCDEnum a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCDEnum();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=modifier {a.setModifier(_localctx.tmp0.ret);} ) ? 
    ( 'enum'   )  
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
    (
        ( 'implements'   )  
        (
             tmp2=referencetype {a.getInterfaces().add(_localctx.tmp2.ret);}   
            (
                ( COMMA   )  
                 tmp3=referencetype {a.getInterfaces().add(_localctx.tmp3.ret);}   
            )
            *
        )

    )
    ?
    (
        ( LCURLY   )  
        (
             tmp4=cdenumconstant {a.getCDEnumConstants().add(_localctx.tmp4.ret);}   
            (
                ( COMMA   )  
                 tmp5=cdenumconstant {a.getCDEnumConstants().add(_localctx.tmp5.ret);}   
            )
            *
        )
        ?
        ( SEMI   )  
        (
             tmp6=cdconstructor {a.getCDConstructors().add(_localctx.tmp6.ret);}   
          |
             tmp7=cdmethod {a.getCDMethods().add(_localctx.tmp7.ret);}   
        )
        *
        ( RCURLY   )  
      |
        ( SEMI   )  
    )

;




cdattribute_eof returns [de.cd4analysis._ast.ASTCDAttribute ret = null] :
    tmp = cdattribute  {$ret = $tmp.ret;} EOF ;

cdattribute returns [de.cd4analysis._ast.ASTCDAttribute ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCDAttribute a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCDAttribute();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=modifier {a.setModifier(_localctx.tmp0.ret);} ) ? 
     tmp1=type {a.setType(_localctx.tmp1.ret);}   
    ( tmp2=Name  {a.setName(convertName($tmp2));})  
    (
        ( EQUALS   )  
         tmp3=value {a.setValue(_localctx.tmp3.ret);}   
    )
    ?
    ( SEMI   )  
;




cdenumconstant_eof returns [de.cd4analysis._ast.ASTCDEnumConstant ret = null] :
    tmp = cdenumconstant  {$ret = $tmp.ret;} EOF ;

cdenumconstant returns [de.cd4analysis._ast.ASTCDEnumConstant ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCDEnumConstant a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCDEnumConstant();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
    (
        ( LPAREN   )  
        (
             tmp1=cdenumparameter {a.getCDEnumParameters().add(_localctx.tmp1.ret);}   
            (
                ( COMMA   )  
                 tmp2=cdenumparameter {a.getCDEnumParameters().add(_localctx.tmp2.ret);}   
            )
            *
        )

        ( RPAREN   )  
    )
    ?
;




cdenumparameter_eof returns [de.cd4analysis._ast.ASTCDEnumParameter ret = null] :
    tmp = cdenumparameter  {$ret = $tmp.ret;} EOF ;

cdenumparameter returns [de.cd4analysis._ast.ASTCDEnumParameter ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCDEnumParameter a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCDEnumParameter();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=value {a.setValue(_localctx.tmp0.ret);}   
;




cdconstructor_eof returns [de.cd4analysis._ast.ASTCDConstructor ret = null] :
    tmp = cdconstructor  {$ret = $tmp.ret;} EOF ;

cdconstructor returns [de.cd4analysis._ast.ASTCDConstructor ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCDConstructor a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCDConstructor();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=modifier {a.setModifier(_localctx.tmp0.ret);}   
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
    ( LPAREN   )  
    (
         tmp2=cdparameter {a.getCDParameters().add(_localctx.tmp2.ret);}   
        (
            ( COMMA   )  
             tmp3=cdparameter {a.getCDParameters().add(_localctx.tmp3.ret);}   
        )
        *
    )
    ?
    ( RPAREN   )  
    (
        ( 'throws'   )  
        (
             tmp4=qualifiedname {a.getExceptions().add(_localctx.tmp4.ret);}   
            (
                ( COMMA   )  
                 tmp5=qualifiedname {a.getExceptions().add(_localctx.tmp5.ret);}   
            )
            *
        )

    )
    ?
    ( SEMI   )  
;




cdmethod_eof returns [de.cd4analysis._ast.ASTCDMethod ret = null] :
    tmp = cdmethod  {$ret = $tmp.ret;} EOF ;

cdmethod returns [de.cd4analysis._ast.ASTCDMethod ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCDMethod a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCDMethod();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=modifier {a.setModifier(_localctx.tmp0.ret);}   
     tmp1=returntype {a.setReturnType(_localctx.tmp1.ret);}   
    ( tmp2=Name  {a.setName(convertName($tmp2));})  
    ( LPAREN   )  
    (
         tmp3=cdparameter {a.getCDParameters().add(_localctx.tmp3.ret);}   
        (
            ( COMMA   )  
             tmp4=cdparameter {a.getCDParameters().add(_localctx.tmp4.ret);}   
        )
        *
    )
    ?
    ( RPAREN   )  
    (
        ( 'throws'   )  
        (
             tmp5=qualifiedname {a.getExceptions().add(_localctx.tmp5.ret);}   
            (
                ( COMMA   )  
                 tmp6=qualifiedname {a.getExceptions().add(_localctx.tmp6.ret);}   
            )
            *
        )

    )
    ?
    ( SEMI   )  
;




cdparameter_eof returns [de.cd4analysis._ast.ASTCDParameter ret = null] :
    tmp = cdparameter  {$ret = $tmp.ret;} EOF ;

cdparameter returns [de.cd4analysis._ast.ASTCDParameter ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCDParameter a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCDParameter();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=type {a.setType(_localctx.tmp0.ret);}   
    (
        POINTPOINTPOINT {a.setEllipsis(true);}
    )
    ?
    ( tmp1=Name  {a.setName(convertName($tmp1));})  
;




cdassociation_eof returns [de.cd4analysis._ast.ASTCDAssociation ret = null] :
    tmp = cdassociation  {$ret = $tmp.ret;} EOF ;

cdassociation returns [de.cd4analysis._ast.ASTCDAssociation ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCDAssociation a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCDAssociation();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=stereotype {a.setStereotype(_localctx.tmp0.ret);} ) ? 
    (
        'association' {a.setAssociation(true);}
      |
        'composition' {a.setComposition(true);}
    )

    (
        SLASH {a.setDerived(true);}
    )
    ?
    ( tmp1=Name  {a.setName(convertName($tmp1));}) ? 
    ( tmp2=modifier {a.setLeftModifier(_localctx.tmp2.ret);} ) ? 
    ( tmp3=cardinality {a.setLeftCardinality(_localctx.tmp3.ret);} ) ? 
     tmp4=qualifiedname {a.setLeftReferenceName(_localctx.tmp4.ret);}   
    (
        ( LBRACK   )  
         tmp5=cdqualifier {a.setLeftQualifier(_localctx.tmp5.ret);}   
        ( RBRACK   )  
    )
    ?
    (
        ( LPAREN   )  
        ( tmp6=Name  {a.setLeftRole(convertName($tmp6));})  
        ( RPAREN   )  
    )
    ?
    (
        MINUSGT {a.setLeftToRight(true);}
      |
        LTMINUS {a.setRightToLeft(true);}
      |
        LTMINUSGT {a.setBidirectional(true);}
      |
        MINUSMINUS {a.setSimple(true);}
    )

    (
        ( LPAREN   )  
        ( tmp7=Name  {a.setRightRole(convertName($tmp7));})  
        ( RPAREN   )  
    )
    ?
    (
        ( LBRACK   )  
         tmp8=cdqualifier {a.setRightQualifier(_localctx.tmp8.ret);}   
        ( RBRACK   )  
    )
    ?
     tmp9=qualifiedname {a.setRightReferenceName(_localctx.tmp9.ret);}   
    ( tmp10=cardinality {a.setRightCardinality(_localctx.tmp10.ret);} ) ? 
    ( tmp11=modifier {a.setRightModifier(_localctx.tmp11.ret);} ) ? 
    ( SEMI   )  
;




modifier_eof returns [de.cd4analysis._ast.ASTModifier ret = null] :
    tmp = modifier  {$ret = $tmp.ret;} EOF ;

modifier returns [de.cd4analysis._ast.ASTModifier ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTModifier a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTModifier();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=stereotype {a.setStereotype(_localctx.tmp0.ret);} ) ? 
    (
        'abstract' {a.setAbstract(true);}
      |
        'final' {a.setFinal(true);}
      |
        'static' {a.setStatic(true);}
      |
        'private' {a.setPrivate(true);}
      |
        MINUS {a.setPrivate(true);}
      |
        'protected' {a.setProtected(true);}
      |
        HASH {a.setProtected(true);}
      |
        'public' {a.setPublic(true);}
      |
        PLUS {a.setPublic(true);}
      |
        'derived' {a.setDerived(true);}
      |
        SLASH {a.setDerived(true);}
    )
    *
;




cardinality_eof returns [de.cd4analysis._ast.ASTCardinality ret = null] :
    tmp = cardinality  {$ret = $tmp.ret;} EOF ;

cardinality returns [de.cd4analysis._ast.ASTCardinality ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCardinality a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCardinality();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    LBRACKSTARRBRACK {a.setMany(true);}
  |
    LEXSYM0 {a.setOne(true);}
  |
    LEXSYM1 {a.setOneToMany(true);}
  |
    LEXSYM2 {a.setOptional(true);}
;




cdqualifier_eof returns [de.cd4analysis._ast.ASTCDQualifier ret = null] :
    tmp = cdqualifier  {$ret = $tmp.ret;} EOF ;

cdqualifier returns [de.cd4analysis._ast.ASTCDQualifier ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTCDQualifier a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTCDQualifier();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
  |
     tmp1=type {a.setType(_localctx.tmp1.ret);}   
;




stereotype_eof returns [de.cd4analysis._ast.ASTStereotype ret = null] :
    tmp = stereotype  {$ret = $tmp.ret;} EOF ;

stereotype returns [de.cd4analysis._ast.ASTStereotype ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTStereotype a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTStereotype();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( LTLT   )  
    (
         tmp0=stereovalue {a.getValues().add(_localctx.tmp0.ret);}   
        (
            ( COMMA   )  
             tmp1=stereovalue {a.getValues().add(_localctx.tmp1.ret);}   
        )
        *
    )

    ( GTGT   )  
;




stereovalue_eof returns [de.cd4analysis._ast.ASTStereoValue ret = null] :
    tmp = stereovalue  {$ret = $tmp.ret;} EOF ;

stereovalue returns [de.cd4analysis._ast.ASTStereoValue ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTStereoValue a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTStereoValue();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
    ( tmp0=Name  {a.setName(convertName($tmp0));})  
;




value_eof returns [de.cd4analysis._ast.ASTValue ret = null] :
    tmp = value  {$ret = $tmp.ret;} EOF ;

value returns [de.cd4analysis._ast.ASTValue ret = null]  
@init{// ret is normally returned, a is used to be compatible with rule using the return construct
de.cd4analysis._ast.ASTValue a = null;
a=de.cd4analysis._ast.CD4AnalysisNodeFactory.createASTValue();
a.setLanguageComponent(de.cd4analysis._ast.ASTConstantsCD4Analysis.LANGUAGE);
$ret=a;
a.set_SourcePositionStart( computeStartPosition(_input.LT(1)));
setActiveASTNode(a);
}
@after{a.set_SourcePositionEnd(computeEndPosition(_input.LT(-1)));}
:
     tmp0=literal {a.setLiteral(_localctx.tmp0.ret);}   
;




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
POINT : '.';
RBRACK : ']';
LEXSYM0 : '[1]';
MINUS : '-';
COMMA : ',';
LBRACK : '[';
PLUS : '+';
GTGTGT : '>>>';
STAR : '*';
POINTPOINTPOINT : '...';
RPAREN : ')';
LTLT : '<<';
LPAREN : '(';
LTMINUSGT : '<->';
AND : '&';
HASH : '#';
RCURLY : '}';
MINUSMINUS : '--';
LCURLY : '{';
LTMINUS : '<-';
LBRACKSTARRBRACK : '[*]';
QUESTION : '?';
LEXSYM2 : '[0..1]';
GT : '>';
EQUALS : '=';
LT : '<';
SEMI : ';';
GTGT : '>>';
LEXSYM1 : '[1..*]';
MINUSGT : '->';
SLASH : '/';
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

