/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class MCFunctionTypesTypeVisitorTest
    extends AbstractTypeVisitorTest {

  @Test
  public void symTypeFromAST_TestRunnable() throws IOException {
    checkTypeRoundTrip("() -> void");
  }

  @Test
  public void symTypeFromAST_TestSimpleFunction1() throws IOException {
    checkType("(int) -> int", "int -> int");
  }

  @Test
  public void symTypeFromAST_TestSimpleFunction2() throws IOException {
    checkTypeRoundTrip("(long, int) -> int");
  }

  @Test
  public void symTypeFromAST_TestSimpleFunction3() throws IOException {
    checkType("int -> int", "int -> int");
  }

  @Test
  public void symTypeFromAST_TestEllipticFunction1() throws IOException {
    checkTypeRoundTrip("(int...) -> int");
  }

  @Test
  public void symTypeFromAST_TestEllipticFunction2() throws IOException {
    checkTypeRoundTrip("(long, int...) -> void");
  }

  @Test
  public void symTypeFromAST_TestHigherOrderFunction1() throws IOException {
    checkTypeRoundTrip("(int -> void) -> () -> int");
  }

  @Test
  public void symTypeFromAST_TestHigherOrderEllipticFunction() throws IOException {
    checkTypeRoundTrip("int -> ((() -> (int, long...) -> int)...) -> void");
  }
}
