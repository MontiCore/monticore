/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Holds tests for {@link ComponentType2TypeSymbolAdapter}.
 */
public class ComponentType2TypeSymbolAdapterTest {

  @ParameterizedTest
  @MethodSource("componentTypeSymbolProvider")
  void shouldAdaptFields(@NonNull ComponentTypeSymbol adaptee) {
    // Given
    ComponentType2TypeSymbolAdapter adapter = new ComponentType2TypeSymbolAdapter(adaptee);

    // Then
    assertAll(
        () -> assertEquals(adaptee.getName(), adapter.getName(),
            "The adapter's name should match the adaptee's name."),
        () -> assertEquals(adaptee.getFullName(), adapter.getFullName(),
            "The adapter's full name should match the adaptee's full name."),
        () -> assertEquals(adaptee.getSpannedScope(), adapter.getSpannedScope(),
            "The adapter's spanned scope should match the adaptee's enclosing scope."),
        () -> assertEquals(adaptee.getEnclosingScope(), adapter.getEnclosingScope(),
            "The adapter's enclosing scope should match the adaptee's enclosing scope."),
        () -> assertEquals(adaptee.getSourcePosition(), adapter.getSourcePosition(),
            "The adapter's source position should match the adaptee's source position."),
        () -> assertEquals(BasicAccessModifier.PUBLIC, adapter.getAccessModifier(),
            "The adapter should have a public access modifier as ports are the public interface of a component.")
    );
  }

  protected static Stream<ComponentTypeSymbol> componentTypeSymbolProvider() {
    ICompSymbolsScope scope = CompSymbolsMill.scope();

    // incoming port
    ComponentTypeSymbol comp1 = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("c1")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();
    scope.add(comp1);
    comp1.setEnclosingScope(scope);

    // outgoing port
    ComponentTypeSymbol comp2 = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("c2")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();
    scope.add(comp2);
    comp2.setEnclosingScope(scope);

    return Stream.of(comp1, comp2);
  }
}
