// (c) https://github.com/MontiCore/monticore
package de.monticore.types.mccollectiontypes.types3.util;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types3.AbstractTypeTest;
import de.monticore.types3.util.DefsTypesForTests;
import org.junit.Before;
import org.junit.Test;

import static de.monticore.types3.util.DefsTypesForTests._boxedListSymType;
import static de.monticore.types3.util.DefsTypesForTests._boxedMapSymType;
import static de.monticore.types3.util.DefsTypesForTests._boxedOptionalSymType;
import static de.monticore.types3.util.DefsTypesForTests._boxedSetSymType;
import static de.monticore.types3.util.DefsTypesForTests._floatSymType;
import static de.monticore.types3.util.DefsTypesForTests._intSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedListSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedMapSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedOptionalSymType;
import static de.monticore.types3.util.DefsTypesForTests._unboxedSetSymType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class MCCollectionSymTypeFactoryTest extends AbstractTypeTest {

  MCCollectionTypeRelations collectionTypeRelations;

  @Before
  public void setup() {
    BasicSymbolsMill.reset();
    BasicSymbolsMill.init();
    // make collection types available in unboxed AND boxed form
    DefsTypesForTests.setup();
    collectionTypeRelations = new MCCollectionTypeRelations();
  }

  @Test
  public void createsList() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    SymTypeOfGenerics intList = MCCollectionSymTypeFactory.createList(_intSymType);
    assertTrue(intList.hasTypeInfo());
    assertSame(_unboxedListSymType.getTypeInfo(), intList.getTypeInfo());
    assertEquals("List", intList.getTypeConstructorFullName());
    assertEquals(1, intList.sizeArguments());
    assertTrue(_intSymType.deepEquals(intList.getArgument(0)));
    assertTrue(getCollectionTypeRelations().isList(intList));

    // again, but with the unboxed "List" not being available
    gs.remove(gs.resolveType("List").get());
    intList = MCCollectionSymTypeFactory.createList(_intSymType);
    assertTrue(intList.hasTypeInfo());
    assertSame(_boxedListSymType.getTypeInfo(), intList.getTypeInfo());
    assertEquals("java.util.List", intList.getTypeConstructorFullName());
    assertEquals(1, intList.sizeArguments());
    assertTrue(_intSymType.deepEquals(intList.getArgument(0)));
    assertTrue(getCollectionTypeRelations().isList(intList));
  }

  @Test
  public void createsSet() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    SymTypeOfGenerics intSet = MCCollectionSymTypeFactory.createSet(_intSymType);
    assertTrue(intSet.hasTypeInfo());
    assertSame(_unboxedSetSymType.getTypeInfo(), intSet.getTypeInfo());
    assertEquals("Set", intSet.getTypeConstructorFullName());
    assertEquals(1, intSet.sizeArguments());
    assertTrue(_intSymType.deepEquals(intSet.getArgument(0)));
    assertTrue(getCollectionTypeRelations().isSet(intSet));

    // again, but with the unboxed "Set" not being available
    gs.remove(gs.resolveType("Set").get());
    intSet = MCCollectionSymTypeFactory.createSet(_intSymType);
    assertTrue(intSet.hasTypeInfo());
    assertSame(_boxedSetSymType.getTypeInfo(), intSet.getTypeInfo());
    assertEquals("java.util.Set", intSet.getTypeConstructorFullName());
    assertEquals(1, intSet.sizeArguments());
    assertTrue(_intSymType.deepEquals(intSet.getArgument(0)));
    assertTrue(getCollectionTypeRelations().isSet(intSet));
  }

  @Test
  public void createsOptional() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    SymTypeOfGenerics intOptional = MCCollectionSymTypeFactory.createOptional(_intSymType);
    assertTrue(intOptional.hasTypeInfo());
    assertSame(_unboxedOptionalSymType.getTypeInfo(), intOptional.getTypeInfo());
    assertEquals("Optional", intOptional.getTypeConstructorFullName());
    assertEquals(1, intOptional.sizeArguments());
    assertTrue(_intSymType.deepEquals(intOptional.getArgument(0)));
    assertTrue(getCollectionTypeRelations().isOptional(intOptional));

    // again, but with the unboxed "Optional" not being available
    gs.remove(gs.resolveType("Optional").get());
    intOptional = MCCollectionSymTypeFactory.createOptional(_intSymType);
    assertTrue(intOptional.hasTypeInfo());
    assertSame(_boxedOptionalSymType.getTypeInfo(), intOptional.getTypeInfo());
    assertEquals("java.util.Optional", intOptional.getTypeConstructorFullName());
    assertEquals(1, intOptional.sizeArguments());
    assertTrue(_intSymType.deepEquals(intOptional.getArgument(0)));
    assertTrue(getCollectionTypeRelations().isOptional(intOptional));
  }

  @Test
  public void createsMap() {
    IBasicSymbolsGlobalScope gs = BasicSymbolsMill.globalScope();

    SymTypeOfGenerics intMap =
        MCCollectionSymTypeFactory.createMap(_intSymType, _floatSymType);
    assertTrue(intMap.hasTypeInfo());
    assertSame(_unboxedMapSymType.getTypeInfo(), intMap.getTypeInfo());
    assertEquals("Map", intMap.getTypeConstructorFullName());
    assertEquals(2, intMap.sizeArguments());
    assertTrue(_intSymType.deepEquals(intMap.getArgument(0)));
    assertTrue(_floatSymType.deepEquals(intMap.getArgument(1)));
    assertTrue(getCollectionTypeRelations().isMap(intMap));

    // again, but with the unboxed "Map" not being available
    gs.remove(gs.resolveType("Map").get());
    intMap = MCCollectionSymTypeFactory.createMap(_intSymType, _floatSymType);
    assertTrue(intMap.hasTypeInfo());
    assertSame(_boxedMapSymType.getTypeInfo(), intMap.getTypeInfo());
    assertEquals("java.util.Map", intMap.getTypeConstructorFullName());
    assertEquals(2, intMap.sizeArguments());
    assertTrue(_intSymType.deepEquals(intMap.getArgument(0)));
    assertTrue(_floatSymType.deepEquals(intMap.getArgument(1)));
    assertTrue(getCollectionTypeRelations().isMap(intMap));
  }

  // Helper

  protected MCCollectionTypeRelations getCollectionTypeRelations() {
    return collectionTypeRelations;
  }
}
