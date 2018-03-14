/* (c) https://github.com/MontiCore/monticore */

package de.monticore.literals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import de.monticore.literals.literals._ast.ASTSignedLiteral;
import de.monticore.literals.literals._ast.ASTSignedLongLiteral;

/**
 * @author Martin Schindler
 */
public class SignedLongLiteralsTest {
  
  private void checkLongLiteral(long l, String s) throws IOException {
    ASTSignedLiteral lit = LiteralsTestHelper.getInstance().parseSignedLiteral(s);
    assertTrue(lit instanceof ASTSignedLongLiteral);
    assertEquals(l, ((ASTSignedLongLiteral) lit).getValue());
  }
  
  @Test
  public void testLongLiterals() {
    try {
      // decimal number
      checkLongLiteral(0L, "0L");
      checkLongLiteral(-0L, "-0L");
      checkLongLiteral(123L, "123L");
      checkLongLiteral(-123L, "-123L");
      
      // hexadezimal number
      checkLongLiteral(0x0L, "0x0L");
      checkLongLiteral(-0x0L, "-0x0L");
      checkLongLiteral(0x12L, "0x12L");
      checkLongLiteral(-0x12L, "-0x12L");
      checkLongLiteral(0XeffL, "0XeffL");
      checkLongLiteral(-0XeffL, "-0XeffL");
      
      // octal number
      checkLongLiteral(00L, "00L");
      checkLongLiteral(-00L, "-00L");
      checkLongLiteral(02L, "02L");
      checkLongLiteral(-02L, "-02L");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
