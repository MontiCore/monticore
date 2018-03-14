/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import de.monticore.mcliterals._ast.ASTBooleanLiteral;
import de.monticore.mcliterals._ast.ASTLiteral;
import de.monticore.mcliterals._ast.ASTNullLiteral;

/**
 * @author Martin Schindler
 */
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
