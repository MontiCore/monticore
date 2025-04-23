/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.stereotypes;

import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonElementFactory;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class StereoValueTypeDeSerTest {
  
  @BeforeEach
  void setup() {
    LogStub.init();
    JsonElementFactory.setInstance(new JsonElementFactory());
  }
  
  @ParameterizedTest
  @MethodSource("serializationTestCaseProvider")
  void shouldSerialize(StereoValueType type, String expectedSerialization) {
    // When
    JsonElement serialized = StereoValueTypeDeSer.serializeStereoValueType(type);
    
    // Then
    assertAll(
      () -> assertTrue(serialized.isJsonString()),
      () -> assertEquals(0, Log.getFindingsCount())
    );
    assertEquals(expectedSerialization, serialized.getAsJsonString().getValue());
  }
  
  protected static Stream<Arguments> serializationTestCaseProvider() {
    return Stream.of(
      arguments(StereoValueType.NONE, "none"),
      arguments(StereoValueType.OBJECT, "object"),
      arguments(StereoValueType.BOOLEAN, "boolean"),
      arguments(StereoValueType.INT, "int"),
      arguments(StereoValueType.LONG, "long"),
      arguments(StereoValueType.FLOAT, "float"),
      arguments(StereoValueType.DOUBLE, "double"),
      arguments(StereoValueType.CHAR, "char"),
      arguments(StereoValueType.STRING, "String")
    );
  }

  @ParameterizedTest
  @MethodSource("deserializationTestCaseProvider")
  void shouldDeserialize(String serializedForm, StereoValueType expectedType) {
    // Given
    JsonElement asJson = JsonElementFactory.createJsonString(serializedForm);

    // When
    StereoValueType stereoValType = StereoValueTypeDeSer.deserializeStereoValueType(asJson);

    // Then
    assertAll(
      () -> assertEquals(expectedType, stereoValType),
      () -> assertEquals(0, Log.getFindingsCount())
    );
  }

  protected static Stream<Arguments> deserializationTestCaseProvider() {
    return Stream.of(
      arguments("none", StereoValueType.NONE),
      arguments("object", StereoValueType.OBJECT),
      arguments("boolean", StereoValueType.BOOLEAN),
      arguments("int", StereoValueType.INT),
      arguments("long", StereoValueType.LONG),
      arguments("float", StereoValueType.FLOAT),
      arguments("double", StereoValueType.DOUBLE),
      arguments("char", StereoValueType.CHAR),
      arguments("String", StereoValueType.STRING)
    );
  }
}
