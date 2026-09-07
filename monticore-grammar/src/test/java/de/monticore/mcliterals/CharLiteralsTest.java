/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mccommonliterals._ast.ASTCharLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@SuppressWarnings({ "UnnecessaryUnicodeEscape", "UnnecessaryStringEscape" })
@TestWithMCLanguage(MCCommonLiteralsMill.class)
public class CharLiteralsTest {
  
  static Stream<Arguments> checkCharLiteralArgs() {
    return Stream.of(
        Arguments.of('a', "'a'"),
        Arguments.of(' ', "' '"),
        Arguments.of('@', "'@'"),
        //Arguments.of('§', "'§'"),
        
        // Escape Sequences:
        Arguments.of('\b', "'\\b'"),
        Arguments.of('\t', "'\\t'"),
        Arguments.of('\n', "'\\n'"),
        Arguments.of('\f', "'\\f'"),
        Arguments.of('\r', "'\\r'"),
        Arguments.of('\"', "'\\\"'"),
        Arguments.of('\'', "'\\\''"),
        Arguments.of('\\', "'\\\\'"),
        
        // Unicode:
        Arguments.of('\u00ef', "'\\u00ef'"),
        Arguments.of('\u0000', "'\\u0000'"),
        Arguments.of('\uffff', "'\\uffff'")
    );
  }
  
  @ParameterizedTest
  @MethodSource("checkCharLiteralArgs")
  public void checkCharLiteral(char c, String s) throws IOException {
      ASTLiteral lit = MCLiteralsTestHelper.getInstance().parseLiteral(s);
    assertInstanceOf(ASTCharLiteral.class, lit);
      assertEquals(c, ((ASTCharLiteral) lit).getValue());
  }
}
