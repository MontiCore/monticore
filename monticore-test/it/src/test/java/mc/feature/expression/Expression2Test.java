/* (c) https://github.com/MontiCore/monticore */

package mc.feature.expression;

import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;
import mc.feature.expression.expression2._ast.*;
import mc.feature.expression.expression2._parser.Expression2Parser;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.Assert.*;
import de.se_rwth.commons.logging.Log;

public class Expression2Test extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  public Optional<ASTExpr> parse(String input) throws IOException {
    Expression2Parser parser = new Expression2Parser();
    Optional<ASTExpr> res = parser.parseExpr(new StringReader(input));
    return res;
  }
  
  @Test
  public void testPlus() {
    try {
      Optional<ASTExpr> res = parse("1+2");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTAddExpr);
      assertEquals(ASTConstantsExpression2.PLUS, ((ASTAddExpr) ast).getOp());
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testLiteral() {
    try {
      Optional<ASTExpr> res = parse("1");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTPrimaryExpr);
      
      assertEquals("1", ((ASTPrimaryExpr) ast).getNumericLiteral());
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testStar() {
    try {
      Optional<ASTExpr> res = parse("1*2");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTMultExpr);
      
      assertEquals(ASTConstantsExpression2.STAR, ((ASTMultExpr) ast).getOp());
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testBracket() {
    try {
      Optional<ASTExpr> res = parse("(1*2)");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTBracketExpr);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testExpr1() {
    try {
      Optional<ASTExpr> res = parse("1*2+3");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTAddExpr);
      
      assertEquals(ASTConstantsExpression2.PLUS, ((ASTAddExpr) ast).getOp());
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testExpr2() {
    try {
      Optional<ASTExpr> res = parse("1+2*3");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTAddExpr);
      
      assertEquals(ASTConstantsExpression2.PLUS, ((ASTAddExpr) ast).getOp());
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testExpr3() {
    try {
      Optional<ASTExpr> res = parse("1-2-3");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTAddExpr);
      
      assertEquals(ASTConstantsExpression2.MINUS, ((ASTAddExpr) ast).getOp());
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testPowerWithRightAssoc() {
    try {
      Optional<ASTExpr> res = parse("2^3^4");
      assertTrue(res.isPresent());
      assertTrue(res.get() instanceof ASTPowerExpr);
    } catch (Exception e) {
      fail(e.getMessage());
    }
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
