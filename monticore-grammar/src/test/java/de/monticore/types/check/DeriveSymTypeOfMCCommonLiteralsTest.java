/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.prettyprint.CombineExpressionsWithLiteralsPrettyPrinter;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.*;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class DeriveSymTypeOfMCCommonLiteralsTest {
  
  /**
   * Focus: Deriving Type of Literals, here:
   *    literals/MCLiteralsBasis.mc4
   */
  
  @BeforeClass
  public static void setup() {
    LogStub.init();         // replace log by a sideffect free variant
    // LogStub.initPlusLog();  // for manual testing purpose only
    Log.enableFailQuick(false);
  }
  
  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfCombineExpressionsDelegator derLit = new DeriveSymTypeOfCombineExpressionsDelegator();
  
  // other arguments not used (and therefore deliberately null)
  
  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null,derLit);
  
  // ------------------------------------------------------  Tests for Function 2b

  // Mill used ... alternative would be a Parser for Literals
  @Test
  @Ignore
  public void deriveTFromLiteral1Null() throws IOException {
    ASTLiteral lit = MCCommonLiteralsMill.nullLiteralBuilder().build();
    assertEquals("null", tc.typeOf(lit).print());
  }

  @Test
  public void deriveTFromLiteral1Boolean() throws IOException {
    ASTLiteral lit = MCCommonLiteralsMill.booleanLiteralBuilder().setSource(0).build();
    assertEquals("boolean", tc.typeOf(lit).print());
  }

  @Test
  public void deriveTFromLiteral1Char() throws IOException {
    ASTLiteral lit = MCCommonLiteralsMill.charLiteralBuilder().setSource("c").build();
    assertEquals("char", tc.typeOf(lit).print());
  }

  @Test
  public void deriveTFromLiteral1String() throws IOException {
    ASTLiteral lit = MCCommonLiteralsMill.stringLiteralBuilder().setSource("hjasdk").build();
    assertEquals("String", tc.typeOf(lit).print());
  }

  @Test
  public void deriveTFromLiteral1Int() throws IOException {
    ASTLiteral lit = MCCommonLiteralsMill.natLiteralBuilder().setDigits("17").build();
    assertEquals("int", tc.typeOf(lit).print());
  }

  @Test
  public void deriveTFromLiteral1BasicLong() throws IOException {
    ASTLiteral lit = MCCommonLiteralsMill.basicLongLiteralBuilder().setDigits("17").build();
    assertEquals("long", tc.typeOf(lit).print());
  }


  @Test
  public void deriveTFromLiteral1BasicFloat() throws IOException {
    ASTLiteral lit = MCCommonLiteralsMill.basicFloatLiteralBuilder().setPre("10").setPost("03").build();
    assertEquals("float", tc.typeOf(lit).print());
  }


  @Test
  public void deriveTFromLiteral1BasicDouble() throws IOException {
    ASTLiteral lit = MCCommonLiteralsMill.basicDoubleLiteralBuilder().setPre("710").setPost("93").build();
    assertEquals("double", tc.typeOf(lit).print());
  }

}
