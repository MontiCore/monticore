/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.literals.mccommonliterals._ast.ASTCharLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CharLiteralsTest {
  
  private void checkCharLiteral(char c, String s) throws IOException {
      ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral(s);
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
