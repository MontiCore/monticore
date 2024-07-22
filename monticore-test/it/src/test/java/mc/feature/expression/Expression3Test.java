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
import org.junit.jupiter.api.Test;

import de.se_rwth.commons.logging.Log;
import mc.GeneratorIntegrationsTest;
import mc.feature.expression.expression3._ast.ASTAddExpr;
import mc.feature.expression.expression3._ast.ASTBracketExpr;
import mc.feature.expression.expression3._ast.ASTExpr;
import mc.feature.expression.expression3._ast.ASTMultExpr;
import mc.feature.expression.expression3._ast.ASTPowerExpr;
import mc.feature.expression.expression3._ast.ASTPrimaryExpr;
import mc.feature.expression.expression3._parser.Expression3Parser;

public class Expression3Test extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  public Optional<ASTExpr> parse(String input) throws IOException {
    Expression3Parser parser = new Expression3Parser();
    Optional<ASTExpr> res = parser.parseExpr(new StringReader(input));
    return res;
  }
  
  @Test
  public void testPlus() {
    try {
      Optional<ASTExpr> res = parse("1+2");
      Assertions.assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      Assertions.assertTrue(ast instanceof ASTAddExpr);
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
      Assertions.assertTrue(ast instanceof ASTPrimaryExpr);

      Assertions.assertEquals("1", ((ASTPrimaryExpr) ast).getNumericLiteral());
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
      Assertions.assertTrue(ast instanceof ASTMultExpr);
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
      Assertions.assertTrue(ast instanceof ASTBracketExpr);
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
      Assertions.assertTrue(ast instanceof ASTAddExpr);
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
      Assertions.assertTrue(ast instanceof ASTAddExpr);
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
      Assertions.assertTrue(ast instanceof ASTAddExpr);
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
      Assertions.assertTrue(res.get()instanceof ASTPowerExpr);
    }
    catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
}
