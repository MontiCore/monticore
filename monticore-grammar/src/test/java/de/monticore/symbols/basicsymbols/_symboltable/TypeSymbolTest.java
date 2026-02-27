/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.basicsymbols._symboltable;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypeSymbolTest {

  @BeforeEach
  void setUp() {
    LogStub.init();
    BasicSymbolsMill.init();
  }

  @Test @SuppressWarnings({"EqualsWithItself", "ConstantConditions"})
  void equalsShouldEqualSame() {
    // Given
    TypeSymbol symbol = BasicSymbolsMill.typeSymbolBuilder()
            .setName("Type")
            .setEnclosingScope(BasicSymbolsMill.scope())
            .build();

    // When
    boolean result = symbol.equals(symbol);

    // Then
    assertTrue(result);
  }

  @Test
  void equalsShouldNotEqualDifferent() {
    // Given
    TypeSymbol symbol1 = BasicSymbolsMill.typeSymbolBuilder()
            .setName("Type1")
            .setEnclosingScope(BasicSymbolsMill.scope())
            .build();

    TypeSymbol symbol2 = BasicSymbolsMill.typeSymbolBuilder()
            .setName("Type2")
            .setEnclosingScope(BasicSymbolsMill.scope())
            .build();

    // When
    boolean result = symbol1.equals(symbol2);

    // Then
    assertFalse(result);
  }

  @Test
  void equalsShouldEqualSurrogate() {
    // Given
    IBasicSymbolsScope scope = BasicSymbolsMill.scope();

    TypeSymbol symbol = BasicSymbolsMill.typeSymbolBuilder()
            .setName("Type")
            .setSpannedScope(BasicSymbolsMill.scope())
            .build();

    scope.add(symbol);

    TypeSymbolSurrogate surrogate = BasicSymbolsMill.typeSymbolSurrogateBuilder()
            .setName("Type")
            .setEnclosingScope(scope)
            .build();

    // When
    boolean result = symbol.equals(surrogate);

    // Then
    assertTrue(result);
  }

  @Test
  void equalsShouldNotEqualSurrogate() {
    // Given
    IBasicSymbolsScope scope = BasicSymbolsMill.scope();

    TypeSymbol symbol = BasicSymbolsMill.typeSymbolBuilder()
            .setName("Type1")
            .setSpannedScope(BasicSymbolsMill.scope())
            .build();

    scope.add(symbol);

    TypeSymbolSurrogate surrogate = BasicSymbolsMill.typeSymbolSurrogateBuilder()
            .setName("Type2")
            .setEnclosingScope(scope)
            .build();

    // When
    boolean result = symbol.equals(surrogate);

    // Then
    assertFalse(result);
  }
}
