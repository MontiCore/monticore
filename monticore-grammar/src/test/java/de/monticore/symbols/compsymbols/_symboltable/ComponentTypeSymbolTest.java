/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.types.check.CompKindOfComponentType;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Holds tests for the handwritten methods of {@link ComponentTypeSymbol}.
 */
public class ComponentTypeSymbolTest {

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

  @Test
  public void shouldStateIfHasParameters() {
    // Given
    ComponentTypeSymbol compWithoutParameters = CompSymbolsMill.componentTypeSymbolBuilder().setName("Comp1")
        .setSpannedScope(CompSymbolsMill.scope()).build();
    ComponentTypeSymbol compWithParameters = CompSymbolsMill.componentTypeSymbolBuilder().setName("Comp2")
        .setSpannedScope(CompSymbolsMill.scope()).build();
    List<VariableSymbol> params = Arrays.asList(
        CompSymbolsMill.variableSymbolBuilder().setName("first").build(),
        CompSymbolsMill.variableSymbolBuilder().setName("second").build(),
        CompSymbolsMill.variableSymbolBuilder().setName("third").build()
    );

    // When
    params.forEach(compWithParameters.getSpannedScope()::add);
    compWithParameters.addAllParameter(params);

    // Then
    Assertions.assertFalse(compWithoutParameters.hasParameters());
    Assertions.assertTrue(compWithParameters.hasParameters());
    Assertions.assertEquals(3, compWithParameters.getParameterList().size());
  }

  @Test
  public void shouldReturnParametersIfPresent() {
    // Given
    ComponentTypeSymbol compWithoutParameters = CompSymbolsMill.componentTypeSymbolBuilder().setName("Comp1")
        .setSpannedScope(CompSymbolsMill.scope()).build();
    ComponentTypeSymbol compWithParameters = CompSymbolsMill.componentTypeSymbolBuilder().setName("Comp2")
        .setSpannedScope(CompSymbolsMill.scope()).build();
    List<VariableSymbol> params = Arrays.asList(
        CompSymbolsMill.variableSymbolBuilder().setName("first").build(),
        CompSymbolsMill.variableSymbolBuilder().setName("second").build(),
        CompSymbolsMill.variableSymbolBuilder().setName("third").build()
    );

    // When
    params.forEach(compWithParameters.getSpannedScope()::add);
    compWithParameters.addAllParameter(params);

    // Then
    for (VariableSymbol param : params) {
      Assertions.assertTrue(compWithParameters.getParameter(param.getName()).isPresent());
      Assertions.assertFalse(compWithoutParameters.getParameter(param.getName()).isPresent());
    }
  }

  @Test
  public void shouldStateIfHasTypeParameters() {
    // Given
    ComponentTypeSymbol compWithoutTypeParameters = CompSymbolsMill.componentTypeSymbolBuilder().setName("Comp1")
        .setSpannedScope(CompSymbolsMill.scope()).build();
    ComponentTypeSymbol compWithTypeParameters = CompSymbolsMill.componentTypeSymbolBuilder().setName("Comp2")
        .setSpannedScope(CompSymbolsMill.scope()).build();
    List<TypeVarSymbol> typeParams = Arrays.asList(
        CompSymbolsMill.typeVarSymbolBuilder().setName("first").build(),
        CompSymbolsMill.typeVarSymbolBuilder().setName("second").build(),
        CompSymbolsMill.typeVarSymbolBuilder().setName("third").build()
    );
    typeParams.forEach(compWithTypeParameters.getSpannedScope()::add);

    // When & Then
    Assertions.assertFalse(compWithoutTypeParameters.hasTypeParameter());
    Assertions.assertTrue(compWithTypeParameters.hasTypeParameter());
  }

  @ParameterizedTest
  @MethodSource("portNameAndDirectionProvider")
  public void shouldReturnIncomingPortsOnly(HashMap<String, Boolean> ports) {
    ComponentTypeSymbol symbol = buildTestComponentWithPorts(ports);
    Assertions.assertIterableEquals(ports.entrySet().stream()
            .filter(p -> p.getValue().equals(true)).map(Map.Entry::getKey).collect(Collectors.toList()),
        symbol.getIncomingPorts().stream().map(PortSymbol::getName).collect(Collectors.toList()));
  }

  @ParameterizedTest
  @MethodSource("portNameAndDirectionProvider")
  public void shouldReturnOutgoingPortsOnly(HashMap<String, Boolean> ports) {
    ComponentTypeSymbol symbol = buildTestComponentWithPorts(ports);
    Assertions.assertIterableEquals(ports.entrySet().stream()
            .filter(p -> p.getValue().equals(false)).map(Map.Entry::getKey).collect(Collectors.toList()),
        symbol.getOutgoingPorts().stream().map(PortSymbol::getName).collect(Collectors.toList()));
  }

  @ParameterizedTest
  @MethodSource("portNameAndDirectionProvider")
  public void shouldFindPortWithExpectedDirection(HashMap<String, Boolean> ports) {
    ComponentTypeSymbol symbol = buildTestComponentWithPorts(ports);
    for (String port : ports.keySet()) {
      if (ports.get(port)) {
        Assertions.assertTrue(symbol.getIncomingPort(port).isPresent());
        Assertions.assertFalse(symbol.getOutgoingPort(port).isPresent());
      } else {
        Assertions.assertFalse(symbol.getIncomingPort(port).isPresent());
        Assertions.assertTrue(symbol.getOutgoingPort(port).isPresent());
      }
    }
  }

