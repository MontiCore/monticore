/* (c) https://github.com/MontiCore/monticore */

package mc.feature.expression;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.expression.expression3._ast.ASTExpr;
import mc.feature.expression.expression5.Expression5Mill;
import mc.feature.expression.expression5._ast.ASTMultExpr;
import mc.feature.expression.expression5._parser.Expression5Parser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(Expression5Mill.class)
public class Expression5Test {

  public Optional<ASTExpr> parse(String input) throws IOException {
    Expression5Parser parser = Expression5Mill.parser();
    return parser.parse_StringExpr(input);
  }
  
  @Test
  public void testExpr1() throws IOException {
    Optional<ASTExpr> res = parse("1*2+3");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTMultExpr.class, ast);
  }
  
  @Test
  public void testExpr2() throws IOException {
    Optional<ASTExpr> res = parse("1+2*3");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTMultExpr.class, ast);
  }
  
  @Test
  public void testExpr3() throws IOException {
    Optional<ASTExpr> res = parse("1*2*3");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTMultExpr.class, ast);
  }
}
