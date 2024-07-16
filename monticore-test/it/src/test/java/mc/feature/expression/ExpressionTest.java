/* (c) https://github.com/MontiCore/monticore */

package mc.feature.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.expression.expression._ast.ASTConstantsExpression;
import mc.feature.expression.expression._ast.ASTExpr;
import mc.feature.expression.expression._parser.ExpressionParser;
import org.junit.jupiter.api.Test;

public class ExpressionTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
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
      Assertions.assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      Assertions.assertEquals(ASTConstantsExpression.PLUS, ast.getOp());
    }
    catch (Exception e) {
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
      Assertions.assertTrue(ast.isPresentNumericLiteral());
      Assertions.assertEquals("1", ast.getNumericLiteral());
    }
    catch (Exception e) {
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
      Assertions.assertEquals(ASTConstantsExpression.STAR, ast.getOp());
    }
    catch (Exception e) {
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
      Assertions.assertTrue(ast.isPresentExpr());
    }
    catch (Exception e) {
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
      Assertions.assertEquals(ASTConstantsExpression.PLUS, ast.getOp());
    }
    catch (Exception e) {
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
      Assertions.assertEquals(ASTConstantsExpression.PLUS, ast.getOp());
    }
    catch (Exception e) {
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
      Assertions.assertEquals(ASTConstantsExpression.MINUS, ast.getOp());
    }
    catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testPowerWithRightAssoc() {
    try {
      Optional<ASTExpr> res = parse("2^3^4");
      Assertions.assertTrue(res.isPresent());
      Assertions.assertTrue(res.get().isPresentLeft());
      Assertions.assertTrue(res.get().getLeft().isPresentNumericLiteral());
    }
    catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
