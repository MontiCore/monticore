/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SuppressWarnings({ "UnnecessaryUnicodeEscape", "UnnecessaryStringEscape" })
@TestWithMCLanguage(TestMCCommonLiteralsMill.class)
public class StringLiteralsTest {
  
  static Stream<Arguments> checkStringLiteralArgs() {
    return Stream.of(
      Arguments.of("abc ABC", "\"abc ABC\""),
      Arguments.of("a", "\"a\""),
      Arguments.of(" ", "\" \""),
      Arguments.of(" a ", "\" a \""),
      Arguments.of("\n", "\"\\n\""),
      Arguments.of("\r", "\"\\r\""),
      Arguments.of("", "\"\""),
      Arguments.of("\\", "\"\\\\\""),
      Arguments.of("\"", "\"\\\"\""),
      Arguments.of("!\"§\\%&{([)]=}?´`*+~'#-_.:,;<>|^°@€",
          "\"!\\\"§\\\\%&{([)]=}?´`*+~'#-_.:,;<>|^°@€\""),
        
      // Escape Sequences:
      Arguments.of("\b\t\n\f\r\"\'\\", "\"\\b\\t\\n\\f\\r\\\"\\'\\\\\""),
      
      // Unicode:
      Arguments.of("\u00ef", "\"\\u00ef\""),
      Arguments.of("\u0000", "\"\\u0000\""),
      Arguments.of("\uffff", "\"\\uffff\""),
      Arguments.of("\u00aaf\u00dd1 123", "\"\\u00aaf\\u00dd1 123\""),
      Arguments.of("\u010000", "\"\\u010000\"")
    );
  }

  @ParameterizedTest
  @MethodSource("checkStringLiteralArgs")
  public void checkStringLiteral(String expected, String actual) throws IOException {
    ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral(actual);
    assertInstanceOf(ASTStringLiteral.class, lit);
    assertEquals(expected, ((ASTStringLiteral) lit).getValue());
  }
}
