// (c) https://github.com/MontiCore/monticore
package de.monticore.types.mccollectiontypes.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.AbstractTypeTest;
import de.monticore.types3.util.DefsTypesForTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static de.monticore.types3.util.DefsTypesForTests._boxedListSymType;
import static de.monticore.types3.util.DefsTypesForTests._boxedMapSymType;
import static de.monticore.types3.util.DefsTypesForTests._boxedOptionalSymType;
import static de.monticore.types3.util.DefsTypesForTests._boxedSetSymType;
import static de.monticore.types3.util.DefsTypesForTests._intSymType;
import static de.monticore.types3.util.DefsTypesForTests._personSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedListSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedMapSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedOptionalSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedSetSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MCCollectionTypeRelationsTest extends AbstractTypeTest {

  @BeforeEach
  public void init() {
    MCCollectionSymTypeRelations.init();
    DefsTypesForTests.setup();
  }

  @Test
  public void recognizeUnboxedCollectionTypes() {
    Assertions.assertTrue(MCCollectionSymTypeRelations.isMCCollection(_unboxedListSymType));
    Assertions.assertTrue(MCCollectionSymTypeRelations.isMCCollection(_unboxedSetSymType));
    Assertions.assertTrue(MCCollectionSymTypeRelations.isMCCollection(_unboxedOptionalSymType));
    Assertions.assertTrue(MCCollectionSymTypeRelations.isMCCollection(_unboxedMapSymType));
  }

  @Test
  public void recognizeBoxedCollectionTypes() {
    Assertions.assertTrue(MCCollectionSymTypeRelations.isMCCollection(_boxedListSymType));
    Assertions.assertTrue(MCCollectionSymTypeRelations.isMCCollection(_boxedSetSymType));
    Assertions.assertTrue(MCCollectionSymTypeRelations.isMCCollection(_boxedOptionalSymType));
    Assertions.assertTrue(MCCollectionSymTypeRelations.isMCCollection(_boxedMapSymType));
  }

  @Test
  public void recognizeNonCollectionTypes() {
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMCCollection(_intSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMCCollection(_personSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMCCollection(_unboxedString));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMCCollection(SymTypeExpressionFactory.createGenerics(
        "noList", BasicSymbolsMill.scope(), _intSymType
    )));
  }

  @Test
  public void recognizeLists() {
    Assertions.assertTrue(MCCollectionSymTypeRelations.isList(_unboxedListSymType));
    Assertions.assertTrue(MCCollectionSymTypeRelations.isList(_boxedListSymType));
  }

  @Test
  public void recognizeNonLists() {
    Assertions.assertFalse(MCCollectionSymTypeRelations.isList(_unboxedMapSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isList(_unboxedSetSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isList(_unboxedOptionalSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isList(_boxedMapSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isList(_boxedSetSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isList(_boxedOptionalSymType));

    // incorrect number of arguments
    _unboxedListSymType.setArgumentList(Collections.emptyList());
    _boxedListSymType.setArgumentList(Collections.emptyList());
    Assertions.assertFalse(MCCollectionSymTypeRelations.isList(_unboxedListSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isList(_boxedListSymType));
    _unboxedListSymType.setArgumentList(List.of(_intSymType, _intSymType));
    _boxedListSymType.setArgumentList(List.of(_intSymType, _intSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isList(_unboxedListSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isList(_boxedListSymType));
  }

  @Test
  public void recognizeSets() {
    Assertions.assertTrue(MCCollectionSymTypeRelations.isSet(_unboxedSetSymType));
    Assertions.assertTrue(MCCollectionSymTypeRelations.isSet(_boxedSetSymType));
  }

  @Test
  public void recognizeNonSets() {
    Assertions.assertFalse(MCCollectionSymTypeRelations.isSet(_unboxedMapSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isSet(_unboxedListSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isSet(_unboxedOptionalSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isSet(_boxedMapSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isSet(_boxedListSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isSet(_boxedOptionalSymType));

    // incorrect number of arguments
    _unboxedSetSymType.setArgumentList(Collections.emptyList());
    _boxedSetSymType.setArgumentList(Collections.emptyList());
    Assertions.assertFalse(MCCollectionSymTypeRelations.isSet(_unboxedSetSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isSet(_boxedSetSymType));
    _unboxedSetSymType.setArgumentList(List.of(_intSymType, _intSymType));
    _boxedSetSymType.setArgumentList(List.of(_intSymType, _intSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isSet(_unboxedSetSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isSet(_boxedSetSymType));
  }

  @Test
  public void recognizeOptionals() {
    Assertions.assertTrue(MCCollectionSymTypeRelations.isOptional(_unboxedOptionalSymType));
    Assertions.assertTrue(MCCollectionSymTypeRelations.isOptional(_boxedOptionalSymType));
  }

  @Test
  public void recognizeNonOptionals() {
    Assertions.assertFalse(MCCollectionSymTypeRelations.isOptional(_unboxedMapSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isOptional(_unboxedListSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isOptional(_unboxedSetSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isOptional(_boxedMapSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isOptional(_boxedListSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isOptional(_boxedSetSymType));

    // incorrect number of arguments
    _unboxedOptionalSymType.setArgumentList(Collections.emptyList());
    _boxedOptionalSymType.setArgumentList(Collections.emptyList());
    Assertions.assertFalse(MCCollectionSymTypeRelations.isOptional(_unboxedOptionalSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isOptional(_boxedOptionalSymType));
    _unboxedOptionalSymType.setArgumentList(List.of(_intSymType, _intSymType));
    _boxedOptionalSymType.setArgumentList(List.of(_intSymType, _intSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isOptional(_unboxedOptionalSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isOptional(_boxedOptionalSymType));
  }

  @Test
  public void recognizeMaps() {
    Assertions.assertTrue(MCCollectionSymTypeRelations.isMap(_unboxedMapSymType));
    Assertions.assertTrue(MCCollectionSymTypeRelations.isMap(_boxedMapSymType));
  }

  @Test
  public void recognizeNonMaps() {
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMap(_unboxedListSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMap(_unboxedSetSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMap(_unboxedOptionalSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMap(_boxedListSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMap(_boxedSetSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMap(_boxedOptionalSymType));

    // incorrect number of arguments
    _unboxedMapSymType.setArgumentList(Collections.emptyList());
    _boxedMapSymType.setArgumentList(Collections.emptyList());
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMap(_unboxedMapSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMap(_boxedMapSymType));
    _unboxedMapSymType.setArgumentList(List.of(_intSymType));
    _boxedMapSymType.setArgumentList(List.of(_intSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMap(_unboxedMapSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMap(_boxedMapSymType));
    _unboxedMapSymType.setArgumentList(
        List.of(_intSymType, _intSymType, _intSymType));
    _boxedMapSymType.setArgumentList(
        List.of(_intSymType, _intSymType, _intSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMap(_unboxedMapSymType));
    Assertions.assertFalse(MCCollectionSymTypeRelations.isMap(_boxedMapSymType));
  }

  @Test
  public void getCollectionElementTypeTest() {
    Assertions.assertSame(_unboxedListSymType.getArgument(0), MCCollectionSymTypeRelations.getCollectionElementType(_unboxedListSymType));
    Assertions.assertSame(_unboxedSetSymType.getArgument(0), MCCollectionSymTypeRelations.getCollectionElementType(_unboxedSetSymType));
    Assertions.assertSame(_unboxedOptionalSymType.getArgument(0), MCCollectionSymTypeRelations.getCollectionElementType(_unboxedOptionalSymType));
    Assertions.assertSame(_unboxedMapSymType.getArgument(1), MCCollectionSymTypeRelations.getCollectionElementType(_unboxedMapSymType));
    Assertions.assertSame(_boxedListSymType.getArgument(0), MCCollectionSymTypeRelations.getCollectionElementType(_boxedListSymType));
    Assertions.assertSame(_boxedSetSymType.getArgument(0), MCCollectionSymTypeRelations.getCollectionElementType(_boxedSetSymType));
    Assertions.assertSame(_boxedOptionalSymType.getArgument(0), MCCollectionSymTypeRelations.getCollectionElementType(_boxedOptionalSymType));
    Assertions.assertSame(_boxedMapSymType.getArgument(1), MCCollectionSymTypeRelations.getCollectionElementType(_boxedMapSymType));
  }

  @Test
  public void getMapKeyTest() {
    Assertions.assertSame(_unboxedMapSymType.getArgument(0), MCCollectionSymTypeRelations.getMapKeyType(_unboxedMapSymType));
    Assertions.assertSame(_boxedMapSymType.getArgument(0), MCCollectionSymTypeRelations.getMapKeyType(_boxedMapSymType));
  }

}
