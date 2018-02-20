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
import mc.feature.expression.expression3._ast.ASTBracketExpr;
import mc.feature.expression.expression3._ast.ASTExpr;
import mc.feature.expression.expression3._ast.ASTPowerExpr;
import mc.feature.expression.expression3._ast.ASTPrimaryExpr;
import mc.feature.expression.expression5._ast.ASTAddExpr;
import mc.feature.expression.expression5._ast.ASTMultExpr;
import mc.feature.expression.expression5._parser.Expression5Parser;

public class Expression5Test extends GeneratorIntegrationsTest {
  
  @BeforeClass
  public static void disableFailQuick() {
    Log.enableFailQuick(false);
  }
  
  public Optional<ASTExpr> parse(String input) throws IOException {
    Expression5Parser parser = new Expression5Parser();
    Optional<ASTExpr> res = parser.parseExpr(new StringReader(input));
    return res;
  }
  

  @Test
  public void testExpr1() {
    try {
      Optional<ASTExpr> res = parse("1*2+3");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTMultExpr);
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
      assertTrue(ast instanceof ASTMultExpr);
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }
  
  @Test
  public void testExpr3() {
    try {
      Optional<ASTExpr> res = parse("1*2*3");
      assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      assertTrue(ast instanceof ASTMultExpr);
    }
    catch (Exception e) {
      fail(e.getMessage());
    }
  }

  
}
