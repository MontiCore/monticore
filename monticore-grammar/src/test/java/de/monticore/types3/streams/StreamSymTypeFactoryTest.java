// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.streams;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsGlobalScope;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.mccollectiontypes.types3.util.MCCollectionSymTypeFactory;
import de.monticore.types.mccollectiontypes.types3.util.MCCollectionTypeRelations;
import de.monticore.types3.AbstractTypeTest;
import de.monticore.types3.util.DefsTypesForTests;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StreamSymTypeFactoryTest extends AbstractTypeTest {

  MCCollectionTypeRelations collectionTypeRelations;

  @BeforeEach
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

  // Helper

  protected MCCollectionTypeRelations getCollectionTypeRelations() {
    return collectionTypeRelations;
  }
}
