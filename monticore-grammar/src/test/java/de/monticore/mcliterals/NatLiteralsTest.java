/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcliterals;

import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(TestMCCommonLiteralsMill.class)
public class NatLiteralsTest {
  
  static Stream<Arguments> checkNatLiteralArgs() {
    return Stream.of(
        Arguments.of(0, "0"),
        Arguments.of(123, "123"),
        Arguments.of(10, "10"),
        Arguments.of(5, "5")
    );
  }
  
  @ParameterizedTest
  @MethodSource("checkNatLiteralArgs")
  public void checkNatLiteral(int i, String s) throws IOException {
    TestMCCommonLiteralsParser parser = TestMCCommonLiteralsMill.parser();
    Optional<ASTNatLiteral> ast = parser.parse_StringNatLiteral(s);
    assertFalse(parser.hasErrors());
    assertTrue(ast.isPresent());
    assertEquals(i, ast.get().getValue());
  }
  
  @ParameterizedTest
  @ValueSource(strings = {"0x5", "-5"})
  public void checkFailingNatLiteral(String s) throws IOException {
    TestMCCommonLiteralsParser parser = TestMCCommonLiteralsMill.parser();
    parser.parse_StringNatLiteral(s);
    assertTrue(parser.hasErrors());
  }
}
