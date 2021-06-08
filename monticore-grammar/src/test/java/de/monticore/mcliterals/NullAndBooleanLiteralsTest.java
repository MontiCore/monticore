/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNullLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class NullAndBooleanLiteralsTest {
  
  @Test
  public void testNullLiteral() {
    try {
      ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral("null");
      assertTrue(lit instanceof ASTNullLiteral);
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testBooleanLiterals() {
    try {
      // literal "true":
      ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral("true");
      assertTrue(lit instanceof ASTBooleanLiteral);
      assertTrue(((ASTBooleanLiteral) lit).getValue());
      
      // literal "false":
      lit = MCLiteralsTestHelper.getInstance().parseLiteral("false");
      assertTrue(lit instanceof ASTBooleanLiteral);
      assertFalse(((ASTBooleanLiteral) lit).getValue());
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
