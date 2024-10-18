/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class MCFullGenericTypesTypeVisitorTest
    extends AbstractTypeVisitorTest {

  @Test
  public void symTypeFromAST_TestFullGeneric() throws IOException {
    checkTypeRoundTrip("List<? extends Person>");
  }

}
