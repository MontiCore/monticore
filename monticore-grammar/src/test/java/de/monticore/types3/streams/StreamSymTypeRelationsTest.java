// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.streams;

import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamSymTypeRelationsTest extends AbstractMCTest {

  @BeforeEach
  public void init() {
    BasicSymbolsMill.reset();
    BasicSymbolsMill.init();
    StreamSymTypeRelations.init();
    BasicSymbolsMill.initializePrimitives();
    BasicSymbolsMill.initializeStreams();
  }

  @Test
  public void recognizeStreamType() {
    // Given
    SymTypeExpression intSymTypeExpression = SymTypeExpressionFactory.createPrimitive("int");
    SymTypeOfGenerics intStream = StreamSymTypeFactory.createStream(intSymTypeExpression);

    // When & Then
    assertTrue(StreamSymTypeRelations.isStream(intStream));
    assertFalse(StreamSymTypeRelations.isEventStream(intStream));
    assertTrue(StreamSymTypeRelations.isStreamOfUnknownSubType(intStream));
    assertFalse(StreamSymTypeRelations.isSyncStream(intStream));
    assertFalse(StreamSymTypeRelations.isToptStream(intStream));
    assertFalse(StreamSymTypeRelations.isUntimedStream(intStream));
    assertEquals(intSymTypeExpression, StreamSymTypeRelations.getStreamElementType(intStream));
  }

  @Test
  public void recognizeEventStreamType() {
    // Given
    SymTypeExpression intSymTypeExpression = SymTypeExpressionFactory.createPrimitive("int");
    SymTypeOfGenerics intStream = StreamSymTypeFactory.createEventStream(intSymTypeExpression);

    // When & Then
    assertTrue(StreamSymTypeRelations.isStream(intStream));
    assertTrue(StreamSymTypeRelations.isEventStream(intStream));
    assertFalse(StreamSymTypeRelations.isStreamOfUnknownSubType(intStream));
    assertFalse(StreamSymTypeRelations.isSyncStream(intStream));
    assertFalse(StreamSymTypeRelations.isToptStream(intStream));
    assertFalse(StreamSymTypeRelations.isUntimedStream(intStream));
    assertEquals(intSymTypeExpression, StreamSymTypeRelations.getStreamElementType(intStream));
  }

  @Test
  public void recognizeSyncStreamType() {
    // Given
    SymTypeExpression intSymTypeExpression = SymTypeExpressionFactory.createPrimitive("int");
    SymTypeOfGenerics intStream = StreamSymTypeFactory.createSyncStream(intSymTypeExpression);

    // When & Then
    assertTrue(StreamSymTypeRelations.isStream(intStream));
    assertFalse(StreamSymTypeRelations.isEventStream(intStream));
    assertFalse(StreamSymTypeRelations.isStreamOfUnknownSubType(intStream));
    assertTrue(StreamSymTypeRelations.isSyncStream(intStream));
    assertFalse(StreamSymTypeRelations.isToptStream(intStream));
    assertFalse(StreamSymTypeRelations.isUntimedStream(intStream));
    assertEquals(intSymTypeExpression, StreamSymTypeRelations.getStreamElementType(intStream));
  }

  @Test
  public void recognizeToptStreamType() {
    // Given
    SymTypeExpression intSymTypeExpression = SymTypeExpressionFactory.createPrimitive("int");
    SymTypeOfGenerics intStream = StreamSymTypeFactory.createToptStream(intSymTypeExpression);

    // When & Then
    assertTrue(StreamSymTypeRelations.isStream(intStream));
    assertFalse(StreamSymTypeRelations.isEventStream(intStream));
    assertFalse(StreamSymTypeRelations.isStreamOfUnknownSubType(intStream));
    assertFalse(StreamSymTypeRelations.isSyncStream(intStream));
    assertTrue(StreamSymTypeRelations.isToptStream(intStream));
    assertFalse(StreamSymTypeRelations.isUntimedStream(intStream));
    assertEquals(intSymTypeExpression, StreamSymTypeRelations.getStreamElementType(intStream));
  }

  @Test
  public void recognizeUntimedStreamType() {
    // Given
    SymTypeExpression intSymTypeExpression = SymTypeExpressionFactory.createPrimitive("int");
    SymTypeOfGenerics intStream = StreamSymTypeFactory.createUntimedStream(intSymTypeExpression);

    // When & Then
    assertTrue(StreamSymTypeRelations.isStream(intStream));
    assertFalse(StreamSymTypeRelations.isEventStream(intStream));
    assertFalse(StreamSymTypeRelations.isStreamOfUnknownSubType(intStream));
    assertFalse(StreamSymTypeRelations.isSyncStream(intStream));
    assertFalse(StreamSymTypeRelations.isToptStream(intStream));
    assertTrue(StreamSymTypeRelations.isUntimedStream(intStream));
    assertEquals(intSymTypeExpression, StreamSymTypeRelations.getStreamElementType(intStream));
  }
}
