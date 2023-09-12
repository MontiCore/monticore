// (c) https://github.com/MontiCore/monticore
package de.monticore.types.mccollectiontypes.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes.types3.MCCollectionSymTypeRelations;
import de.monticore.types3.AbstractTypeTest;
import de.monticore.types3.util.DefsTypesForTests;
import org.junit.Before;
import org.junit.Test;

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

  @Before
  public void init() {
    MCCollectionSymTypeRelations.init();
    DefsTypesForTests.setup();
  }

  @Test
  public void recognizeUnboxedCollectionTypes() {
    assertTrue(MCCollectionSymTypeRelations.isMCCollection(_unboxedListSymType));
    assertTrue(MCCollectionSymTypeRelations.isMCCollection(_unboxedSetSymType));
    assertTrue(MCCollectionSymTypeRelations.isMCCollection(_unboxedOptionalSymType));
    assertTrue(MCCollectionSymTypeRelations.isMCCollection(_unboxedMapSymType));
  }

  @Test
  public void recognizeBoxedCollectionTypes() {
    assertTrue(MCCollectionSymTypeRelations.isMCCollection(_boxedListSymType));
    assertTrue(MCCollectionSymTypeRelations.isMCCollection(_boxedSetSymType));
    assertTrue(MCCollectionSymTypeRelations.isMCCollection(_boxedOptionalSymType));
    assertTrue(MCCollectionSymTypeRelations.isMCCollection(_boxedMapSymType));
  }

  @Test
  public void recognizeNonCollectionTypes() {
    assertFalse(MCCollectionSymTypeRelations.isMCCollection(_intSymType));
    assertFalse(MCCollectionSymTypeRelations.isMCCollection(_personSymType));
    assertFalse(MCCollectionSymTypeRelations.isMCCollection(_unboxedString));
    assertFalse(MCCollectionSymTypeRelations.isMCCollection(SymTypeExpressionFactory.createGenerics(
        "noList", BasicSymbolsMill.scope(), _intSymType
    )));
  }

  @Test
  public void recognizeLists() {
    assertTrue(MCCollectionSymTypeRelations.isList(_unboxedListSymType));
    assertTrue(MCCollectionSymTypeRelations.isList(_boxedListSymType));
  }

  @Test
  public void recognizeNonLists() {
    assertFalse(MCCollectionSymTypeRelations.isList(_unboxedMapSymType));
    assertFalse(MCCollectionSymTypeRelations.isList(_unboxedSetSymType));
    assertFalse(MCCollectionSymTypeRelations.isList(_unboxedOptionalSymType));
    assertFalse(MCCollectionSymTypeRelations.isList(_boxedMapSymType));
    assertFalse(MCCollectionSymTypeRelations.isList(_boxedSetSymType));
    assertFalse(MCCollectionSymTypeRelations.isList(_boxedOptionalSymType));

    // incorrect number of arguments
    _unboxedListSymType.setArgumentList(Collections.emptyList());
    _boxedListSymType.setArgumentList(Collections.emptyList());
    assertFalse(MCCollectionSymTypeRelations.isList(_unboxedListSymType));
    assertFalse(MCCollectionSymTypeRelations.isList(_boxedListSymType));
    _unboxedListSymType.setArgumentList(List.of(_intSymType, _intSymType));
    _boxedListSymType.setArgumentList(List.of(_intSymType, _intSymType));
    assertFalse(MCCollectionSymTypeRelations.isList(_unboxedListSymType));
    assertFalse(MCCollectionSymTypeRelations.isList(_boxedListSymType));
  }

  @Test
  public void recognizeSets() {
    assertTrue(MCCollectionSymTypeRelations.isSet(_unboxedSetSymType));
    assertTrue(MCCollectionSymTypeRelations.isSet(_boxedSetSymType));
  }

  @Test
  public void recognizeNonSets() {
    assertFalse(MCCollectionSymTypeRelations.isSet(_unboxedMapSymType));
    assertFalse(MCCollectionSymTypeRelations.isSet(_unboxedListSymType));
    assertFalse(MCCollectionSymTypeRelations.isSet(_unboxedOptionalSymType));
    assertFalse(MCCollectionSymTypeRelations.isSet(_boxedMapSymType));
    assertFalse(MCCollectionSymTypeRelations.isSet(_boxedListSymType));
    assertFalse(MCCollectionSymTypeRelations.isSet(_boxedOptionalSymType));

    // incorrect number of arguments
    _unboxedSetSymType.setArgumentList(Collections.emptyList());
    _boxedSetSymType.setArgumentList(Collections.emptyList());
    assertFalse(MCCollectionSymTypeRelations.isSet(_unboxedSetSymType));
    assertFalse(MCCollectionSymTypeRelations.isSet(_boxedSetSymType));
    _unboxedSetSymType.setArgumentList(List.of(_intSymType, _intSymType));
    _boxedSetSymType.setArgumentList(List.of(_intSymType, _intSymType));
    assertFalse(MCCollectionSymTypeRelations.isSet(_unboxedSetSymType));
    assertFalse(MCCollectionSymTypeRelations.isSet(_boxedSetSymType));
  }

  @Test
  public void recognizeOptionals() {
    assertTrue(MCCollectionSymTypeRelations.isOptional(_unboxedOptionalSymType));
    assertTrue(MCCollectionSymTypeRelations.isOptional(_boxedOptionalSymType));
  }

  @Test
  public void recognizeNonOptionals() {
    assertFalse(MCCollectionSymTypeRelations.isOptional(_unboxedMapSymType));
    assertFalse(MCCollectionSymTypeRelations.isOptional(_unboxedListSymType));
    assertFalse(MCCollectionSymTypeRelations.isOptional(_unboxedSetSymType));
    assertFalse(MCCollectionSymTypeRelations.isOptional(_boxedMapSymType));
    assertFalse(MCCollectionSymTypeRelations.isOptional(_boxedListSymType));
    assertFalse(MCCollectionSymTypeRelations.isOptional(_boxedSetSymType));

    // incorrect number of arguments
    _unboxedOptionalSymType.setArgumentList(Collections.emptyList());
    _boxedOptionalSymType.setArgumentList(Collections.emptyList());
    assertFalse(MCCollectionSymTypeRelations.isOptional(_unboxedOptionalSymType));
    assertFalse(MCCollectionSymTypeRelations.isOptional(_boxedOptionalSymType));
    _unboxedOptionalSymType.setArgumentList(List.of(_intSymType, _intSymType));
    _boxedOptionalSymType.setArgumentList(List.of(_intSymType, _intSymType));
    assertFalse(MCCollectionSymTypeRelations.isOptional(_unboxedOptionalSymType));
    assertFalse(MCCollectionSymTypeRelations.isOptional(_boxedOptionalSymType));
  }

  @Test
  public void recognizeMaps() {
    assertTrue(MCCollectionSymTypeRelations.isMap(_unboxedMapSymType));
    assertTrue(MCCollectionSymTypeRelations.isMap(_boxedMapSymType));
  }

  @Test
  public void recognizeNonMaps() {
    assertFalse(MCCollectionSymTypeRelations.isMap(_unboxedListSymType));
    assertFalse(MCCollectionSymTypeRelations.isMap(_unboxedSetSymType));
    assertFalse(MCCollectionSymTypeRelations.isMap(_unboxedOptionalSymType));
    assertFalse(MCCollectionSymTypeRelations.isMap(_boxedListSymType));
    assertFalse(MCCollectionSymTypeRelations.isMap(_boxedSetSymType));
    assertFalse(MCCollectionSymTypeRelations.isMap(_boxedOptionalSymType));

    // incorrect number of arguments
    _unboxedMapSymType.setArgumentList(Collections.emptyList());
    _boxedMapSymType.setArgumentList(Collections.emptyList());
    assertFalse(MCCollectionSymTypeRelations.isMap(_unboxedMapSymType));
    assertFalse(MCCollectionSymTypeRelations.isMap(_boxedMapSymType));
    _unboxedMapSymType.setArgumentList(List.of(_intSymType));
    _boxedMapSymType.setArgumentList(List.of(_intSymType));
    assertFalse(MCCollectionSymTypeRelations.isMap(_unboxedMapSymType));
    assertFalse(MCCollectionSymTypeRelations.isMap(_boxedMapSymType));
    _unboxedMapSymType.setArgumentList(
        List.of(_intSymType, _intSymType, _intSymType));
    _boxedMapSymType.setArgumentList(
        List.of(_intSymType, _intSymType, _intSymType));
    assertFalse(MCCollectionSymTypeRelations.isMap(_unboxedMapSymType));
    assertFalse(MCCollectionSymTypeRelations.isMap(_boxedMapSymType));
  }

  @Test
  public void getCollectionElementTypeTest() {
    assertSame(_unboxedListSymType.getArgument(0),
        MCCollectionSymTypeRelations.getCollectionElementType(_unboxedListSymType));
    assertSame(_unboxedSetSymType.getArgument(0),
        MCCollectionSymTypeRelations.getCollectionElementType(_unboxedSetSymType));
    assertSame(_unboxedOptionalSymType.getArgument(0),
        MCCollectionSymTypeRelations.getCollectionElementType(_unboxedOptionalSymType));
    assertSame(_unboxedMapSymType.getArgument(1),
        MCCollectionSymTypeRelations.getCollectionElementType(_unboxedMapSymType));
    assertSame(_boxedListSymType.getArgument(0),
        MCCollectionSymTypeRelations.getCollectionElementType(_boxedListSymType));
    assertSame(_boxedSetSymType.getArgument(0),
        MCCollectionSymTypeRelations.getCollectionElementType(_boxedSetSymType));
    assertSame(_boxedOptionalSymType.getArgument(0),
        MCCollectionSymTypeRelations.getCollectionElementType(_boxedOptionalSymType));
    assertSame(_boxedMapSymType.getArgument(1),
        MCCollectionSymTypeRelations.getCollectionElementType(_boxedMapSymType));
  }

  @Test
  public void getMapKeyTest() {
    assertSame(_unboxedMapSymType.getArgument(0),
        MCCollectionSymTypeRelations.getMapKeyType(_unboxedMapSymType));
    assertSame(_boxedMapSymType.getArgument(0),
        MCCollectionSymTypeRelations.getMapKeyType(_boxedMapSymType));
  }

}
