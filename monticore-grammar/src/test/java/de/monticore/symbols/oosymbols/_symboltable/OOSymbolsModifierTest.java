/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.oosymbols._symboltable;

import de.monticore.symbols.oosymbols.OOSymbolsMill;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OOSymbolsModifierTest {

  @BeforeEach
  public void setup() {
    OOSymbolsMill.reset();
    OOSymbolsMill.init();
  }

  @Test
  public void testOOTypeSymbolModifier() {
    OOTypeSymbol symbol = OOSymbolsMill.oOTypeSymbolBuilder().setName("Foo").build();

    Assertions.assertFalse(symbol.isIsPublic());
    Assertions.assertFalse(symbol.isIsPrivate());
    Assertions.assertFalse(symbol.isIsProtected());

    symbol.setIsPublic(true);

    Assertions.assertTrue(symbol.isIsPublic());
    Assertions.assertFalse(symbol.isIsPrivate());
    Assertions.assertFalse(symbol.isIsProtected());

    symbol.setIsPrivate(true);

    Assertions.assertFalse(symbol.isIsPublic());
    Assertions.assertTrue(symbol.isIsPrivate());
    Assertions.assertFalse(symbol.isIsProtected());

    symbol.setIsProtected(true);

    Assertions.assertFalse(symbol.isIsPublic());
    Assertions.assertFalse(symbol.isIsPrivate());
    Assertions.assertTrue(symbol.isIsProtected());

    symbol.setIsProtected(false);

    Assertions.assertFalse(symbol.isIsPublic());
    Assertions.assertFalse(symbol.isIsPrivate());
    Assertions.assertFalse(symbol.isIsProtected());
  }

  @Test
  public void testFieldSymbolModifier() {
    FieldSymbol symbol = OOSymbolsMill.fieldSymbolBuilder().setName("Foo").build();

    Assertions.assertFalse(symbol.isIsPublic());
    Assertions.assertFalse(symbol.isIsPrivate());
    Assertions.assertFalse(symbol.isIsProtected());

    symbol.setIsPublic(true);

    Assertions.assertTrue(symbol.isIsPublic());
    Assertions.assertFalse(symbol.isIsPrivate());
    Assertions.assertFalse(symbol.isIsProtected());

    symbol.setIsPrivate(true);

    Assertions.assertFalse(symbol.isIsPublic());
    Assertions.assertTrue(symbol.isIsPrivate());
    Assertions.assertFalse(symbol.isIsProtected());

    symbol.setIsProtected(true);

    Assertions.assertFalse(symbol.isIsPublic());
    Assertions.assertFalse(symbol.isIsPrivate());
    Assertions.assertTrue(symbol.isIsProtected());

    symbol.setIsProtected(false);

    Assertions.assertFalse(symbol.isIsPublic());
    Assertions.assertFalse(symbol.isIsPrivate());
    Assertions.assertFalse(symbol.isIsProtected());
  }

  @Test
  public void testMethodSymbolModifier() {
    MethodSymbol symbol = OOSymbolsMill.methodSymbolBuilder().setName("Foo").build();

    Assertions.assertFalse(symbol.isIsPublic());
    Assertions.assertFalse(symbol.isIsPrivate());
    Assertions.assertFalse(symbol.isIsProtected());

    symbol.setIsPublic(true);

    Assertions.assertTrue(symbol.isIsPublic());
    Assertions.assertFalse(symbol.isIsPrivate());
    Assertions.assertFalse(symbol.isIsProtected());

    symbol.setIsPrivate(true);

    Assertions.assertFalse(symbol.isIsPublic());
    Assertions.assertTrue(symbol.isIsPrivate());
    Assertions.assertFalse(symbol.isIsProtected());

    symbol.setIsProtected(true);

    Assertions.assertFalse(symbol.isIsPublic());
    Assertions.assertFalse(symbol.isIsPrivate());
    Assertions.assertTrue(symbol.isIsProtected());

    symbol.setIsProtected(false);

    Assertions.assertFalse(symbol.isIsPublic());
    Assertions.assertFalse(symbol.isIsPrivate());
    Assertions.assertFalse(symbol.isIsProtected());

    Assertions.assertFalse(symbol.isAbstract);

    symbol.setIsAbstract(true);

    Assertions.assertTrue(symbol.isAbstract);

  }

}
