/* (c) https://github.com/MontiCore/monticore */

package mc.feature.expression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import org.junit.BeforeClass;
import org.junit.Test;

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
  
  @BeforeClass
  public static void disableFailQuick() {
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
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTAddExpr);
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testLiteral() {
    try {
      Optional<ASTExpr> res = parse("1");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTPrimaryExpr);

      assertEquals("1", ((ASTPrimaryExpr) ast).getNumericLiteral());
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testStar() {
    try {
      Optional<ASTExpr> res = parse("1*2");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTMultExpr);
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testBracket() {
    try {
      Optional<ASTExpr> res = parse("(1*2)");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTBracketExpr);
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testExpr1() {
    try {
      Optional<ASTExpr> res = parse("1*2+3");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTAddExpr);
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testExpr2() {
    try {
      Optional<ASTExpr> res = parse("1+2*3");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTAddExpr);
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testExpr3() {
    try {
      Optional<ASTExpr> res = parse("1-2-3");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTAddExpr);
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }

  @Test
  public void testPowerWithRightAssoc() {
    try {
      Optional<ASTExpr> res = parse("2^3^4");
      assertTrue(res.isPresent());
      assertTrue(res.get()instanceof ASTPowerExpr);
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
}
