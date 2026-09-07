/* (c) https://github.com/MontiCore/monticore */

package mc.feature.expression;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.expression.expression4.Expression4Mill;
import mc.feature.expression.expression4._ast.*;
import mc.feature.expression.expression4._parser.Expression4Parser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(Expression4Mill.class)
public class Expression4Test {

  public Optional<ASTExpr> parse(String input) throws IOException {
    Expression4Parser parser = Expression4Mill.parser();
    return parser.parse_StringExpr(input);
  }
  
  static Stream<Arguments> testArgs() {
    return Stream.of(
        Arguments.of("1+2", ASTAddExpr.class),
        Arguments.of("1*2", ASTMultExpr.class),
        Arguments.of("(1*2)", ASTBracketExpr.class),
        Arguments.of("1*2+3", ASTAddExpr.class),
        Arguments.of("1+2*3", ASTAddExpr.class),
        Arguments.of("1-2-3", ASTAddExpr.class),
        Arguments.of("2^3^4", ASTPowerExpr.class)
    );
  }

  @ParameterizedTest
  @MethodSource("testArgs")
  public void testPlus(String input, Class<?> clazz) throws IOException {
    Optional<ASTExpr> res = parse(input);
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(clazz, ast);
  }
  
  @Test
  public void testLiteral() throws IOException {
    Optional<ASTExpr> res = parse("1");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTPrimaryExpr.class, ast);
    
    assertEquals("1", ((ASTPrimaryExpr) ast).getNumericLiteral());
  }

}
