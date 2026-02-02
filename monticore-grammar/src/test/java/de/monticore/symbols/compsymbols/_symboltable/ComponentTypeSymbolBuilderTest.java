/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindOfComponentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Holds tests for the handwritten methods of {@link ComponentTypeSymbolBuilder}.
 */
class ComponentTypeSymbolBuilderTest {

  @Test
  void shouldBeValid() {
    ComponentTypeSymbolBuilder builder = new ComponentTypeSymbolBuilder();
    builder.setName("A").setSpannedScope(CompSymbolsMill.scope());
    assertTrue(builder.isValid());
  }

  @Test
  void shouldBeInvalid() {
    ComponentTypeSymbolBuilder builder1 = new ComponentTypeSymbolBuilder();
    ComponentTypeSymbolBuilder builder2 = new ComponentTypeSymbolBuilder();
    builder2.setName("Comp");
    ComponentTypeSymbolBuilder builder3 = new ComponentTypeSymbolBuilder();
    builder3.setSpannedScope(CompSymbolsMill.scope());
    assertFalse(builder1.isValid());
    assertFalse(builder2.isValid());
    assertFalse(builder3.isValid());
  }

  @Test
  void shouldHaveParent() {
    ComponentTypeSymbol parentComp = CompSymbolsMill.componentTypeSymbolBuilder()
        .setSpannedScope(CompSymbolsMill.scope()).setName("A").build();
    ComponentTypeSymbol childComp = CompSymbolsMill.componentTypeSymbolBuilder().setName("B")
        .setSpannedScope(CompSymbolsMill.scope()).setSuperComponentsList(Collections.singletonList(new CompKindOfComponentType(parentComp))).build();
    assertFalse(childComp.isEmptySuperComponents());
  }

  @Test
  void shouldNotHaveParent() {
    ComponentTypeSymbol symbol = CompSymbolsMill.componentTypeSymbolBuilder().setName("A")
        .setSpannedScope(CompSymbolsMill.scope()).build();
    assertTrue(symbol.isEmptySuperComponents());
  }

  @Test
  void shouldHaveSpec() {
    // Given
    ComponentTypeSymbol parentComp = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("A")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();
    CompKindExpression parentExpr = new CompKindOfComponentType(parentComp);
    ComponentTypeSymbolBuilder childBuilder = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("B")
        .setSpannedScope(CompSymbolsMill.scope())
        .setRefinementsList(Collections.singletonList(parentExpr));

    // When
    ComponentTypeSymbol child = childBuilder.build();

    // Then
    assertEquals(1, child.sizeRefinements());
    assertEquals(parentExpr, child.getRefinements(0));
  }

  @Test
  void shouldHaveSpecs() {
    // Given
    ComponentTypeSymbol parentComp1 = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("A1")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();
    ComponentTypeSymbol parentComp2 = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("A2")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();

    CompKindExpression parentExpr1 = new CompKindOfComponentType(parentComp1);
    CompKindExpression parentExpr2 = new CompKindOfComponentType(parentComp2);

    ComponentTypeSymbolBuilder childBuilder = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("B")
        .setSpannedScope(CompSymbolsMill.scope())
        .setRefinementsList(List.of(parentExpr1, parentExpr2));

    // When
    ComponentTypeSymbol child = childBuilder.build();

    // Then
    assertEquals(2, child.sizeRefinements());
    assertAll(
        () -> assertEquals(parentExpr1, child.getRefinements(0)),
        () -> assertEquals(parentExpr2, child.getRefinements(1))
    );
  }

  @Test
  void shouldNotHaveSpecs() {
    // Given
    ComponentTypeSymbolBuilder childBuilder = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("A")
        .setSpannedScope(CompSymbolsMill.scope());

    // When
    ComponentTypeSymbol child = childBuilder.build();

    // Then
    assertTrue(child.isEmptyRefinements());
  }

  @ParameterizedTest
  @MethodSource("compNameAndParametersProvider")
  void shouldBuildWithExpectedParameters(String name, List<VariableSymbol> parameters) {
    ComponentTypeSymbol symbol = CompSymbolsMill.componentTypeSymbolBuilder().setName(name)
        .setSpannedScope(CompSymbolsMill.scope()).setParameterList(parameters).build();
    assertEquals(symbol.getName(), name);
    assertIterableEquals(parameters, symbol.getParameterList());
  }

  static Stream<Arguments> compNameAndParametersProvider() {
    return Stream.of(arguments("Comp1", Collections.emptyList()),
        arguments("Comp2", Arrays.asList(
            CompSymbolsMill.variableSymbolBuilder().setName("a").build(),
            CompSymbolsMill.variableSymbolBuilder().setName("b").build(),
            CompSymbolsMill.variableSymbolBuilder().setName("c").build())),
        arguments("Comp3", Arrays.asList(
            CompSymbolsMill.variableSymbolBuilder().setName("c").build(),
            CompSymbolsMill.variableSymbolBuilder().setName("d").build())));
  }

  @Test
  void shouldBuildWithExpectedNumberOfOptionalParameters() {
    // Given
    VariableSymbol symParamA = CompSymbolsMill.variableSymbolBuilder().setName("A").build();
    VariableSymbol symParamB = CompSymbolsMill.variableSymbolBuilder().setName("B").build();
    VariableSymbol symParamC = CompSymbolsMill.variableSymbolBuilder().setName("C").build();
    VariableSymbol symParamD = CompSymbolsMill.variableSymbolBuilder().setName("D").build();
    int numberOfOptionalParameters = 2;

    // When
    ComponentTypeSymbol symbol = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("A")
        .setParameterList(List.of(symParamA, symParamB, symParamC, symParamD))
        .setNumOptParams(numberOfOptionalParameters)
        .setSpannedScope(CompSymbolsMill.scope())
        .build();

    // Then
    assertEquals(2, symbol.getNumOptParams());
  }

  @ParameterizedTest
  @MethodSource("compNameAndTypeParametersProvider")
  void shouldBuildWithExpectedTypeParameters(String name,
                                             List<TypeVarSymbol> typeParameters) {
    ComponentTypeSymbol symbol = CompSymbolsMill.componentTypeSymbolBuilder().setName(name)
        .setSpannedScope(CompSymbolsMill.scope()).setTypeParameters(typeParameters).build();
    assertEquals(symbol.getName(), name);
    assertIterableEquals(symbol.getTypeParameters(), typeParameters);
  }

  static Stream<Arguments> compNameAndTypeParametersProvider() {
    return Stream.of(
        arguments("Comp1", Collections.emptyList()),
        arguments("Comp2", Arrays.asList(
            CompSymbolsMill.typeVarSymbolBuilder().setName("A").build(),
            CompSymbolsMill.typeVarSymbolBuilder().setName("B").build(),
            CompSymbolsMill.typeVarSymbolBuilder().setName("C").build())),
        arguments("Comp3", Collections.singletonList(
            CompSymbolsMill.typeVarSymbolBuilder().setName("D").build())));
  }
}
