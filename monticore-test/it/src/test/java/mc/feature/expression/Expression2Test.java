/* (c) https://github.com/MontiCore/monticore */

package mc.feature.expression;

import mc.GeneratorIntegrationsTest;
import mc.feature.expression.expression2._ast.*;
import mc.feature.expression.expression2._parser.Expression2Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.Log;

public class Expression2Test extends GeneratorIntegrationsTest {
  
  public Optional<ASTExpr> parse(String input) throws IOException {
    Expression2Parser parser = new Expression2Parser();
    Optional<ASTExpr> res = parser.parseExpr(new StringReader(input));
    return res;
  }
  
  @Test
  public void testPlus() {
    try {
      Optional<ASTExpr> res = parse("1+2");
      Assertions.assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      Assertions.assertInstanceOf(ASTAddExpr.class, ast);
      Assertions.assertEquals(ASTConstantsExpression2.PLUS, ((ASTAddExpr) ast).getOp());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testLiteral() {
    try {
      Optional<ASTExpr> res = parse("1");
      Assertions.assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      Assertions.assertInstanceOf(ASTPrimaryExpr.class, ast);
      
      Assertions.assertEquals("1", ((ASTPrimaryExpr) ast).getNumericLiteral());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testStar() {
    try {
      Optional<ASTExpr> res = parse("1*2");
      Assertions.assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      Assertions.assertInstanceOf(ASTMultExpr.class, ast);
      
      Assertions.assertEquals(ASTConstantsExpression2.STAR, ((ASTMultExpr) ast).getOp());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBracket() {
    try {
      Optional<ASTExpr> res = parse("(1*2)");
      Assertions.assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      Assertions.assertInstanceOf(ASTBracketExpr.class, ast);
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testExpr1() {
    try {
      Optional<ASTExpr> res = parse("1*2+3");
      Assertions.assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      Assertions.assertInstanceOf(ASTAddExpr.class, ast);
      
      Assertions.assertEquals(ASTConstantsExpression2.PLUS, ((ASTAddExpr) ast).getOp());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testExpr2() {
    try {
      Optional<ASTExpr> res = parse("1+2*3");
      Assertions.assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      Assertions.assertInstanceOf(ASTAddExpr.class, ast);
      
      Assertions.assertEquals(ASTConstantsExpression2.PLUS, ((ASTAddExpr) ast).getOp());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testExpr3() {
    try {
      Optional<ASTExpr> res = parse("1-2-3");
      Assertions.assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      Assertions.assertInstanceOf(ASTAddExpr.class, ast);
      
      Assertions.assertEquals(ASTConstantsExpression2.MINUS, ((ASTAddExpr) ast).getOp());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testPowerWithRightAssoc() {
    try {
      Optional<ASTExpr> res = parse("2^3^4");
      Assertions.assertTrue(res.isPresent());
      Assertions.assertInstanceOf(ASTPowerExpr.class, res.get());
    } catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
