/* (c) https://github.com/MontiCore/monticore */

package de.monticore.mcjavaliterals;

import de.monticore.literals.mcjavaliterals._ast.ASTLongLiteral;
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
public class LongJavaLiteralsTest {

  static Stream<Arguments> checkLongLiteralArgs() {
    return Stream.of(
        Arguments.of(0L, "0L"),
        Arguments.of(123L, "123L"),
        Arguments.of(10L, "10L"),
        Arguments.of(5L, "5L"),
        
        Arguments.of(0x12L, "0x12L"),
        Arguments.of(0XeffL, "0XeffL"),
        Arguments.of(0x1234567890L, "0x1234567890L"),
        Arguments.of(0xabcdefL, "0xabcdefL"),
        Arguments.of(0x0L, "0x0L"),
        Arguments.of(0xaL, "0xaL"),
        Arguments.of(0xC0FFEEL, "0xC0FFEEL"),
        Arguments.of(0x005fL, "0x005fL"),
        
        Arguments.of(02L, "02L"),
        Arguments.of(07L, "07L"),
        Arguments.of(00L, "00L"),
        Arguments.of(076543210L, "076543210L"),
        Arguments.of(00017L, "00017L")
    );
  }
  
  @ParameterizedTest
  @MethodSource("checkLongLiteralArgs")
  public void checkLongLiteral(long l, String s) throws IOException {
    ASTLiteral lit = MCJavaLiteralsTestHelper.getInstance().parseLiteral(s);
    assertInstanceOf(ASTLongLiteral.class, lit);
    assertEquals(l, ((ASTLongLiteral) lit).getValue());
  }
}
