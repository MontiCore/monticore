/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types3;

import de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.types.check.SymTypeExpression;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Optional;

import static de.monticore.expressions.combineexpressionswithliterals.CombineExpressionsWithLiteralsMill.typeSymbolBuilder;
import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static de.monticore.statements.testmccommonstatements.TestMCCommonStatementsMill.*;
import static de.monticore.symbols.basicsymbols.BasicSymbolsMill.BOOLEAN;
import static de.monticore.types.check.SymTypeExpressionFactory.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Holds the test for {@link SymTypeRelationsOfIterables}.
 */
class SymTypeRelationsOfIterablesTest extends AbstractMCTest {

  @BeforeEach
  public void init() {
    CombineExpressionsWithLiteralsMill.reset();
    CombineExpressionsWithLiteralsMill.init();
    BasicSymbolsMill.initializePrimitives();
    assertNoFindings();
  }

  @Test
  void testIsArrayType_Obscure() {
    // Given
    SymTypeExpression type = createObscureType();

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When && Then
    assertFalse(symTypeRel._isArrayType(type));
  }

  @Test
  void testIsArrayType_Primitive() {
    // Given
    SymTypeExpression type = createPrimitive(BOOLEAN);

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When && Then
    assertFalse(symTypeRel._isArrayType(type));
  }

  @Test
  void testIsArrayType_Object() {
    // Given
    TypeSymbol Type = typeSymbolBuilder().setName("Type").build();
    SymTypeExpression type = createTypeObject(Type);

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When && Then
    assertFalse(symTypeRel._isArrayType(type));
  }

  @Test
  void testIsArrayType_Array() {
    // Given
    SymTypeExpression type = createTypeArray(createPrimitive(BOOLEAN), 1);

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When && Then
    assertTrue(symTypeRel._isArrayType(type));
  }

  @Test
  void testIsOfTypeIterable_Obscure() {
    // Given
    SymTypeExpression type = createObscureType();

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When && Then
    assertFalse(symTypeRel._isOfTypeIterable(type));
  }

  @Test
  void testIsOfTypeIterable_Object() {
    // Given
    TypeSymbol Type = typeSymbolBuilder().setName("Type").build();
    SymTypeExpression type = createTypeObject(Type);

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When && Then
    assertFalse(symTypeRel._isOfTypeIterable(type));
  }

  @Test
  void testIsOfTypeIterable_Generic() {
    // Given
    TypeVarSymbol T = typeVarSymbolBuilder().setName("T").build();

    TypeSymbol Generic = oOTypeSymbolBuilder()
      .setName("Generic")
      .setEnclosingScope(globalScope())
      .setSpannedScope(scope())
      .build();
    Generic.getEnclosingScope().add(Generic);
    Generic.getEnclosingScope().addSubScope(Generic.getSpannedScope());
    Generic.getSpannedScope().add(T);

    SymTypeExpression type = createGenerics(Generic, createPrimitive(BOOLEAN));

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When && Then
    assertFalse(symTypeRel._isOfTypeIterable(type));
  }

  @ParameterizedTest
  @CsvSource(value = {
    "Iterable, null",
    "Iterable, java.lang"
  })
  void testIsOfTypeIterable_Iterable(String name, String pkg) {
    // Given
    TypeVarSymbol T = typeVarSymbolBuilder().setName("T").build();

    TypeSymbol Iterable = oOTypeSymbolBuilder()
      .setName(name)
      .setPackageName(pkg)
      .setEnclosingScope(globalScope())
      .setSpannedScope(scope())
      .build();
    Iterable.getEnclosingScope().add(Iterable);
    Iterable.getEnclosingScope().addSubScope(Iterable.getSpannedScope());
    Iterable.getSpannedScope().add(T);

    SymTypeExpression type = createGenerics(Iterable, createPrimitive(BOOLEAN));

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When && Then
    assertTrue(symTypeRel._isOfTypeIterable(type));
  }

  @Test
  void testIsOfTypeIterableOrSubType_Obscure() {
    // Given
    SymTypeExpression type = createObscureType();

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When && Then
    assertFalse(symTypeRel._isOfTypeIterableOrSubType(type));
  }

  @Test
  void testIsOfTypeIterableOrSubType_Object() {
    // Given
    TypeSymbol Type = typeSymbolBuilder().setName("Type").build();
    SymTypeExpression type = createTypeObject(Type);

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When && Then
    assertFalse(symTypeRel._isOfTypeIterableOrSubType(type));
  }

