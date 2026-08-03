/* (c) https://github.com/MontiCore/monticore */

package mc.feature.expression;

import de.monticore.runtime.junit.TestWithMCLanguage;
import mc.feature.expression.expression4.Expression4Mill;
import mc.feature.expression.expression4._ast.*;
import mc.feature.expression.expression4._parser.Expression4Parser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestWithMCLanguage(Expression4Mill.class)
public class Expression4Test {

  public Optional<ASTExpr> parse(String input) throws IOException {
    Expression4Parser parser = Expression4Mill.parser();
    return parser.parse_StringExpr(input);
  }
  
  @Test
  public void testPlus() throws IOException {
    Optional<ASTExpr> res = parse("1+2");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTAddExpr.class, ast);
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
  }
  
  @Test
  public void testExpr2() throws IOException {
    Optional<ASTExpr> res = parse("1+2*3");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTAddExpr.class, ast);
  }
  
  @Test
  public void testExpr3() throws IOException {
    Optional<ASTExpr> res = parse("1-2-3");
    assertTrue(res.isPresent());
    ASTExpr ast = res.get();
    assertInstanceOf(ASTAddExpr.class, ast);
  }
  
  @Test
  public void testPowerWithRightAssoc() throws IOException {
    Optional<ASTExpr> res = parse("2^3^4");
    assertTrue(res.isPresent());
    assertInstanceOf(ASTPowerExpr.class, res.get());
  }
  
}
