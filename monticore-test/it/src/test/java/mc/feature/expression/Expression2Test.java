/* (c) https://github.com/MontiCore/monticore */

package mc.feature.expression;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.expression.expression2.Expression2Mill;
import mc.feature.expression.expression2._ast.*;
import mc.feature.expression.expression2._parser.Expression2Parser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(Expression2Mill.class)
public class Expression2Test {

  public Optional<ASTExpr> parse(String input) throws IOException {
    Expression2Parser parser = Expression2Mill.parser();
    return parser.parse_StringExpr(input);
  }
  
  @Test
  public void testPlus() throws IOException {
    Optional<ASTExpr> res = parse("1+2");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTAddExpr.class, ast);
    assertEquals(ASTConstantsExpression2.PLUS, ((ASTAddExpr) ast).getOp());
  }
  
  @Test
  public void testLiteral() throws IOException {
    Optional<ASTExpr> res = parse("1");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTPrimaryExpr.class, ast);
    
    assertEquals("1", ((ASTPrimaryExpr) ast).getNumericLiteral());
  }
  
  @Test
  public void testStar() throws IOException {
    Optional<ASTExpr> res = parse("1*2");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTMultExpr.class, ast);
    
    assertEquals(ASTConstantsExpression2.STAR, ((ASTMultExpr) ast).getOp());
  }
  
  @Test
  public void testBracket() throws IOException {
    Optional<ASTExpr> res = parse("(1*2)");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTBracketExpr.class, ast);
  }
  
  @Test
  public void testExpr1() throws IOException {
    Optional<ASTExpr> res = parse("1*2+3");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTAddExpr.class, ast);
    
    assertEquals(ASTConstantsExpression2.PLUS, ((ASTAddExpr) ast).getOp());
  }
  
  @Test
  public void testExpr2() throws IOException {
    Optional<ASTExpr> res = parse("1+2*3");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTAddExpr.class, ast);
    
    assertEquals(ASTConstantsExpression2.PLUS, ((ASTAddExpr) ast).getOp());
  }
  
  @Test
  public void testExpr3() throws IOException {
    Optional<ASTExpr> res = parse("1-2-3");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTAddExpr.class, ast);
    
    assertEquals(ASTConstantsExpression2.MINUS, ((ASTAddExpr) ast).getOp());
  }
  
  @Test
  public void testPowerWithRightAssoc() throws IOException {
    Optional<ASTExpr> res = parse("2^3^4");
    assertTrue(res.isPresent());
    assertInstanceOf(ASTPowerExpr.class, res.get());
  }
  
}
