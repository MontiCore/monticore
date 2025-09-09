/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Holds test for {@link CompKindOfComponentType}
 */
public class CompKindOfComponentTypeTest {

  @BeforeEach
  public void setup() {
    CompSymbolsMill.reset();
    CompSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

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

  /**
   * Method under test {@link CompKindOfComponentType#getSuperComponents()}
   */
  @Test
  public void getParentShouldReturnExpected() {
    // Given
    ComponentTypeSymbol symbolWithDefinitions = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("Comp")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();

    ComponentTypeSymbol symbolVersionForTypeExpr = CompSymbolsMill
        .componentTypeSymbolSurrogateBuilder()
        .setName(symbolWithDefinitions.getFullName())
        .setEnclosingScope(CompSymbolsMill.globalScope())
        .build();

    // Given
    CompSymbolsMill.globalScope().add(symbolWithDefinitions);
    symbolWithDefinitions.setEnclosingScope(CompSymbolsMill.globalScope());

    ComponentTypeSymbol parent = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("Parent")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();
    CompKindOfComponentType parentTypeExpr = new CompKindOfComponentType(parent);

    symbolWithDefinitions.setSuperComponentsList(Collections.singletonList(parentTypeExpr));
    CompKindOfComponentType compTypeExpr = new CompKindOfComponentType(symbolVersionForTypeExpr);

    // When
    List<CompKindExpression> parentOfTypeExpr = compTypeExpr.getSuperComponents();

    // Then
    Assertions.assertFalse(parentOfTypeExpr.isEmpty(), "Parent not present.");
    Assertions.assertEquals(parentTypeExpr, parentOfTypeExpr.get(0));
  }

  /**
   * Method under test {@link CompKindOfComponentType#getSuperComponents()}
   */
  @Test
  public void getParentShouldReturnOptionalEmpty() {
    // Given
    ComponentTypeSymbol component = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("Comp")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();
    CompKindOfComponentType compTypeExpr = new CompKindOfComponentType(component);

    // When
    List<CompKindExpression> parentOfTypeExpr = compTypeExpr.getSuperComponents();

    // Then
    Assertions.assertTrue(parentOfTypeExpr.isEmpty());
  }

  @Test
  public void shouldGetTypeExprOfPort() {
    // Given
    ComponentTypeSymbol symbolWithDefinitions = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("Comp")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();

    ComponentTypeSymbol symbolVersionForTypeExpr = CompSymbolsMill
        .componentTypeSymbolSurrogateBuilder()
        .setName(symbolWithDefinitions.getFullName())
        .setEnclosingScope(CompSymbolsMill.globalScope())
        .build();

    // Given
    CompSymbolsMill.globalScope().add(symbolWithDefinitions);
    symbolWithDefinitions.setEnclosingScope(CompSymbolsMill.globalScope());

    String portName = "port";
    PortSymbol port = CompSymbolsMill.portSymbolBuilder()
        .setName(portName)
        .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
        .setIncoming(true)
        .build();
    symbolWithDefinitions.getSpannedScope().add(port);

    CompKindOfComponentType compTypeExpr = new CompKindOfComponentType(symbolVersionForTypeExpr);

    // When
    Optional<SymTypeExpression> portsType = compTypeExpr.getTypeOfPort(portName);

    // Then
    Assertions.assertTrue(portsType.isPresent(), "Port not present");
    Assertions.assertInstanceOf(SymTypePrimitive.class, portsType.get());
    Assertions.assertEquals(BasicSymbolsMill.INT, portsType.get().print());
  }

  @Test
  public void shouldGetTypeExprOfInheritedPort() {
    // Given
    ComponentTypeSymbol parent = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("Parent")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();
    String portName = "port";
    PortSymbol port = CompSymbolsMill.portSymbolBuilder()
        .setName(portName)
        .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
        .setIncoming(true)
        .build();
    parent.getSpannedScope().add(port);

    ComponentTypeSymbol component = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("Comp")
        .setSuperComponentsList(Collections.singletonList(new CompKindOfComponentType(parent)))
        .setSpannedScope(CompSymbolsMill.scope())
        .build();

    CompKindOfComponentType compTypeExpr = new CompKindOfComponentType(component);

    // When
    Optional<SymTypeExpression> portsType = compTypeExpr.getTypeOfPort(portName);

    // Then
    Assertions.assertTrue(portsType.isPresent());
    Assertions.assertTrue(portsType.get() instanceof SymTypePrimitive);
    Assertions.assertEquals(BasicSymbolsMill.INT, portsType.get().print());
  }

  @Test
  public void shouldGetTypeExprOfParameter() {
    // Given
    ComponentTypeSymbol symbolWithDefinitions = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("Comp")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();

    ComponentTypeSymbol symbolVersionForTypeExpr = CompSymbolsMill
        .componentTypeSymbolSurrogateBuilder()
        .setName(symbolWithDefinitions.getFullName())
        .setEnclosingScope(CompSymbolsMill.globalScope())
        .build();

    // Given
    CompSymbolsMill.globalScope().add(symbolWithDefinitions);
    symbolWithDefinitions.setEnclosingScope(CompSymbolsMill.globalScope());

    String paramName = "para";
    VariableSymbol param = CompSymbolsMill.variableSymbolBuilder()
        .setName(paramName)
        .setType(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT))
        .build();
    symbolWithDefinitions.getSpannedScope().add(param);
    symbolWithDefinitions.addParameter(param);

    CompKindOfComponentType compTypeExpr = new CompKindOfComponentType(symbolVersionForTypeExpr);

    // When
    Optional<SymTypeExpression> paramType = compTypeExpr.getTypeOfParameter(paramName);

    // Then
    Assertions.assertTrue(paramType.isPresent(), "Param not present");
    Assertions.assertInstanceOf(SymTypePrimitive.class, paramType.get());
    Assertions.assertEquals(BasicSymbolsMill.INT, paramType.get().print());
  }
}
