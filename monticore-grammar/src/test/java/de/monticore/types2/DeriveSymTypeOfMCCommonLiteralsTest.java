/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types2;

import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;
import de.monticore.literals.mccommonliterals._ast.MCCommonLiteralsMill;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.monticore.mccommon._ast.MCCommonLiterals;
import de.se_rwth.commons.logging.Log;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class DeriveSymTypeOfMCCommonLiteralsTest {
  
  /**
   * Focus: Deriving Type of Literals, here:
   *    literals/MCLiteralsBasis.mc4
   */
  
  @BeforeClass
  public static void setup() {
    Log.init();
    Log.enableFailQuick(false);
  }
  
  // This is the core Visitor under Test (but rather empty)
  DeriveSymTypeOfLiterals derLit = new DeriveSymTypeOfMCCommonLiterals();
  
  // other arguments not used (and therefore deliberately null)
  
  // This is the TypeChecker under Test:
  TypeCheck tc = new TypeCheck(null,null,derLit);
  
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
    ASTLiteral lit = MCCommonLiteralsMill.basicLongLiteralBuilder().setDigits("17").setName("L").build();
    assertEquals("long", tc.typeOf(lit).print());
  }


  @Test
  public void deriveTFromLiteral1BasicFloat() throws IOException {
    ASTLiteral lit = MCCommonLiteralsMill.basicFloatLiteralBuilder().setPre("10").setPost("03").setName("f").build();
    assertEquals("float", tc.typeOf(lit).print());
  }


  @Test
  public void deriveTFromLiteral1BasicDouble() throws IOException {
    ASTLiteral lit = MCCommonLiteralsMill.basicDoubleLiteralBuilder().setPre("710").setPost("93").build();
    assertEquals("double", tc.typeOf(lit).print());
  }

}
