/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcjavaliterals;

import de.monticore.literals.mcjavaliterals._ast.ASTDoubleLiteral;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

public class DoubleJavaLiteralsTest {
  
  static Stream<Arguments> checkDoubleLiteralArgs() {
    return Stream.of(
        // decimal number
        Arguments.of(0.0, "0.0"),
        Arguments.of(.0, ".0"),
        Arguments.of(0., "0."),
        Arguments.of(5d, "5d"),
        Arguments.of(.4, ".4"),
        Arguments.of(000009.3, "000009.3"),
        Arguments.of(5., "5."),
        Arguments.of(009., "009."),
        Arguments.of(009e2, "009e2"),
        Arguments.of(23.4, "23.4"),
        Arguments.of(2e3, "2e3"),
        Arguments.of(2E-3, "2E-3"),
        Arguments.of(009d, "009d"),
        Arguments.of(.1e1, ".1e1"),
        Arguments.of(.1, ".1"),
        Arguments.of(.11e12, ".11e12"),
        Arguments.of(.11e+12, ".11e+12"),
        Arguments.of(29.18e08, "29.18e08"),
        Arguments.of(0029.0008e-00008, "0029.0008e-00008"),
        Arguments.of(0029.0008e-00008D, "0029.0008e-00008D"),
        
        // hexadezimal number
        Arguments.of(0x5.p1, "0x5.p1"),
        Arguments.of(0x.5p1, "0x.5p1"),
        Arguments.of(0xFp-9, "0xFp-9"),
        Arguments.of(0xfP2, "0xfP2"),
        Arguments.of(0xfp1, "0xfp1"),
        Arguments.of(0x.fP1, "0x.fP1"),
        Arguments.of(0x0p0, "0x0p0"),
        Arguments.of(0x0.0p1, "0x0.0p1"),
        Arguments.of(0x.0p1, "0x.0p1"),
        Arguments.of(0x.5AFp1, "0x.5AFp1"),
        Arguments.of(0x0050AF.CD9p-008, "0x0050AF.CD9p-008"),
        Arguments.of(0x0050AF.CD9p-008d, "0x0050AF.CD9p-008d"),
        Arguments.of(0x0p-5, "0x0p-5"),
        Arguments.of(0x0p1, "0x0p1"),
        Arguments.of(0x0p1d, "0x0p1d"),
        
        // Examples from Java Language Specification
        Arguments.of(.3, ".3"),
        Arguments.of(1e1, "1e1"),
        Arguments.of(2., "2."),
        Arguments.of(0.0, "0.0"),
        Arguments.of(3.14, "3.14"),
        Arguments.of(1e-9d, "1e-9d"),
        Arguments.of(1e137, "1e137")
    );
  }

  @ParameterizedTest
  @MethodSource("checkDoubleLiteralArgs")
  public void checkDoubleLiteral(double d, String s) throws IOException {
    ASTLiteral lit = MCJavaLiteralsTestHelper.getInstance().parseLiteral(s);
    assertInstanceOf(ASTDoubleLiteral.class, lit);
    assertEquals(d, ((ASTDoubleLiteral) lit).getValue(), 0);
  }
}