  @Test
  void testIsOfTypeIterableOrSubType_Iterable() {
    // Given
    TypeVarSymbol T = typeVarSymbolBuilder().setName("T").build();

    TypeSymbol Iterable = oOTypeSymbolBuilder()
      .setName("Iterable")
      .setEnclosingScope(globalScope())
      .setSpannedScope(scope())
      .build();
    Iterable.getEnclosingScope().add(Iterable);
    Iterable.getEnclosingScope().addSubScope(Iterable.getSpannedScope());
    Iterable.getSpannedScope().add(T);

    SymTypeExpression type = createGenerics(Iterable, createPrimitive(BOOLEAN));

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When && Then
    assertTrue(symTypeRel._isOfTypeIterableOrSubType(type));
  }

  @Test
  void testIsOfTypeIterableOrSubType_SubtypeOfIterable() {
    // Given
    TypeVarSymbol T = typeVarSymbolBuilder().setName("T").build();

    TypeSymbol Iterable = oOTypeSymbolBuilder()
      .setName("Iterable")
      .setEnclosingScope(globalScope())
      .setSpannedScope(scope())
      .build();
    Iterable.getEnclosingScope().add(Iterable);
    Iterable.getEnclosingScope().addSubScope(Iterable.getSpannedScope());
    Iterable.getSpannedScope().add(T);

    SymTypeExpression superType = createGenerics(Iterable, createPrimitive(BOOLEAN));

    TypeSymbol Type = oOTypeSymbolBuilder().setName("Type")
      .setSuperTypesList(List.of(superType)).build();

    SymTypeExpression type = createTypeObject(Type);

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When && Then
    assertTrue(symTypeRel._isOfTypeIterableOrSubType(type));
  }

  @Test
  void testGetIterationType_Obscure() {
    // Given
    SymTypeExpression type = createObscureType();

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When
    Optional<SymTypeExpression> result = symTypeRel._getIterationType(type);

    // Then
    assertFalse(result.isPresent());
  }

  @Test
  void testGetIterationType_Object() {
    // Given
    TypeSymbol Type = typeSymbolBuilder().setName("Type").build();
    SymTypeExpression type = createTypeObject(Type);

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When
    Optional<SymTypeExpression> result = symTypeRel._getIterationType(type);

    // Then
    assertFalse(result.isPresent());
  }

  @Test
  void testGetIterationType_Iterable() {
    // Given
    TypeVarSymbol T = typeVarSymbolBuilder().setName("T").build();

    TypeSymbol Iterable = oOTypeSymbolBuilder()
      .setName("Iterable")
      .setEnclosingScope(globalScope())
      .setSpannedScope(scope())
      .build();
    Iterable.getEnclosingScope().add(Iterable);
    Iterable.getEnclosingScope().addSubScope(Iterable.getSpannedScope());
    Iterable.getSpannedScope().add(T);

    SymTypeExpression typeArg = createPrimitive(BOOLEAN);

    SymTypeExpression type = createGenerics(Iterable, typeArg);

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When
    Optional<SymTypeExpression> result = symTypeRel._getIterationType(type);

    // Then
    assertTrue(result.isPresent());
    assertEquals(typeArg, result.get());
  }

  @Test
  void testGetIterationType_SubtypeOfIterable() {
    // Given
    TypeVarSymbol T = typeVarSymbolBuilder().setName("T").build();

    TypeSymbol Iterable = oOTypeSymbolBuilder()
      .setName("Iterable")
      .setEnclosingScope(globalScope())
      .setSpannedScope(scope())
      .build();
    Iterable.getEnclosingScope().add(Iterable);
    Iterable.getEnclosingScope().addSubScope(Iterable.getSpannedScope());
    Iterable.getSpannedScope().add(T);

    SymTypeExpression typeArg = createPrimitive(BOOLEAN);

    SymTypeExpression superType = createGenerics(Iterable, typeArg);

    TypeSymbol Type = oOTypeSymbolBuilder().setName("Type")
      .setSuperTypesList(List.of(superType)).build();

    SymTypeExpression type = createTypeObject(Type);

    SymTypeRelationsOfIterables symTypeRel = new SymTypeRelationsOfIterables();

    // When
    Optional<SymTypeExpression> result = symTypeRel._getIterationType(type);

    // Then
    assertTrue(result.isPresent());
    assertEquals(typeArg, result.get());
  }

}
