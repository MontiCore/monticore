/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.types3.util.DefsVariablesForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

public class JavaClassExpressionsTypeVisitorTest
    extends AbstractTypeVisitorTest {

  @BeforeEach
  public void initVars() {
    DefsVariablesForTests.setup();
  }

  @ParameterizedTest
  @MethodSource
  public void deriveFromClassExpression(
      String expr, String expectedType
  ) throws IOException {
    checkExpr(expr, expectedType);
    assertNoFindings();
  }

  public static Stream<Arguments> deriveFromClassExpression() {
    return Stream.of(
        Arguments.of("Person.class", "java.lang.Class<Person>"),
        Arguments.of("java.lang.Integer.class", "java.lang.Class<java.lang.Integer>"),
        Arguments.of("int.class", "java.lang.Class<java.lang.Integer>"),
        Arguments.of("boolean.class", "java.lang.Class<java.lang.Boolean>"),
        Arguments.of("void.class", "java.lang.Class<java.lang.Void>")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void deriveFromInvalidClassExpression(
      String expr, String expectedError
  ) throws IOException {
    checkErrorExpr(expr, expectedError);
  }

  public static Stream<Arguments> deriveFromInvalidClassExpression() {
    return Stream.of(
        Arguments.of("Person[].class", "0xFD576"),
        Arguments.of("(Student|Teacher).class", "0xFD576"),
        Arguments.of("(Car&Person).class", "0xFD576"),
        Arguments.of("[km/h].class", "0xFD576"),
        Arguments.of("[km/h]<int>.class", "0xFD576"),
        Arguments.of("(int -> int).class", "0xFD576")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void deriveFromInstanceofExpression(
      String expr, String expectedType
  ) throws IOException {
    checkExpr(expr, expectedType);
    assertNoFindings();
  }

  public static Stream<Arguments> deriveFromInstanceofExpression() {
    return Stream.of(
        Arguments.of("varPerson instanceof Student student", "boolean"),
        Arguments.of("varPerson instanceof (Student|Teacher) studentOrTeacher", "boolean"),
        // useless, always true:
        Arguments.of("varPerson instanceof Person person", "boolean"),
        Arguments.of("varStudent instanceof Person person", "boolean")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void deriveFromInvalidInstanceofExpression(
      String expr, String expectedError
  ) throws IOException {
    checkErrorExpr(expr, expectedError);
  }

  public static Stream<Arguments> deriveFromInvalidInstanceofExpression() {
    return Stream.of(
        Arguments.of("varCar instanceof Person person", "0xFD571"),
        Arguments.of("varintSecond instanceof Person person", "0xFD571")
    );
  }

}
