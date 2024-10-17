/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class MCArrayTypesTypeVisitorTest
    extends AbstractTypeVisitorTest {

  @Test
  public void symTypeFromAST_ArrayTest() throws IOException {
    checkTypeRoundTrip("int[][]");
  }

  @Test
  public void symTypeFromAST_ArrayTest2() throws IOException {
    checkTypeRoundTrip("Person[]");
  }

  @Test
  public void symTypeFromAST_ArrayTest3() throws IOException {
    checkErrorMCType("notAType[]", "0xE9CDC");
  }

}
