/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.types.check.CompKindOfComponentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Holds test for {@link CompKindOfComponentType}
 */
public class CompKindOfComponentTypeTest {

  @Test
  void testDeepClone() {
    // Given
    CompKindOfComponentType comp = new CompKindOfComponentType(CompSymbolsMill.componentTypeSymbolBuilder()
      .setName("A")
      .setSpannedScope(CompSymbolsMill.scope())
      .build());

    // When
    CompKindOfComponentType clone = comp.deepClone().asComponentType();

    // Then
    Assertions.assertEquals(comp.getTypeInfo(), clone.getTypeInfo());
    Assertions.assertNotSame(comp.getArguments(), clone.getArguments());
    Assertions.assertIterableEquals(comp.getArguments(), clone.getArguments());
    Assertions.assertNotSame(comp.getParamBindings(), clone.getParamBindings());
    Assertions.assertIterableEquals(comp.getParamBindingsAsList(), clone.getParamBindingsAsList());
    Assertions.assertEquals(comp.getSourceNode().isPresent(), clone.getSourceNode().isPresent());
  }
}
