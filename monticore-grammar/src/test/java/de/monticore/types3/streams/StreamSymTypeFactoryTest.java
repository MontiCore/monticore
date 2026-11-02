// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.streams;

import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamSymTypeFactoryTest extends AbstractMCTest {


  @BeforeEach
  public void setup() {
    BasicSymbolsMill.reset();
    BasicSymbolsMill.init();
    StreamSymTypeRelations.init();
    BasicSymbolsMill.initializePrimitives();
    BasicSymbolsMill.initializeStreams();
  }

  @Test
  public void createsStream() {
    // Given
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    SymTypeExpression intSymTypeExpression = SymTypeExpressionFactory.createPrimitive("int");

    // When
    SymTypeOfGenerics intStream = StreamSymTypeFactory.createStream(intSymTypeExpression);

    // Then
    assertTrue(intStream.hasTypeInfo());
    assertSame(gs.resolveType("Stream").orElseThrow(), intStream.getTypeInfo());
    assertEquals("Stream.Stream", intStream.getTypeConstructorFullName());
    assertEquals(1, intStream.sizeArguments());
    assertTrue(intSymTypeExpression.deepEquals(intStream.getArgument(0)));
  }

  @Test
  public void createsEventStream() {
    // Given
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    SymTypeExpression intSymTypeExpression = SymTypeExpressionFactory.createPrimitive("int");

    // When
    SymTypeOfGenerics intStream = StreamSymTypeFactory.createEventStream(intSymTypeExpression);

    // Then
    assertTrue(intStream.hasTypeInfo());
    assertSame(gs.resolveType("EventStream").orElseThrow(), intStream.getTypeInfo());
    assertEquals("EventStream.EventStream", intStream.getTypeConstructorFullName());
    assertEquals(1, intStream.sizeArguments());
    assertTrue(intSymTypeExpression.deepEquals(intStream.getArgument(0)));
  }

  @Test
  public void createsUntimedStream() {
    // Given
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    SymTypeExpression intSymTypeExpression = SymTypeExpressionFactory.createPrimitive("int");

    // When
    SymTypeOfGenerics intStream = StreamSymTypeFactory.createUntimedStream(intSymTypeExpression);

    // Then
    assertTrue(intStream.hasTypeInfo());
    assertSame(gs.resolveType("UntimedStream").orElseThrow(), intStream.getTypeInfo());
    assertEquals("UntimedStream.UntimedStream", intStream.getTypeConstructorFullName());
    assertEquals(1, intStream.sizeArguments());
    assertTrue(intSymTypeExpression.deepEquals(intStream.getArgument(0)));
  }

  @Test
  public void createsToptStream() {
    // Given
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    SymTypeExpression intSymTypeExpression = SymTypeExpressionFactory.createPrimitive("int");

    // When
    SymTypeOfGenerics intStream = StreamSymTypeFactory.createToptStream(intSymTypeExpression);

    // Then
    assertTrue(intStream.hasTypeInfo());
    assertSame(gs.resolveType("ToptStream").orElseThrow(), intStream.getTypeInfo());
    assertEquals("ToptStream.ToptStream", intStream.getTypeConstructorFullName());
    assertEquals(1, intStream.sizeArguments());
    assertTrue(intSymTypeExpression.deepEquals(intStream.getArgument(0)));
  }

  @Test
  public void createsSyncStream() {
    // Given
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();
    SymTypeExpression intSymTypeExpression = SymTypeExpressionFactory.createPrimitive("int");

    // When
    SymTypeOfGenerics intStream = StreamSymTypeFactory.createSyncStream(intSymTypeExpression);

    // Then
    assertTrue(intStream.hasTypeInfo());
    assertSame(gs.resolveType("SyncStream").orElseThrow(), intStream.getTypeInfo());
    assertEquals("SyncStream.SyncStream", intStream.getTypeConstructorFullName());
    assertEquals(1, intStream.sizeArguments());
    assertTrue(intSymTypeExpression.deepEquals(intStream.getArgument(0)));
  }
}
