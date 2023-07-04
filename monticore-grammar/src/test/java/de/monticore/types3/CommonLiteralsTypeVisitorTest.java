/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types3.util.DefsTypesForTests;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class CommonLiteralsTypeVisitorTest extends AbstractTypeVisitorTest {

  @Before
  public void setupForEach() {
    setupValues();
  }

  @Test
  public void deriveTFromLiteral1() throws IOException {
    checkExpr("17", "int");
  }

  @Test
  public void deriveTFromLiteral2() throws IOException {
    checkExpr("true", "boolean");
  }

  @Test
  public void deriveTFromLiteral3() throws IOException {
    checkExpr("17.3", "double");
  }

  @Test
  public void deriveTFromLiteral1Null() {
    ASTLiteral lit = MCCommonLiteralsMill.nullLiteralBuilder().build();
    check(lit, "null");
  }

  @Test
  public void deriveTFromLiteral1Boolean() {
    ASTLiteral lit = MCCommonLiteralsMill.booleanLiteralBuilder()
        .setSource(0)
        .build();
    check(lit, "boolean");
  }

  @Test
  public void deriveTFromLiteral1Char() {
    ASTLiteral lit = MCCommonLiteralsMill.charLiteralBuilder()
        .setSource("c")
        .build();
    check(lit, "char");
  }

  @Test
  public void deriveTFromLiteral1String() {
    ASTLiteral lit = MCCommonLiteralsMill.stringLiteralBuilder()
        .setSource("Y05H1")
        .build();
    check(lit, "String");
  }

  @Test
  public void deriveTFromLiteralStringUnBoxedAvailable() {
    // only String is available
    MCCommonLiteralsMill.globalScope().clear();
    DefsTypesForTests.set_unboxedObjects();
    ASTLiteral lit = MCCommonLiteralsMill.stringLiteralBuilder()
        .setSource("G0M84")
        .build();
    check(lit, "String");
  }

  @Test
  public void deriveTFromLiteralStringBoxedAvailable() {
    // only java.util.String is available
    MCCommonLiteralsMill.globalScope().clear();
    DefsTypesForTests.set_boxedObjects();
    ASTLiteral lit = MCCommonLiteralsMill.stringLiteralBuilder()
        .setSource("W4210")
        .build();
    check(lit, "java.lang.String");
  }

  @Test
  public void deriveTFromLiteralStringUnavailable() {
    // only java.util.String is available
    MCCommonLiteralsMill.globalScope().clear();
    ASTLiteral lit = MCCommonLiteralsMill.stringLiteralBuilder()
        .setSource("50N1C")
        .build();
    lit.setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope());
    lit.accept(getTypeMapTraverser());
    assertFalse(getType4Ast().hasTypeOfExpression(lit));
    assertHasErrorCode("0xD02A6");
  }

  @Test
  public void deriveTFromLiteral1Int() {
    ASTLiteral lit = MCCommonLiteralsMill.natLiteralBuilder()
        .setDigits("17")
        .build();
    check(lit, "int");
  }

  @Test
  public void deriveTFromLiteral1BasicLong() {
    ASTLiteral lit = MCCommonLiteralsMill.basicLongLiteralBuilder()
        .setDigits("17")
        .build();
    check(lit, "long");
  }

  @Test
  public void deriveTFromLiteral1BasicFloat() {
    ASTLiteral lit = MCCommonLiteralsMill.basicFloatLiteralBuilder()
        .setPre("10")
        .setPost("03")
        .build();
    check(lit, "float");
  }

  @Test
  public void deriveTFromLiteral1BasicDouble() {
    ASTLiteral lit = MCCommonLiteralsMill.basicDoubleLiteralBuilder()
        .setPre("710")
        .setPost("93")
        .build();
    check(lit, "double");
  }

  protected void check(ASTLiteral lit, String expected) {
    lit.setEnclosingScope(CombineExpressionsWithLiteralsMill.globalScope());
    lit.accept(getTypeMapTraverser());
    assertEquals(expected, getType4Ast().getTypeOfExpression(lit).printFullName());
    assertNoFindings();
  }

}
