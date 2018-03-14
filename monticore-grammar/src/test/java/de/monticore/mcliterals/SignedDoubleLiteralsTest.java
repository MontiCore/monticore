/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import de.monticore.mcliterals._ast.ASTSignedDoubleLiteral;
import de.monticore.mcliterals._ast.ASTSignedLiteral;

/**
 * @author Martin Schindler
 */
public class SignedDoubleLiteralsTest {
  
  private void checkDoubleLiteral(double d, String s) throws IOException {
    ASTSignedLiteral lit = MCLiteralsTestHelper.getInstance().parseSignedLiteral(s);
    assertTrue(lit instanceof ASTSignedDoubleLiteral);
    assertEquals(d, ((ASTSignedDoubleLiteral) lit).getValue(), 0);
  }
  
  @Test
  public void testDoubleLiterals() {
    try {
      // decimal number
      checkDoubleLiteral(0.0, "0.0");
      checkDoubleLiteral(-0.0, "-0.0");
      checkDoubleLiteral(5d, "5d");
      checkDoubleLiteral(-5d, "-5d");
      checkDoubleLiteral(.4, ".4");
      checkDoubleLiteral(-.4, "-.4");
      checkDoubleLiteral(2E-3, "2E-3");
      checkDoubleLiteral(-2E-3, "-2E-3");
      
      // hexadezimal number
      checkDoubleLiteral(0x5.p1, "0x5.p1");
      checkDoubleLiteral(-0x5.p1, "-0x5.p1");
      checkDoubleLiteral(1e-9d, "1e-9d");
      checkDoubleLiteral(-1e-9d, "-1e-9d");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
