/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNullLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(TestMCCommonLiteralsMill.class)
public class NullAndBooleanLiteralsTest {

  @Test
  public void testNullLiteral() throws IOException {
    ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral("null");
    assertInstanceOf(ASTNullLiteral.class, lit);
  }
  
  @Test
  public void testBooleanLiterals() throws IOException {
    // literal "true":
    ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral("true");
    assertInstanceOf(ASTBooleanLiteral.class, lit);
    assertTrue(((ASTBooleanLiteral) lit).getValue());
    
    // literal "false":
    lit = MCLiteralsTestHelper.getInstance().parseLiteral("false");
    assertInstanceOf(ASTBooleanLiteral.class, lit);
    assertFalse(((ASTBooleanLiteral) lit).getValue());
  }
}
