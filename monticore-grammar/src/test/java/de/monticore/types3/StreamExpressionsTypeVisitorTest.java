/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.types3.streams.StreamSymTypeRelations;
import de.monticore.types3.util.DefsVariablesForTests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

public class StreamExpressionsTypeVisitorTest extends AbstractTypeVisitorTest {

  // Note: for unspecified stream types, only the constructor is changed.
  // In the cases of tests with only <>, it would be correct
  // to replace the resulting Stream with EventStream.
  // This is not the case as this is not needed as of now.

  @BeforeEach
  public void setup() {
    StreamSymTypeRelations.init();
    DefsVariablesForTests.setup();
  }

  @ParameterizedTest
  @MethodSource
  public void streamConstructorTest(
      String exprStr,
      String expectedTypeStr
  ) throws IOException {
    checkExpr(exprStr, expectedTypeStr);
  }

  public static Stream<Arguments> streamConstructorTest() {
    return Stream.of(
        arguments("<1>", "EventStream<int>"),
        arguments("Event<1>", "EventStream<int>"),
        arguments("<1, 1.2f>", "EventStream<float>"),
        arguments("Event<1, 1.2f>", "EventStream<float>"),
        arguments("Sync<1, 1.2f>", "SyncStream<float>"),
        arguments("Topt<1, 1.2f>", "ToptStream<float>"),
        arguments("Untimed<1, 1.2f>", "UntimedStream<float>")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void streamConstructorCTTITest(
      String exprStr,
      String targetTypeStr,
      String expectedTypeStr
  ) throws IOException {
    checkExpr(exprStr, targetTypeStr, expectedTypeStr);
  }

  public static Stream<Arguments> streamConstructorCTTITest() {
    return Stream.of(
        arguments("<>", "Stream<int>", "EventStream<int>"),
        arguments("<>", "EventStream<int>", "EventStream<int>"),
        arguments("<>", "SyncStream<int>", "SyncStream<int>"),
        arguments("<>", "ToptStream<int>", "ToptStream<int>"),
        arguments("<>", "UntimedStream<int>", "UntimedStream<int>"),
        arguments("<1>", "Stream<int>", "EventStream<int>"),
        arguments("<1>", "EventStream<int>", "EventStream<int>"),
        arguments("<1>", "SyncStream<int>", "SyncStream<int>"),
        arguments("<1>", "ToptStream<int>", "ToptStream<int>"),
        arguments("<1>", "UntimedStream<int>", "UntimedStream<int>")
    );
  }
  @ParameterizedTest
  @MethodSource
  public void streamConstructorInvalidCTTITest(
      String exprStr,
      String targetTypeStr,
      String expectedErrorStr
  ) throws IOException {
    checkErrorExpr(exprStr, targetTypeStr, expectedErrorStr);
  }

  public static Stream<Arguments> streamConstructorInvalidCTTITest() {
    return Stream.of(
        arguments("Sync<1.2f>", "SyncStream<int>", "0xFD451"),
        arguments("Sync<>", "EventStream<int>", "0xFD451")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void appendStreamTest(
      String exprStr,
      String expectedTypeStr
  ) throws IOException {
    checkExpr(exprStr, expectedTypeStr);
  }

  public static Stream<Arguments> appendStreamTest() {
    return Stream.of(
        arguments("1:<1>", "Stream<int>"),
        arguments("1:Event<1>", "EventStream<int>"),
        arguments("1.2f:<1>", "Stream<float>"),
        arguments("1.2f:Event<1>", "EventStream<float>"),
        arguments("1.2f:Sync<1>", "SyncStream<float>"),
        arguments("1.2f:Topt<1>", "ToptStream<float>"),
        arguments("1.2f:Untimed<1>", "UntimedStream<float>"),
        // longer chains
        arguments("1:2:3:4:5:Event<6>", "EventStream<int>"),
        arguments("1:2:3.5f:4:5:Event<6>", "EventStream<float>")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void appendStreamCTTITest(
      String exprStr,
      String targetTypeStr,
      String expectedTypeStr
  ) throws IOException {
    checkExpr(exprStr, targetTypeStr, expectedTypeStr);
  }

  public static Stream<Arguments> appendStreamCTTITest() {
    return Stream.of(
        arguments("1:2:<>", "Stream<int>", "Stream<int>"),
        arguments("1:2:<>", "Stream<float>", "Stream<float>"),
        arguments("1:2:<>", "EventStream<float>", "EventStream<float>"),
        arguments("1:2:<>", "SyncStream<float>", "SyncStream<float>"),
        arguments("1:2:<>", "ToptStream<float>", "ToptStream<float>"),
        arguments("1:2:<>", "UntimedStream<float>", "UntimedStream<float>")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void appendStreamInvalidCTTITest(
      String exprStr,
      String targetTypeStr,
      String expectedErrorStr
  ) throws IOException {
    checkErrorExpr(exprStr, targetTypeStr, expectedErrorStr);
  }

  public static Stream<Arguments> appendStreamInvalidCTTITest() {
    return Stream.of(
        arguments("1:Sync<1.2f>", "SyncStream<int>", "0xFD447"),
        arguments("1.2f:Sync<1>", "SyncStream<int>", "0xFD451"),
        arguments("1:Sync<1>", "EventStream<int>", "0xFD447")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void concatStreamTest(
      String exprStr,
      String expectedTypeStr
  ) throws IOException {
    checkExpr(exprStr, expectedTypeStr);
  }

  public static Stream<Arguments> concatStreamTest() {
    return Stream.of(
        arguments("<1>^^<1>", "Stream<int>"),
        arguments("<>^^<1>", "Stream<int>"),
        arguments("<1>^^<>", "Stream<int>"),
        arguments("<1>^^<1.2f>", "Stream<float>"),
        arguments("Event<1>^^Event<1.2f>", "EventStream<float>"),
        arguments("Sync<1>^^Sync<1.2f>", "SyncStream<float>"),
        arguments("Topt<1>^^Topt<1.2f>", "ToptStream<float>"),
        arguments("Untimed<1>^^Untimed<1.2f>", "UntimedStream<float>"),
        // longer chains
        arguments("Event<6>^^Event<2>^^Event<1.2f>", "EventStream<float>")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void concatStreamCTTITest(
      String exprStr,
      String targetTypeStr,
      String expectedTypeStr
  ) throws IOException {
    checkExpr(exprStr, targetTypeStr, expectedTypeStr);
  }

  public static Stream<Arguments> concatStreamCTTITest() {
    return Stream.of(
        arguments("<1>^^<1>", "EventStream<int>", "EventStream<int>"),
        arguments("<>^^<1>", "EventStream<int>","EventStream<int>"),
        arguments("Event<1>^^<>", "EventStream<int>","EventStream<int>"),
        arguments("<1>^^<1.2f>", "EventStream<float>","EventStream<float>")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void concatStreamInvalidCTTITest(
      String exprStr,
      String targetTypeStr,
      String expectedErrorStr
  ) throws IOException {
    checkErrorExpr(exprStr, targetTypeStr, expectedErrorStr);
  }

  public static Stream<Arguments> concatStreamInvalidCTTITest() {
    return Stream.of(
        arguments("<1>^^<1.2f>", "SyncStream<int>", "0xFD447"),
        arguments("<1.2f>^^<1>", "SyncStream<int>", "0xFD447"),
        arguments("Sync<>^^<>", "EventStream<int>", "0xFD447")
    );
  }

}
