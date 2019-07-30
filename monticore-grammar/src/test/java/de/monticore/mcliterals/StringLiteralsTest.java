/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class StringLiteralsTest {
  
  private void checkStringLiteral(String expected, String actual) throws IOException {
    ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral(actual);
    assertTrue(lit instanceof ASTStringLiteral);
    assertEquals(expected, ((ASTStringLiteral) lit).getValue());
  }
  
  @Test
  public void testStringLiterals() {
    try {
      checkStringLiteral("abc ABC", "\"abc ABC\"");
      checkStringLiteral("a", "\"a\"");
      checkStringLiteral(" ", "\" \"");
      checkStringLiteral(" a ", "\" a \"");
      checkStringLiteral("\n", "\"\\n\"");
      checkStringLiteral("\r", "\"\\r\"");
      checkStringLiteral("", "\"\"");
      checkStringLiteral("\\", "\"\\\\\"");
      checkStringLiteral("\"", "\"\\\"\"");
      checkStringLiteral("!\"§\\%&{([)]=}?´`*+~'#-_.:,;<>|^°@€",
          "\"!\\\"§\\\\%&{([)]=}?´`*+~'#-_.:,;<>|^°@€\"");
      
      // Escape Sequences:
      checkStringLiteral("\b\t\n\f\r\"\'\\", "\"\\b\\t\\n\\f\\r\\\"\\'\\\\\"");
      
      // Unicode:
      checkStringLiteral("\u00ef", "\"\\u00ef\"");
      checkStringLiteral("\u0000", "\"\\u0000\"");
      checkStringLiteral("\uffff", "\"\\uffff\"");
      checkStringLiteral("\u00aaf\u00dd1 123", "\"\\u00aaf\\u00dd1 123\"");
      checkStringLiteral("\u010000", "\"\\u010000\"");
    }
    catch (IOException e) {
      fail(e.getMessage());
    }
  }
}
