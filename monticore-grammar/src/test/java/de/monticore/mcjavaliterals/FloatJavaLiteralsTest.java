/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcjavaliterals;

import de.monticore.literals.mcjavaliterals._ast.ASTFloatLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.literals.testmcjavaliterals.TestMCJavaLiteralsMill;
import de.monticore.runtime.junit.TestWithMCLanguage;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

@TestWithMCLanguage(TestMCJavaLiteralsMill.class)
public class FloatJavaLiteralsTest {
  
  static Stream<Arguments> checkFloatLiteralArgs() {
    return Stream.of(
        Arguments.of(0F, "0F"),
        Arguments.of(0.0F, "0.0F"),
        Arguments.of(5F, "5F"),
        Arguments.of(.4F, ".4F"),
        Arguments.of(000009.3F, "000009.3F"),
        Arguments.of(5.F, "5.F"),
        Arguments.of(009.f, "009.f"),
        Arguments.of(009f, "009f"),
        Arguments.of(009e2f, "009e2f"),
        Arguments.of(23.4F, "23.4F"),
        Arguments.of(2e3F, "2e3F"),
        Arguments.of(2E-3F, "2E-3F"),
        Arguments.of(009f, "009f"),
        Arguments.of(.1e1F, ".1e1F"),
        Arguments.of(.1F, ".1F"),
        Arguments.of(.11e12F, ".11e12F"),
        Arguments.of(.11e+12F, ".11e+12F"),
        Arguments.of(29.18e08F, "29.18e08F"),
        Arguments.of(0029.0008e-00008F, "0029.0008e-00008F"),
        
        // hexadezimal number
        Arguments.of(0x5.p1f, "0x5.p1f"),
        // Arguments.of()(0x.5p1f, "0x.5p1f"),
        Arguments.of(0xFp-9f, "0xFp-9f"),
        Arguments.of(0xfP2F, "0xfP2F"),
        Arguments.of(0xfp1F, "0xfp1F"),
        Arguments.of(0x.fP1F, "0x.fP1F"),
        Arguments.of(0x0p0F, "0x0p0F"),
        Arguments.of(0x0.0p1F, "0x0.0p1F"),
        Arguments.of(0x.0p1F, "0x.0p1F"),
        Arguments.of(0x.5AFp1f, "0x.5AFp1f"),
        Arguments.of(0x0050AF.CD9p-008f, "0x0050AF.CD9p-008f"),
        Arguments.of(0x1.fffffeP+127f, "0x1.fffffeP+127f"),
        Arguments.of(0x0p-5f, "0x0p-5f"),
        Arguments.of(0x0p1F, "0x0p1F"),
        Arguments.of(0x0p-5F, "0x0p-5F"),
        
        // Examples from Java Language Specification
        Arguments.of(1e1f, "1e1f"),
        Arguments.of(2.f, "2.f"),
        Arguments.of(.3f, ".3f"),
        Arguments.of(0f, "0f"),
        Arguments.of(3.14f, "3.14f"),
        Arguments.of(6.022137e+23f, "6.022137e+23f")
    );
  }
  
  @ParameterizedTest
  @MethodSource("checkFloatLiteralArgs")
  public void checkFloatLiteral(float f, String s) throws IOException {
    ASTLiteral lit = MCJavaLiteralsTestHelper.getInstance().parseLiteral(s);
    assertInstanceOf(ASTFloatLiteral.class, lit);
    assertEquals(f, ((ASTFloatLiteral) lit).getValue(), 0);
  }
}
