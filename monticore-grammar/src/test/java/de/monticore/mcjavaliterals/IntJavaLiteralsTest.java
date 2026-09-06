/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcjavaliterals;

import de.monticore.literals.mcjavaliterals._ast.ASTIntLiteral;
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

@SuppressWarnings("OctalInteger")
@TestWithMCLanguage(TestMCJavaLiteralsMill.class)
public class IntJavaLiteralsTest {

  static Stream<Arguments> checkIntLiteralArgs() {
    return Stream.of(
        // decimal number
        Arguments.of(0, "0"),
        Arguments.of(123, "123"),
        Arguments.of(10, "10"),
        Arguments.of(5, "5"),
        
        // hexadezimal number
        Arguments.of(0x12, "0x12"),
        Arguments.of(0Xeff, "0Xeff"),
        Arguments.of(0x34567890, "0x34567890"),
        Arguments.of(0xabcdef, "0xabcdef"),
        Arguments.of(0x0, "0x0"),
        Arguments.of(0xa, "0xa"),
        Arguments.of(0xC0FFEE, "0xC0FFEE"),
        Arguments.of(0x005f, "0x005f"),
        
        // octal number
        Arguments.of(02, "02"),
        Arguments.of(07, "07"),
        Arguments.of(00, "00"),
        Arguments.of(076543210, "076543210"),
        Arguments.of(00017, "00017")
    );
  }
  
  @ParameterizedTest
  @MethodSource("checkIntLiteralArgs")
  public void checkIntLiteral(int i, String s) throws IOException {
    ASTLiteral lit = MCJavaLiteralsTestHelper.getInstance().parseLiteral(s);
    assertInstanceOf(ASTIntLiteral.class, lit);
    assertEquals(i, ((ASTIntLiteral) lit).getValue());
  }
}
