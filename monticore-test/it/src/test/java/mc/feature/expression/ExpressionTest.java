/* (c) https://github.com/MontiCore/monticore */

package mc.feature.expression;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.expression.expression.ExpressionMill;
import mc.feature.expression.expression._ast.ASTConstantsExpression;
import mc.feature.expression.expression._ast.ASTExpr;
import mc.feature.expression.expression._parser.ExpressionParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(ExpressionMill.class)
public class ExpressionTest {

  public Optional<ASTExpr> parse(String input) throws IOException {
    ExpressionParser parser = ExpressionMill.parser();
    return parser.parse_StringExpr(input);
  }
  
  @Test
  public void testPlus() throws IOException {
    Optional<ASTExpr> res = parse("1+2");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertEquals(ASTConstantsExpression.PLUS, ast.getOp());
  }
  
  @Test
  public void testLiteral() throws IOException {
    Optional<ASTExpr> res = parse("1");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertTrue(ast.isPresentNumericLiteral());
    assertEquals("1", ast.getNumericLiteral());
  }
  
  @Test
  public void testStar() throws IOException {
    Optional<ASTExpr> res = parse("1*2");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertEquals(ASTConstantsExpression.STAR, ast.getOp());
  }
  
  @Test
  public void testBracket() throws IOException {
    Optional<ASTExpr> res = parse("(1*2)");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertTrue(ast.isPresentExpr());
  }

  @Test
  public void testExpr1() throws IOException {
    Optional<ASTExpr> res = parse("1*2+3");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertEquals(ASTConstantsExpression.PLUS, ast.getOp());
  }
  
  @Test
  public void testExpr2() throws IOException {
    Optional<ASTExpr> res = parse("1+2*3");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertEquals(ASTConstantsExpression.PLUS, ast.getOp());
  }
  
  @Test
  public void testExpr3() throws IOException {
    Optional<ASTExpr> res = parse("1-2-3");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertEquals(ASTConstantsExpression.MINUS, ast.getOp());
  }

  @Test
  public void testPowerWithRightAssoc() throws IOException {
    Optional<ASTExpr> res = parse("2^3^4");
    assertTrue(res.isPresent());
    assertTrue(res.get().isPresentLeft());
    assertTrue(res.get().getLeft().isPresentNumericLiteral());
  }
  
}
