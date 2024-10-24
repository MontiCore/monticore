/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class LambdaExpressionsTypeVisitorTest extends AbstractTypeVisitorTest {

  @BeforeEach
  public void setup() {
    setupValues();
  }

  @Test
  public void deriveFromLambdaExpressionNoParameterTest() throws IOException {
    // example with int
    checkExpr("() -> 5", "() -> int");
    // example with lambda nesting
    checkExpr("() -> () -> 5", "() -> () -> int");
  }

  @Test
  public void deriveFromLambdaExpressionOneParameterTest() throws IOException {
    // example with int, long
    checkExpr("(int x) -> 5L", "int -> long");
    // example with input equaling output
    checkExpr("(int x) -> x", "int -> int");
    // example with lambda nesting
    checkExpr("(int x) -> (int y) -> x + y", "int -> int -> int");
  }

  @Test
  public void deriveFromLambdaExpressionMultipleParameterTest() throws IOException {
    // example with int, long, int
    checkExpr("(int x, long y) -> 5", "(int, long) -> int");
    // example with lambda nesting
    checkExpr("(int x, long y) -> () -> 5", "(int, long) -> () -> int");
    // example with int, long, expression
    checkExpr("(int x, long y) -> x + y", "(int, long) -> long");
  }

}
