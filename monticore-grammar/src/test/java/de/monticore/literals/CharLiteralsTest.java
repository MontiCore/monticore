/* (c) https://github.com/MontiCore/monticore */

package de.monticore.literals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import de.monticore.literals.literals._ast.ASTCharLiteral;
import de.monticore.literals.literals._ast.ASTLiteral;

/**
 * @author Martin Schindler
 */
public class CharLiteralsTest {
  
  private void checkCharLiteral(char c, String s) throws IOException {
      ASTLiteral lit = LiteralsTestHelper.getInstance().parseLiteral(s);
      assertTrue(lit instanceof ASTCharLiteral);
      assertEquals(c, ((ASTCharLiteral) lit).getValue());
  }
  
  @Test
  public void testCharLiterals() {
    try {
      checkCharLiteral('a', "'a'");
      checkCharLiteral(' ', "' '");
      checkCharLiteral('@', "'@'");
      // checkCharLiteral('§', "'§'");
      
      // Escape Sequences:
      checkCharLiteral('\b', "'\\b'");
      checkCharLiteral('\t', "'\\t'");
      checkCharLiteral('\n', "'\\n'");
      checkCharLiteral('\f', "'\\f'");
      checkCharLiteral('\r', "'\\r'");
      checkCharLiteral('\"', "'\\\"'");
      checkCharLiteral('\'', "'\\\''");
      checkCharLiteral('\\', "'\\\\'");
      
      // Unicode:
      checkCharLiteral('\u00ef', "'\\u00ef'");
      checkCharLiteral('\u0000', "'\\u0000'");
      checkCharLiteral('\uffff', "'\\uffff'");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
    
  }
}