  @ParameterizedTest
  @MethodSource("portNameAndDirectionProvider")
  public void shouldStateCorrectlyIFHasPorts(HashMap<String, Boolean> ports) {
    ComponentTypeSymbol symbol = buildTestComponentWithPorts(ports);
    if (ports.isEmpty()) {
      Assertions.assertFalse(symbol.hasPorts());
    } else {
      Assertions.assertTrue(symbol.hasPorts());
    }
  }

  static Stream<Arguments> portNameAndDirectionProvider() {
    HashMap<String, Boolean> ports1 = new HashMap<>();
    HashMap<String, Boolean> ports2 = new HashMap<>();
    ports2.put("o1", false);
    ports2.put("o2", false);
    HashMap<String, Boolean> ports3 = new HashMap<>();
    ports3.put("i1", true);
    ports3.put("i2", true);
    HashMap<String, Boolean> ports4 = new HashMap<>();
    ports4.put("i1", true);
    ports4.put("o1", false);
    ports4.put("i2", true);
    ports4.put("o2", false);
    return Stream.of(arguments(ports1), arguments(ports2), arguments(ports3), arguments(ports4));
  }

  private ComponentTypeSymbol buildTestComponentWithPorts(HashMap<String, Boolean> ports) {
    ComponentTypeSymbol compSymbol = CompSymbolsMill.componentTypeSymbolBuilder().setName("Comp")
        .setSpannedScope(CompSymbolsMill.scope()).build();
    for (String port : ports.keySet()) {
      PortSymbol portSymbol = CompSymbolsMill.portSymbolBuilder()
          .setName(port).setType(SymTypeExpressionFactory.createObscureType()).setIncoming(ports.get(port)).setOutgoing(!ports.get(port)).build();
      compSymbol.getSpannedScope().add(portSymbol);
    }
    return compSymbol;
  }

  @ParameterizedTest
  @MethodSource("instanceNamesProvider")
  public void shouldFindSubComponents(List<String> instances) {
    ComponentTypeSymbol symbol = builtTestComponentWithInstances(instances);
    Assertions.assertEquals(symbol.getSubcomponents().size(), instances.size());
    Assertions.assertIterableEquals(symbol.getSubcomponents()
        .stream().map(SubcomponentSymbol::getName).collect(Collectors.toList()), instances);
  }

  static Stream<Arguments> instanceNamesProvider() {
    return Stream.of(
        arguments(Collections.emptyList()),
        arguments(Arrays.asList("sub1", "sub2", "sub3")));
  }

  @Test
  public void shouldFindExpectedSubComponent() {
    List<String> instances = Arrays.asList("sub1", "sub2", "sub3");
    ComponentTypeSymbol symbol = this.builtTestComponentWithInstances(instances);
    for (String instance : instances) {
      Assertions.assertTrue(symbol.getSubcomponents(instance).isPresent());
      Assertions.assertEquals(symbol.getSubcomponents(instance).get().getName(), instance);
    }
  }

  @Test
  public void shouldNotFindUnexpectedSubComponent() {
    ComponentTypeSymbol symbol1 = this.builtTestComponentWithInstances(Collections.emptyList());
    ComponentTypeSymbol symbol2 = this.builtTestComponentWithInstances(
        Arrays.asList("sub1", "sub2", "sub3"));
    Assertions.assertFalse(symbol1.getSubcomponents("sub4").isPresent());
    Assertions.assertFalse(symbol2.getSubcomponents("sub4").isPresent());
  }

  @Test
  void shouldBeAtomicOrDecomposed() {
    ComponentTypeSymbol composedComponent =
        builtTestComponentWithInstances(Arrays.asList("a", "b", "c"));
    ComponentTypeSymbol atomicComponent =
        builtTestComponentWithInstances(Collections.emptyList());
    Assertions.assertTrue(composedComponent.isDecomposed());
    Assertions.assertFalse(composedComponent.isAtomic());
    Assertions.assertFalse(atomicComponent.isDecomposed());
    Assertions.assertTrue(atomicComponent.isAtomic());
  }

  private ComponentTypeSymbol builtTestComponentWithInstances(List<String> instances) {
    ComponentTypeSymbol compSymbol = CompSymbolsMill.componentTypeSymbolBuilder().setName("Comp")
        .setSpannedScope(CompSymbolsMill.scope()).build();
    for (String instance : instances) {
      SubcomponentSymbol subCompSymbol = CompSymbolsMill.subcomponentSymbolBuilder()
          .setName(instance)
          .setType(new CompKindOfComponentType(
              CompSymbolsMill.componentTypeSymbolSurrogateBuilder()
                  .setName("empty")
                  .setEnclosingScope(compSymbol.getSpannedScope())
                  .build()))
          .build();
      compSymbol.getSpannedScope().add(subCompSymbol);
    }
    return compSymbol;
  }
}