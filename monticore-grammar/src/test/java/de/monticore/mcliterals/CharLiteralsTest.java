/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._ast.ASTCharLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CharLiteralsTest {
  
  @Before
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    MCCommonLiteralsMill.reset();
    MCCommonLiteralsMill.init();
  }
  
  private void checkCharLiteral(char c, String s) throws IOException {
      ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral(s);
      assertTrue(lit instanceof ASTCharLiteral);
      assertEquals(c, ((ASTCharLiteral) lit).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
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
