/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;


import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.types.check.CompKindOfComponentType;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Holds tests for the handwritten methods of {@link ComponentTypeSymbol}.
 */
public class ComponentSymbolTest {

  @Test
  void shouldGetAllInheritedPortsWithOverlappingName() {
    // Given
    ComponentTypeSymbol parent = CompSymbolsMill.componentTypeSymbolBuilder()
      .setSpannedScope(CompSymbolsMill.scope())
      .setName("sut")
      .build();

    PortSymbol p1 = CompSymbolsMill.portSymbolBuilder().setName("p").setType(SymTypeExpressionFactory.createObscureType()).build();
    PortSymbol p2 = CompSymbolsMill.portSymbolBuilder().setName("p").setType(SymTypeExpressionFactory.createObscureType()).build();

    parent.getSpannedScope().add(p1);
    parent.getSpannedScope().add(p2);

    ComponentTypeSymbol sut = CompSymbolsMill.componentTypeSymbolBuilder()
      .setName("sut")
      .setSpannedScope(CompSymbolsMill.scope())
      .setSuperComponentsList(Collections.singletonList(new CompKindOfComponentType(parent)))
      .build();

    // When
    Set<PortSymbol> ports = sut.getAllPorts();

    // Then
    Assertions.assertIterableEquals(List.of(p1, p2), ports);
  }
}