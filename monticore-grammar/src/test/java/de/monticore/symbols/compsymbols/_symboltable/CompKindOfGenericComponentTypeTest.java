/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.types.check.CompKindOfComponentType;
import de.monticore.types.check.CompKindOfGenericComponentType;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * Holds test for {@link CompKindOfGenericComponentType}
 */
public class CompKindOfGenericComponentTypeTest {

  @Test
  void testDeepClone() {
    // Given
    CompKindOfGenericComponentType comp = new CompKindOfGenericComponentType(CompSymbolsMill.componentTypeSymbolBuilder()
      .setName("A")
      .setSpannedScope(CompSymbolsMill.scope())
      .build(),
      List.of(SymTypeExpressionFactory.createPrimitive("int")));

    // When
    CompKindOfGenericComponentType clone = comp.deepClone().asGenericComponentType();

    // Then
    Assertions.assertEquals(comp.getTypeInfo(), clone.getTypeInfo());
    Assertions.assertNotSame(comp.getArguments(), clone.getArguments());
    Assertions.assertIterableEquals(comp.getArguments(), clone.getArguments());
    Assertions.assertNotSame(comp.getParamBindings(), clone.getParamBindings());
    Assertions.assertIterableEquals(comp.getParamBindingsAsList(), clone.getParamBindingsAsList());
    Assertions.assertEquals(comp.getSourceNode().isPresent(), clone.getSourceNode().isPresent());
    Assertions.assertNotSame(comp.getTypeBindingsAsList(), clone.getTypeBindingsAsList());
  }
}
