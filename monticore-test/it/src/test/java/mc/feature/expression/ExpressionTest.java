/* (c) https://github.com/MontiCore/monticore */

package mc.feature.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.expression.expression._ast.ASTConstantsExpression;
import mc.feature.expression.expression._ast.ASTExpr;
import mc.feature.expression.expression._parser.ExpressionParser;

public class ExpressionTest extends GeneratorIntegrationsTest {
  
  @Before
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  public Optional<ASTExpr> parse(String input) throws IOException {
    ExpressionParser parser = new ExpressionParser();
    Optional<ASTExpr> res = parser.parseExpr(new StringReader(input));
    return res;
  }
  
  @Test
  public void testPlus() {
    try {
      Optional<ASTExpr> res = parse("1+2");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertEquals(ASTConstantsExpression.PLUS, ast.getOp());
    }
    catch (Exception e) {
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
      assertTrue(ast.isPresentNumericLiteral());
      assertEquals("1", ast.getNumericLiteral());
    }
    catch (Exception e) {
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
      assertEquals(ASTConstantsExpression.STAR, ast.getOp());
    }
    catch (Exception e) {
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
      assertTrue(ast.isPresentExpr());
    }
    catch (Exception e) {
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
      assertEquals(ASTConstantsExpression.PLUS, ast.getOp());
    }
    catch (Exception e) {
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
      assertEquals(ASTConstantsExpression.PLUS, ast.getOp());
    }
    catch (Exception e) {
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
      assertEquals(ASTConstantsExpression.MINUS, ast.getOp());
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPowerWithRightAssoc() {
    try {
      Optional<ASTExpr> res = parse("2^3^4");
      assertTrue(res.isPresent());
      assertTrue(res.get().isPresentLeft());
      assertTrue(res.get().getLeft().isPresentNumericLiteral());
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
    assertTrue(Log.getFindings().isEmpty());
  }
  
}
