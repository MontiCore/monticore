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
        arguments("Event<1, 1.2f>", "EventStream<float>"),
        arguments("Sync<1, 1.2f>", "SyncStream<float>"),
        arguments("Topt<1, 1.2f>", "ToptStream<float>"),
        arguments("Untimed<1, 1.2f>", "UntimedStream<float>"),
        // Tick
        arguments("<1,Tick,Tick,1.2f>", "EventStream<float>"),
        // Abs
        arguments("Topt<1,~,~,1.2f>", "ToptStream<float>"),
        // type argument
        arguments("<int><>", "EventStream<int>"),
        arguments("<int><1>", "EventStream<int>"),
        arguments("Event<int><1>", "EventStream<int>"),
        arguments("Event<float><1>", "EventStream<float>")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void streamConstructorInvalidTest(
      String exprStr,
      String expectedErrorStr
  ) throws IOException {
    checkErrorExpr(exprStr, expectedErrorStr);
  }

  public static Stream<Arguments> streamConstructorInvalidTest() {
    return Stream.of(
        // Empty
        arguments("<>", "0xFD577"),
        arguments("Sync<>", "0xFD577"),
        // invalid expression type
        arguments("<int><1.2f>", "0xFD578"),
        arguments("Sync<boolean><1,1.2f>", "0xFD578")
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
        arguments("1:<1>", "EventStream<int>"),
        arguments("1:Event<1>", "EventStream<int>"),
        arguments("1.2f:<1>", "EventStream<float>"),
        arguments("1.2f:Event<1>", "EventStream<float>"),
        arguments("1.2f:Sync<1>", "SyncStream<float>"),
        arguments("1.2f:Topt<1>", "ToptStream<float>"),
        arguments("1.2f:Untimed<1>", "UntimedStream<float>"),
        // longer chains
        arguments("1:2:3:4:5:Event<6>", "EventStream<int>"),
        arguments("1:2:3.5f:4:5:Event<6>", "EventStream<float>"),
        // Tick
        arguments("Tick:<6>", "EventStream<int>"),
        arguments("Tick:Tick:2:<6>", "EventStream<int>"),
        // Abs
        arguments("Abs:Topt<6>", "ToptStream<int>"),
        arguments("Abs:Abs:2:Topt<6>", "ToptStream<int>")
    );
  }

  @ParameterizedTest
  @MethodSource
  public void appendStreamInvalidTest(
      String exprStr,
      String expectedErrorStr
  ) throws IOException {
    checkErrorExpr(exprStr, expectedErrorStr);
  }

  public static Stream<Arguments> appendStreamInvalidTest() {
    return Stream.of(
        arguments("Abs:1:Tick:1:<1>", "0xFD573"),
        arguments("Tick:1:Abs:1:Topt<1>", "0xFD574"),
        arguments("Tick:Topt<~,1>", "0xFD574"),
        arguments("Abs:<Tick,1>", "0xFD573")
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
        arguments("<1>^^<1>", "EventStream<int>"),
        arguments("<1>^^<1.2f>", "EventStream<float>"),
        arguments("Event<1>^^Event<1.2f>", "EventStream<float>"),
        arguments("Sync<1>^^Sync<1.2f>", "SyncStream<float>"),
        arguments("Topt<1>^^Topt<1.2f>", "ToptStream<float>"),
        arguments("Untimed<1>^^Untimed<1.2f>", "UntimedStream<float>"),
        // longer chains
        arguments("Event<6>^^Event<2>^^Event<1.2f>", "EventStream<float>")
    );
  }

}
