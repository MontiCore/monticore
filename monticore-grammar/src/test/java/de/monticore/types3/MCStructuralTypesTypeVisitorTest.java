/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import org.junit.jupiter.api.Test;

import java.io.IOException;

public class MCStructuralTypesTypeVisitorTest
    extends AbstractTypeVisitorTest {

  @Test
  public void symTypeFromAST_TestSimpleUnion() throws IOException {
    checkTypeRoundTrip("Person | int");
    checkType("(Person | int)", "Person | int");
    checkTypeRoundTrip("Person | Student | float | int");
    checkTypeRoundTrip("Person | Student | char | double | float | int");
  }

  @Test
  public void symTypeFromAST_TestNestedUnion() throws IOException {
    checkTypeRoundTrip("(Person | Student) | (float | int)");
    checkTypeRoundTrip("(Person | Student) | float | int");
  }

  @Test
  public void symTypeFromAST_TestSimpleIntersection() throws IOException {
    checkTypeRoundTrip("Person & int");
    checkType("(Person & int)", "Person & int");
    checkTypeRoundTrip("Person & Student & float & int");
    checkTypeRoundTrip("Person & Student & char & double & float & int");
  }

  @Test
  public void symTypeFromAST_TestNestedIntersection() throws IOException {
    checkTypeRoundTrip("(Person & Student) & (float & int)");
    checkTypeRoundTrip("(Person & Student) & float & int");
  }

  @Test
  public void symTypeFromAST_TestUnionAndIntersection() throws IOException {
    checkTypeRoundTrip("(Person | float) & int");
    checkType("Person | float & int", "(float & int) | Person");
    checkTypeRoundTrip("(Person & float) | int");
    checkType("Person & float | int", "(Person & float) | int");
  }

  @Test
  public void symTypeFromAST_TestUnionAndIntersection2() throws IOException {
    checkType("int | float | char | byte", "byte | char | float | int");
    checkType("int | float | char & byte", "(byte & char) | float | int");
    checkType("int | float & char | byte", "(char & float) | byte | int");
    checkType("int | float & char & byte", "(byte & char & float) | int");
    checkType("int & float | char | byte", "(float & int) | byte | char");
    checkType("int & float | char & byte", "(byte & char) | (float & int)");
    checkType("int & float & char | byte", "(char & float & int) | byte");
    checkType("int & float & char & byte", "byte & char & float & int");
  }

  @Test
  public void symTypeFromAST_TestTupleAndFunction() throws IOException {
    // `(a,b)` in front of `->` is not to be interpreted as a tuple
    // currently (01.2024) they require parenthesis
    checkTypeRoundTrip("(int, int) -> void");
    checkTypeRoundTrip("((int, int)) -> void");
  }

  @Test
  public void symTypeFromAST_TestUnionAndFunction() throws IOException {
    checkType("float | int -> byte | char", "float | int -> byte | char");
    checkType("(float) | int -> byte | char", "float | int -> byte | char");
    checkType("(float | int) -> byte | char", "float | int -> byte | char");
    checkType("(float | int -> byte) | char", "(float | int -> byte) | char");
    checkType("(float | int -> byte | char)", "float | int -> byte | char");
    checkType("float | (int) -> byte | char", "float | int -> byte | char");
    checkType("float | (int -> byte) | char", "(int -> byte) | char | float");
    checkType("float | (int -> byte | char)", "(int -> byte | char) | float");
    checkType("float | int -> (byte) | char", "float | int -> byte | char");
    checkType("float | int -> (byte | char)", "float | int -> byte | char");
    checkType("float | int -> byte | (char)", "float | int -> byte | char");
  }

}
