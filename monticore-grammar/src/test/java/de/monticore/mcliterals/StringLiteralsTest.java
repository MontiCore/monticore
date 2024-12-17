/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.literals.testmccommonliterals._ast.ASTA;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class StringLiteralsTest {
  
  @BeforeEach
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestMCCommonLiteralsMill.reset();
    TestMCCommonLiteralsMill.init();
  }
  
  private void checkStringLiteral(String expected, String actual) throws IOException {
    ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral(actual);
    assertTrue(lit instanceof ASTStringLiteral);
    assertEquals(expected, ((ASTStringLiteral) lit).getValue());
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testName() throws IOException {
    Optional<ASTA> ast = TestMCCommonLiteralsMill.parser().parse_StringA("Meier");
    assertTrue(ast.isPresent());

    List<String> shouldParseName = List.of("Müller", "Vπ", "a", "b", "c"
        , "d", "e", "f", "g", "h");
    for (String s : shouldParseName) {
      ast = TestMCCommonLiteralsMill.parser().parse_StringA(s);
      assertTrue(ast.isPresent(), "Could not parse string '" + s + "'");
      assertEquals(s, ast.get().getName());
    }

    assertFalse(Character.isUnicodeIdentifierStart('1'));
    assertTrue(Character.isUnicodeIdentifierPart('1'));

    assertFalse(Character.isUnicodeIdentifierStart('.'));
    assertFalse(Character.isUnicodeIdentifierPart('.'));

    List<Character> notAllowed = List.of('.', '(', ')', '+', ',', '/', ' ');
    for (Character s : notAllowed) {
      assertFalse(Character.isUnicodeIdentifierPart(s), "Character <"+s+"> is not allowed");
      assertFalse(Character.isUnicodeIdentifierStart(s), "Character <"+s+"> is not allowed");
    }

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
      Assertions.fail(e.getMessage());
    }
  }
}
