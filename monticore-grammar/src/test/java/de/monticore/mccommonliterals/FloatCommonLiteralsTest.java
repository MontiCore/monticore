/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mccommonliterals;

import de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmccommonliterals.TestMCCommonLiteralsMill;
import de.monticore.literals.testmccommonliterals._parser.TestMCCommonLiteralsParser;
import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(TestMCCommonLiteralsMill.class)
public class FloatCommonLiteralsTest {
  
  static Stream<Arguments> checkFloatLiteralArgs() {
    return Stream.of(
        Arguments.of(0.0f, "0.0f"),
        Arguments.of(23.4f, "23.4f")
    );
  }

  @ParameterizedTest
  @MethodSource("checkFloatLiteralArgs")
  public void checkFloatLiteral(float f, String s) throws IOException {
    TestMCCommonLiteralsParser parser = TestMCCommonLiteralsMill.parser();
    Optional<ASTLiteral> lit = parser.parseLiteral(new StringReader(s));
    assertTrue(lit.isPresent());
    assertInstanceOf(ASTBasicFloatLiteral.class, lit.get());
    assertEquals(f, ((ASTBasicFloatLiteral) lit.get()).getValue(), 0);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      "0F",
      ".4F",
      "5.F",
      "009.f",
      "009f",
      "009e2f",
      "2e3F",
      "2E-3F",
      "009f",
      ".1e1F",
      ".1F",
      ".11e12F",
      ".11e+12F",
      "29.18e08F",
      "0029.0008e-00008F",
      "0. 0f",
      "0 .0f",
      "23.4 f",
      
      // hexadezimal number
      "0x5.p1f",
      "0x.5p1f",
      "0xFp-9f",
      "0xfP2F",
      "0xfp1F",
      "0x.fP1F",
      "0x0p0F",
      "0x0.0p1F",
      "0x.0p1F",
      "0x.5AFp1f",
      "0x0050AF.CD9p-008f",
      "0x1.fffffeP+127f",
      "0x0p-5f",
      "0x0p1F",
      "0x0p-5F",
      
      // Examples from Java Language Specification
      "1e1f",
      "2.f",
      ".3f",
      "0f",
      "6.022137e+23f",
  })
  public void checkFalse(String s) throws IOException {
    TestMCCommonLiteralsParser parser = TestMCCommonLiteralsMill.parser();
    Optional<ASTBasicFloatLiteral> lit = parser.parseBasicFloatLiteral(new StringReader(s));
    assertFalse(lit.isPresent());
    
    Log.getFindings().removeAll(
        MCAssertions.assertHasFindingsStartingWith("rule basicFloatLiteral failed predicate"));
    
    if (Log.getFindingsCount() == 1) {
      Log.getFindings().remove(
          MCAssertions.assertHasFindingStartingWith("token recognition error"));
    }
  }
}
