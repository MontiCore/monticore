// (c) https://github.com/MontiCore/monticore
package de.monticore.types.mccollectiontypes.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mccollectiontypes.types3.IMCCollectionTypeRelations;
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
    DefsTypesForTests.setup();
  }

  @Test
  public void recognizeUnboxedCollectionTypes() {
    assertTrue(getRel().isCollection(_unboxedListSymType));
    assertTrue(getRel().isCollection(_unboxedSetSymType));
    assertTrue(getRel().isCollection(_unboxedOptionalSymType));
    assertTrue(getRel().isCollection(_unboxedMapSymType));
  }

  @Test
  public void recognizeBoxedCollectionTypes() {
    assertTrue(getRel().isCollection(_boxedListSymType));
    assertTrue(getRel().isCollection(_boxedSetSymType));
    assertTrue(getRel().isCollection(_boxedOptionalSymType));
    assertTrue(getRel().isCollection(_boxedMapSymType));
  }

  @Test
  public void recognizeNonCollectionTypes() {
    assertFalse(getRel().isCollection(_intSymType));
    assertFalse(getRel().isCollection(_personSymType));
    assertFalse(getRel().isCollection(_unboxedString));
    assertFalse(getRel().isCollection(SymTypeExpressionFactory.createGenerics(
        "noList", BasicSymbolsMill.scope(), _intSymType
    )));
  }

  @Test
  public void recognizeLists() {
    assertTrue(getRel().isList(_unboxedListSymType));
    assertTrue(getRel().isList(_boxedListSymType));
  }

  @Test
  public void recognizeNonLists() {
    assertFalse(getRel().isList(_unboxedMapSymType));
    assertFalse(getRel().isList(_unboxedSetSymType));
    assertFalse(getRel().isList(_unboxedOptionalSymType));
    assertFalse(getRel().isList(_boxedMapSymType));
    assertFalse(getRel().isList(_boxedSetSymType));
    assertFalse(getRel().isList(_boxedOptionalSymType));

    // incorrect number of arguments
    _unboxedListSymType.setArgumentList(Collections.emptyList());
    _boxedListSymType.setArgumentList(Collections.emptyList());
    assertFalse(getRel().isList(_unboxedListSymType));
    assertFalse(getRel().isList(_boxedListSymType));
    _unboxedListSymType.setArgumentList(List.of(_intSymType, _intSymType));
    _boxedListSymType.setArgumentList(List.of(_intSymType, _intSymType));
    assertFalse(getRel().isList(_unboxedListSymType));
    assertFalse(getRel().isList(_boxedListSymType));
  }

  @Test
  public void recognizeSets() {
    assertTrue(getRel().isSet(_unboxedSetSymType));
    assertTrue(getRel().isSet(_boxedSetSymType));
  }

  @Test
  public void recognizeNonSets() {
    assertFalse(getRel().isSet(_unboxedMapSymType));
    assertFalse(getRel().isSet(_unboxedListSymType));
    assertFalse(getRel().isSet(_unboxedOptionalSymType));
    assertFalse(getRel().isSet(_boxedMapSymType));
    assertFalse(getRel().isSet(_boxedListSymType));
    assertFalse(getRel().isSet(_boxedOptionalSymType));

    // incorrect number of arguments
    _unboxedSetSymType.setArgumentList(Collections.emptyList());
    _boxedSetSymType.setArgumentList(Collections.emptyList());
    assertFalse(getRel().isSet(_unboxedSetSymType));
    assertFalse(getRel().isSet(_boxedSetSymType));
    _unboxedSetSymType.setArgumentList(List.of(_intSymType, _intSymType));
    _boxedSetSymType.setArgumentList(List.of(_intSymType, _intSymType));
    assertFalse(getRel().isSet(_unboxedSetSymType));
    assertFalse(getRel().isSet(_boxedSetSymType));
  }

  @Test
  public void recognizeOptionals() {
    assertTrue(getRel().isOptional(_unboxedOptionalSymType));
    assertTrue(getRel().isOptional(_boxedOptionalSymType));
  }

  @Test
  public void recognizeNonOptionals() {
    assertFalse(getRel().isOptional(_unboxedMapSymType));
    assertFalse(getRel().isOptional(_unboxedListSymType));
    assertFalse(getRel().isOptional(_unboxedSetSymType));
    assertFalse(getRel().isOptional(_boxedMapSymType));
    assertFalse(getRel().isOptional(_boxedListSymType));
    assertFalse(getRel().isOptional(_boxedSetSymType));

    // incorrect number of arguments
    _unboxedOptionalSymType.setArgumentList(Collections.emptyList());
    _boxedOptionalSymType.setArgumentList(Collections.emptyList());
    assertFalse(getRel().isOptional(_unboxedOptionalSymType));
    assertFalse(getRel().isOptional(_boxedOptionalSymType));
    _unboxedOptionalSymType.setArgumentList(List.of(_intSymType, _intSymType));
    _boxedOptionalSymType.setArgumentList(List.of(_intSymType, _intSymType));
    assertFalse(getRel().isOptional(_unboxedOptionalSymType));
    assertFalse(getRel().isOptional(_boxedOptionalSymType));
  }

  @Test
  public void recognizeMaps() {
    assertTrue(getRel().isMap(_unboxedMapSymType));
    assertTrue(getRel().isMap(_boxedMapSymType));
  }

  @Test
  public void recognizeNonMaps() {
    assertFalse(getRel().isMap(_unboxedListSymType));
    assertFalse(getRel().isMap(_unboxedSetSymType));
    assertFalse(getRel().isMap(_unboxedOptionalSymType));
    assertFalse(getRel().isMap(_boxedListSymType));
    assertFalse(getRel().isMap(_boxedSetSymType));
    assertFalse(getRel().isMap(_boxedOptionalSymType));

    // incorrect number of arguments
    _unboxedMapSymType.setArgumentList(Collections.emptyList());
    _boxedMapSymType.setArgumentList(Collections.emptyList());
    assertFalse(getRel().isMap(_unboxedMapSymType));
    assertFalse(getRel().isMap(_boxedMapSymType));
    _unboxedMapSymType.setArgumentList(List.of(_intSymType));
    _boxedMapSymType.setArgumentList(List.of(_intSymType));
    assertFalse(getRel().isMap(_unboxedMapSymType));
    assertFalse(getRel().isMap(_boxedMapSymType));
    _unboxedMapSymType.setArgumentList(
        List.of(_intSymType, _intSymType, _intSymType));
    _boxedMapSymType.setArgumentList(
        List.of(_intSymType, _intSymType, _intSymType));
    assertFalse(getRel().isMap(_unboxedMapSymType));
    assertFalse(getRel().isMap(_boxedMapSymType));
  }

  @Test
  public void getCollectionElementTypeTest() {
    assertSame(_unboxedListSymType.getArgument(0),
        getRel().getCollectionElementType(_unboxedListSymType));
    assertSame(_unboxedSetSymType.getArgument(0),
        getRel().getCollectionElementType(_unboxedSetSymType));
    assertSame(_unboxedOptionalSymType.getArgument(0),
        getRel().getCollectionElementType(_unboxedOptionalSymType));
    assertSame(_unboxedMapSymType.getArgument(1),
        getRel().getCollectionElementType(_unboxedMapSymType));
    assertSame(_boxedListSymType.getArgument(0),
        getRel().getCollectionElementType(_boxedListSymType));
    assertSame(_boxedSetSymType.getArgument(0),
        getRel().getCollectionElementType(_boxedSetSymType));
    assertSame(_boxedOptionalSymType.getArgument(0),
        getRel().getCollectionElementType(_boxedOptionalSymType));
    assertSame(_boxedMapSymType.getArgument(1),
        getRel().getCollectionElementType(_boxedMapSymType));
  }

  @Test
  public void getMapKeyTest() {
    assertSame(_unboxedMapSymType.getArgument(0),
        getRel().getMapKeyType(_unboxedMapSymType));
    assertSame(_boxedMapSymType.getArgument(0),
        getRel().getMapKeyType(_boxedMapSymType));
  }

  // Helper

  protected IMCCollectionTypeRelations getRel() {
    return new MCCollectionTypeRelations();
  }

}
