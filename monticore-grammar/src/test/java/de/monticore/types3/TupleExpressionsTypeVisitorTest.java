/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TupleExpressionsTypeVisitorTest extends AbstractTypeVisitorTest {

  @Test
  public void deriveFromTupleExpressionTest() {
    // simple
    checkExpr("(1,1)", "(int, int)");
    checkExpr("(1, 1)", "(int, int)");
    checkExpr("(\"1\", 1)", "(R\"1\", int)");
    checkExpr("(1, \"1\")", "(int, R\"1\")");
    checkExpr("(\"1\", 1, 1.0f, 1.0)", "(R\"1\", int, float, double)");

    // complex
    checkExpr("((1, 1), 1)", "((int, int), int)");
    checkExpr("(1, (1, (1, 1)))", "(int, (int, (int, int)))");
    checkExpr("((1, 1), (1, 1))", "((int, int), (int, int))");
  }

  @Test
  public void deriveFromTupleExpressionCTTITest() {
    checkExpr("([], [1])", "(List<int>, List<float>)", "(List<int>, List<float>)");
    checkExpr("(([], [1]), 1)", "((List<int>, List<float>), int)", "((List<int>, List<float>), int)");
  }

  @Test
  public void bracketExpressionIsNotATupleTest() {
    checkExpr("(1)", "int");
    checkExpr("((1,1))", "(int, int)");
  }

}

