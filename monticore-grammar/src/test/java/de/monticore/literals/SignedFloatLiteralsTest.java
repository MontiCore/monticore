/* (c) https://github.com/MontiCore/monticore */

package de.monticore.literals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import de.monticore.literals.literals._ast.ASTSignedFloatLiteral;
import de.monticore.literals.literals._ast.ASTSignedLiteral;

/**
 * @author Martin Schindler
 */
public class SignedFloatLiteralsTest {
  
  private void checkFloatLiteral(float f, String s) throws IOException {
    ASTSignedLiteral lit = LiteralsTestHelper.getInstance().parseSignedLiteral(s);
    assertTrue(lit instanceof ASTSignedFloatLiteral);
    assertEquals(f, ((ASTSignedFloatLiteral) lit).getValue(), 0);
  }
  
  @Test
  public void testFloatLiterals() {
    try {
      // decimal number
      checkFloatLiteral(5F, "5F");
      checkFloatLiteral(5F, "5F");
      checkFloatLiteral(-5F, "-5F");
      checkFloatLiteral(-0.0F, "-0.0F");
      checkFloatLiteral(.4F, ".4F");
      checkFloatLiteral(-.4F, "-.4F");
      checkFloatLiteral(0E-3F, "0E-3F");
      checkFloatLiteral(0E-3F, "0E-3F");
      checkFloatLiteral(-2E-3F, "-2E-3F");
      
      // hexadezimal number
      checkFloatLiteral(0x5.p1f, "0x5.p1f");
      checkFloatLiteral(-0x5.p1f, "-0x5.p1f");
      checkFloatLiteral(0x0050AF.CD9p-008f, "0x0050AF.CD9p-008f");
      checkFloatLiteral(-0x0050AF.CD9p-008f, "-0x0050AF.CD9p-008f");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
