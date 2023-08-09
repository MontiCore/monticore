/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symbols.oosymbols.OOSymbolsMill;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OOSymbolsModifierTest {

  @Before
  public void setup() {
    OOSymbolsMill.reset();
    OOSymbolsMill.init();
  }

  @Test
  public void testOOTypeSymbolModifier() {
    OOTypeSymbol symbol = OOSymbolsMill.oOTypeSymbolBuilder().setName("Foo").build();

    assertFalse(symbol.isIsPublic());
    assertFalse(symbol.isIsPrivate());
    assertFalse(symbol.isIsProtected());

    symbol.setIsPublic(true);

    assertTrue(symbol.isIsPublic());
    assertFalse(symbol.isIsPrivate());
    assertFalse(symbol.isIsProtected());

    symbol.setIsPrivate(true);

    assertFalse(symbol.isIsPublic());
    assertTrue(symbol.isIsPrivate());
    assertFalse(symbol.isIsProtected());

    symbol.setIsProtected(true);

    assertFalse(symbol.isIsPublic());
    assertFalse(symbol.isIsPrivate());
    assertTrue(symbol.isIsProtected());

    symbol.setIsProtected(false);

    assertFalse(symbol.isIsPublic());
    assertFalse(symbol.isIsPrivate());
    assertFalse(symbol.isIsProtected());
  }

  @Test
  public void testFieldSymbolModifier() {
    FieldSymbol symbol = OOSymbolsMill.fieldSymbolBuilder().setName("Foo").build();

    assertFalse(symbol.isIsPublic());
    assertFalse(symbol.isIsPrivate());
    assertFalse(symbol.isIsProtected());

    symbol.setIsPublic(true);

    assertTrue(symbol.isIsPublic());
    assertFalse(symbol.isIsPrivate());
    assertFalse(symbol.isIsProtected());

    symbol.setIsPrivate(true);

    assertFalse(symbol.isIsPublic());
    assertTrue(symbol.isIsPrivate());
    assertFalse(symbol.isIsProtected());

    symbol.setIsProtected(true);

    assertFalse(symbol.isIsPublic());
    assertFalse(symbol.isIsPrivate());
    assertTrue(symbol.isIsProtected());

    symbol.setIsProtected(false);

    assertFalse(symbol.isIsPublic());
    assertFalse(symbol.isIsPrivate());
    assertFalse(symbol.isIsProtected());
  }

  @Test
  public void testMethodSymbolModifier() {
    MethodSymbol symbol = OOSymbolsMill.methodSymbolBuilder().setName("Foo").build();

    assertFalse(symbol.isIsPublic());
    assertFalse(symbol.isIsPrivate());
    assertFalse(symbol.isIsProtected());

    symbol.setIsPublic(true);

    assertTrue(symbol.isIsPublic());
    assertFalse(symbol.isIsPrivate());
    assertFalse(symbol.isIsProtected());

    symbol.setIsPrivate(true);

    assertFalse(symbol.isIsPublic());
    assertTrue(symbol.isIsPrivate());
    assertFalse(symbol.isIsProtected());

    symbol.setIsProtected(true);

    assertFalse(symbol.isIsPublic());
    assertFalse(symbol.isIsPrivate());
    assertTrue(symbol.isIsProtected());

    symbol.setIsProtected(false);

    assertFalse(symbol.isIsPublic());
    assertFalse(symbol.isIsPrivate());
    assertFalse(symbol.isIsProtected());
  }

}
