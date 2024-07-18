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
import mc.feature.expression.expression3._ast.ASTExpr;
import mc.feature.expression.expression5._ast.ASTMultExpr;
import mc.feature.expression.expression5._parser.Expression5Parser;

public class Expression5Test extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
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
  public void testExpr2() {
    try {
      Optional<ASTExpr> res = parse("1+2*3");
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
  public void testExpr3() {
    try {
      Optional<ASTExpr> res = parse("1*2*3");
      Assertions.assertTrue(res.isPresent());
      ASTExpr ast = res.get();
      Assertions.assertTrue(ast instanceof ASTMultExpr);
    }
    catch (Exception e) {
      Assertions.fail(e.getMessage());
    }
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  
}
