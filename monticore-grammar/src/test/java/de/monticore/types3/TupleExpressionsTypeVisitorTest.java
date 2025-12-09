/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TupleExpressionsTypeVisitorTest extends AbstractTypeVisitorTest {

  @Test
  public void deriveFromTupleExpressionTest() throws IOException {
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
  public void bracketExpressionIsNotATupleTest() throws IOException {
    checkExpr("(1)", "int");
    checkExpr("((1,1))", "(int, int)");
  }

}

