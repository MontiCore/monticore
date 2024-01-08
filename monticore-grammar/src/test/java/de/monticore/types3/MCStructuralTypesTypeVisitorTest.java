/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import org.junit.Test;

import java.io.IOException;

public class MCStructuralTypesTypeVisitorTest
    extends AbstractTypeVisitorTest {

  @Test
  public void symTypeFromAST_TestSimpleUnion() throws IOException {
    checkTypeRoundTrip("(Person | int)");
    checkType("Person | int", "(Person | int)");
    checkTypeRoundTrip("(Person | Student | float | int)");
    checkTypeRoundTrip("(Person | Student | char | double | float | int)");
  }

  @Test
  public void symTypeFromAST_TestNestedUnion() throws IOException {
    checkTypeRoundTrip("((Person | Student) | (float | int))");
    checkTypeRoundTrip("((Person | Student) | float | int)");
  }

  @Test
  public void symTypeFromAST_TestSimpleIntersection() throws IOException {
    checkTypeRoundTrip("(Person & int)");
    checkType("Person & int", "(Person & int)");
    checkTypeRoundTrip("(Person & Student & float & int)");
    checkTypeRoundTrip("(Person & Student & char & double & float & int)");
  }

  @Test
  public void symTypeFromAST_TestNestedIntersection() throws IOException {
    checkTypeRoundTrip("((Person & Student) & (float & int))");
    checkTypeRoundTrip("((Person & Student) & float & int)");
  }

  @Test
  public void symTypeFromAST_TestUnionAndIntersection() throws IOException {
    checkTypeRoundTrip("((Person | float) & int)");
    checkTypeRoundTrip("((Person & float) | int)");
  }

  @Test
  public void symTypeFromAST_TestUnionAndIntersectionMissingParenthesis1() throws IOException {
    checkErrorMCType("Person | float & int", "0xFD770");
  }

  @Test
  public void symTypeFromAST_TestUnionAndIntersectionMissingParenthesis2() throws IOException {
    checkErrorMCType("Person & float | int", "0xFD771");
  }

}
