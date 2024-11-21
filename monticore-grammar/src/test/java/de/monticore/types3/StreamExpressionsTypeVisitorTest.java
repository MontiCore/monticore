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


}
