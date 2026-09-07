/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbolSurrogate;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Holds test for {@link CompKindOfGenericComponentType}
 */
public class CompKindOfGenericComponentTypeTest {

  @BeforeEach
  public void setup() {
    CompSymbolsMill.reset();
    CompSymbolsMill.init();
    BasicSymbolsMill.initializePrimitives();
  }

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
    assertEquals(comp.getTypeInfo(), clone.getTypeInfo());
    assertNotSame(comp.getArguments(), clone.getArguments());
    assertIterableEquals(comp.getArguments(), clone.getArguments());
    assertNotSame(comp.getParamBindings(), clone.getParamBindings());
    assertIterableEquals(comp.getParamBindingsAsList(), clone.getParamBindingsAsList());
    assertEquals(comp.getSourceNode().isPresent(), clone.getSourceNode().isPresent());
    assertNotSame(comp.getTypeBindingsAsList(), clone.getTypeBindingsAsList());
  }

  @Test
  public void shouldGetParentComponent() {
    // Given
    ComponentTypeSymbol parent = createComponentWithTypeVar("Parent", "S");
    ComponentTypeSymbol component = CompSymbolsMill.componentTypeSymbolBuilder()
        .setName("Comp")
        .setSpannedScope(CompSymbolsMill.scope())
        .build();

    // Creating a typeExpr representing Parent<int> that is then set to be the parent of comp
    CompKindExpression parentTypeExpr = new CompKindOfGenericComponentType(parent,
        Lists.newArrayList(SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT)));
    component.setSuperComponentsList(Collections.singletonList(parentTypeExpr));

    CompKindOfComponentType compTypeExpr = new CompKindOfComponentType(component);

    // When && Then
    assertFalse(compTypeExpr.getSuperComponents().isEmpty());
    assertEquals(parentTypeExpr, compTypeExpr.getSuperComponents().get(0));
  }

  @Test
  public void shouldGetParentWithTypeVarPrimitive() {
    // Given
    ComponentTypeSymbol parent = createComponentWithTypeVar("Parent", "S");
    ComponentTypeSymbol child = createComponentWithTypeVar("Child", "T");

    SymTypeVariable typeVar = SymTypeExpressionFactory.createTypeVariable(child.getTypeParameters().get(0));
    child.setSuperComponentsList(Collections.singletonList(new CompKindOfGenericComponentType(parent, Lists.newArrayList(typeVar))));

    SymTypeExpression typeArg = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    CompKindExpression bChild = new CompKindOfGenericComponentType(child, Lists.newArrayList(typeArg));

    // When
    CompKindOfGenericComponentType bParent = ((CompKindOfGenericComponentType) bChild.getSuperComponents().get(0));

    // Then
    assertSame(parent, bParent.getTypeInfo());
    assertInstanceOf(CompKindOfGenericComponentType.class, bParent);
    assertTrue(bParent.getTypeBindingFor(parent.getTypeParameters().get(0)).isPresent());
    assertEquals(typeArg, bParent.getTypeBindingFor(parent.getTypeParameters().get(0)).get());
  }

  @Test
  public void shouldGetParentWithTypeVarObject() {
    // Given
    ComponentTypeSymbol parent = createComponentWithTypeVar("Parent", "S");
    ComponentTypeSymbol child = createComponentWithTypeVar("Child", "T");

    SymTypeVariable typeVar = SymTypeExpressionFactory.createTypeVariable(child.getTypeParameters().get(0));
    child.setSuperComponentsList(Collections.singletonList(new CompKindOfGenericComponentType(parent, Lists.newArrayList(typeVar))));

    SymTypeExpression typeArg = SymTypeExpressionFactory
        .createTypeObject(CompSymbolsMill.typeSymbolBuilder()
            .setName("First")
            .setSpannedScope(CompSymbolsMill.scope())
            .build()
        );
    CompKindExpression bChild = new CompKindOfGenericComponentType(child, Lists.newArrayList(typeArg));

    // When
    CompKindOfGenericComponentType bParent = ((CompKindOfGenericComponentType) bChild.getSuperComponents().get(0));

    // Then
    assertSame(parent, bParent.getTypeInfo());
    assertInstanceOf(CompKindOfGenericComponentType.class, bParent);
    assertTrue(bParent.getTypeBindingFor(parent.getTypeParameters().get(0)).isPresent());
    assertEquals(typeArg, bParent.getTypeBindingFor(parent.getTypeParameters().get(0)).get());
  }

  @Test
  public void shouldGetParentWithTypeVarObjects() {
    // Given
    ComponentTypeSymbol parent = createComponentWithTypeVar("Parent", "S", "T");
    ComponentTypeSymbol child = createComponentWithTypeVar("Child", "U", "V");

    SymTypeVariable typeVar1 = SymTypeExpressionFactory.createTypeVariable(child.getTypeParameters().get(0));
    SymTypeVariable typeVar2 = SymTypeExpressionFactory.createTypeVariable(child.getTypeParameters().get(1));
    child.setSuperComponentsList(Collections.singletonList(new CompKindOfGenericComponentType(parent, Lists.newArrayList(typeVar1, typeVar2))));

    SymTypeExpression typeArg1 = SymTypeExpressionFactory
        .createTypeObject(CompSymbolsMill.typeSymbolBuilder()
            .setName("First")
            .setSpannedScope(CompSymbolsMill.scope())
            .build());
    SymTypeExpression typeArg2 = SymTypeExpressionFactory
        .createTypeObject(CompSymbolsMill.typeSymbolBuilder()
            .setName("Second")
            .setSpannedScope(CompSymbolsMill.scope())
            .build());
    CompKindExpression bChild = new CompKindOfGenericComponentType(child, Lists.newArrayList(typeArg1, typeArg2));

    // When
    CompKindOfGenericComponentType bParent = ((CompKindOfGenericComponentType) bChild.getSuperComponents().get(0));

    // Then
    assertSame(parent, bParent.getTypeInfo());
    assertInstanceOf(CompKindOfGenericComponentType.class, bParent);
    assertTrue(bParent.getTypeBindingFor(parent.getTypeParameters().get(0)).isPresent());
    assertEquals(typeArg1, bParent.getTypeBindingFor(parent.getTypeParameters().get(0)).get());
    assertTrue(bParent.getTypeBindingFor(parent.getTypeParameters().get(1)).isPresent());
    assertEquals(typeArg2, bParent.getTypeBindingFor(parent.getTypeParameters().get(1)).get());
  }

  @Test
  public void shouldGetParentWithTypeVar() {
    // Given
    ComponentTypeSymbol parent = createComponentWithTypeVar("Parent", "S");
    ComponentTypeSymbol child = createComponentWithTypeVar("Child", "T");

    SymTypeVariable typeVar = SymTypeExpressionFactory.createTypeVariable(child.getTypeParameters().get(0));
    child.setSuperComponentsList(Collections.singletonList(new CompKindOfGenericComponentType(parent, Lists.newArrayList(typeVar))));

    TypeVarSymbol symbol = CompSymbolsMill.typeVarSymbolBuilder().setName("A").build();
    SymTypeExpression typeArg = SymTypeExpressionFactory.createTypeVariable(symbol);
    CompKindExpression bChild = new CompKindOfGenericComponentType(child, Lists.newArrayList(typeArg));

    // When
    CompKindOfGenericComponentType bParent = ((CompKindOfGenericComponentType) bChild.getSuperComponents().get(0));

    // Then
    assertSame(parent, bParent.getTypeInfo());
    assertInstanceOf(CompKindOfGenericComponentType.class, bParent);
    assertTrue(bParent.getTypeBindingFor(parent.getTypeParameters().get(0)).isPresent());
    assertEquals(typeArg, bParent.getTypeBindingFor(parent.getTypeParameters().get(0)).get());
  }

  protected static Stream<Arguments> compWithTypeParamAndOptionallySurrogateProvider() {
    Named<ComponentTypeSymbol> original = Named.of(
        "CompSymbol",
        createComponentWithTypeVar("Comp", "T")
    );
    Named<ComponentTypeSymbol> surrogate = Named.of(
        "CompSurrogate",
        createSurrogateInGlobalScopeFor(original.getPayload())
    );

    return Stream.of(
        Arguments.of(original, original),
        Arguments.of(original, surrogate)
    );
  }

  /**
   * @param symbolWithDefinitions    Provide a component type symbol in which ports will be added in this test.
   * @param symbolVersionForTypeExpr Set this to {@code symbolWithDefinitions}, or to a surrogate pointing to that
   *                                 symbol. This object will be used to create The ComponentTypeExpression.
   */
  @ParameterizedTest
  @MethodSource("compWithTypeParamAndOptionallySurrogateProvider")
  public void shouldGetTypeExprOfPortWithOwnTypeVarReplaced(@NonNull ComponentTypeSymbol symbolWithDefinitions,
                                                            @NonNull ComponentTypeSymbol symbolVersionForTypeExpr) {
    Preconditions.checkNotNull(symbolWithDefinitions);
    Preconditions.checkNotNull(symbolVersionForTypeExpr);

    // Given
    CompSymbolsMill.globalScope().add(symbolWithDefinitions);
    symbolWithDefinitions.setEnclosingScope(CompSymbolsMill.globalScope());
    symbolVersionForTypeExpr.setEnclosingScope(CompSymbolsMill.globalScope());

    TypeVarSymbol typeVar = symbolWithDefinitions.getTypeParameters().get(0);

    String portName = "port";
    PortSymbol port = CompSymbolsMill.portSymbolBuilder()
        .setName(portName)
        .setType(SymTypeExpressionFactory.createTypeVariable(typeVar))
        .setIncoming(true)
        .build();
    symbolWithDefinitions.getSpannedScope().add(port);

    SymTypeExpression intTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    CompKindOfGenericComponentType boundCompTypeExpr =
        new CompKindOfGenericComponentType(symbolVersionForTypeExpr, Lists.newArrayList(intTypeExpr));

    // When
    Optional<SymTypeExpression> portsType = boundCompTypeExpr.getTypeOfPort(portName);

    // Then
    assertTrue(portsType.isPresent(), "Port missing");
    assertInstanceOf(SymTypePrimitive.class, portsType.get());
    assertEquals(BasicSymbolsMill.INT, portsType.get().print());
  }

  @Test
  public void shouldGetTypeExprOfPortWithParentTypeVarReplaced() {
    // Given
    ComponentTypeSymbol parentCompDefinition = createComponentWithTypeVar("Parent", "S");
    TypeVarSymbol parentTypeVar = parentCompDefinition.getTypeParameters().get(0);
    String portName = "porr";
    PortSymbol port = CompSymbolsMill.portSymbolBuilder()
        .setName(portName)
        .setType(SymTypeExpressionFactory.createTypeVariable(parentTypeVar))
        .setIncoming(true)
        .build();
    parentCompDefinition.getSpannedScope().add(port);

    ComponentTypeSymbol compDefinition = createComponentWithTypeVar("Comp", "T");
    // bind parent's S with child's T to declare: Comp<T> extends Parent<T>
    TypeVarSymbol childTypeVar = compDefinition.getTypeParameters().get(0);
    SymTypeExpression childTypeVarExpr = SymTypeExpressionFactory.createTypeVariable(childTypeVar);
    CompKindExpression boundParentTypeExpr =
        new CompKindOfGenericComponentType(parentCompDefinition, Lists.newArrayList(childTypeVarExpr));
    compDefinition.setSuperComponentsList(Collections.singletonList(boundParentTypeExpr));

    // create CompTypeExpr representing Comp<int>
    SymTypeExpression intTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    CompKindOfGenericComponentType boundCompTypeExpr =
        new CompKindOfGenericComponentType(compDefinition, Lists.newArrayList(intTypeExpr));

    // When
    Optional<SymTypeExpression> portsType = boundCompTypeExpr.getTypeOfPort(portName);

    // Then
    assertTrue(portsType.isPresent());
    assertTrue(portsType.get() instanceof SymTypePrimitive);
    assertEquals(BasicSymbolsMill.INT, portsType.get().print());
  }

  /**
   * @param symbolWithDefinitions    Provide a component type symbol in which parameters will be added in this test.
   * @param symbolVersionForTypeExpr Set this to {@code symbolWithDefinitions}, or to a surrogate pointing to that
   *                                 symbol. This object will be used to create The ComponentTypeExpression.
   */
  @ParameterizedTest
  @MethodSource("compWithTypeParamAndOptionallySurrogateProvider")
  public void shouldGetTypeExprOfParameterWithOwnTypeVarReplaced(@NonNull ComponentTypeSymbol symbolWithDefinitions,
                                                                 @NonNull ComponentTypeSymbol symbolVersionForTypeExpr) {
    Preconditions.checkNotNull(symbolWithDefinitions);
    Preconditions.checkNotNull(symbolVersionForTypeExpr);

    // Given
    CompSymbolsMill.globalScope().add(symbolWithDefinitions);
    symbolWithDefinitions.setEnclosingScope(CompSymbolsMill.globalScope());
    symbolVersionForTypeExpr.setEnclosingScope(CompSymbolsMill.globalScope());

    TypeVarSymbol typeVar = symbolWithDefinitions.getTypeParameters().get(0);

    String paramName = "parr";
    VariableSymbol param = CompSymbolsMill.variableSymbolBuilder()
        .setName(paramName)
        .setType(SymTypeExpressionFactory.createTypeVariable(typeVar))
        .build();
    symbolWithDefinitions.getSpannedScope().add(param);
    symbolWithDefinitions.addParameter(param);

    SymTypeExpression intTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    CompKindOfGenericComponentType boundCompTypeExpr =
        new CompKindOfGenericComponentType(symbolVersionForTypeExpr, Lists.newArrayList(intTypeExpr));

    // When
    Optional<SymTypeExpression> paramTypeExpr = boundCompTypeExpr.getTypeOfParameter(paramName);

    // Then
    assertTrue(paramTypeExpr.isPresent(), "param missing");
    assertInstanceOf(SymTypePrimitive.class, paramTypeExpr.get());
    assertEquals(BasicSymbolsMill.INT, paramTypeExpr.get().print());
  }

  @Test
  public void shouldGetTypeExprOfParameterWithParentTypeVarReplaced() {
    // Given
    ComponentTypeSymbol parentCompDefinition = createComponentWithTypeVar("Parent", "S");
    TypeVarSymbol parentTypeVar = parentCompDefinition.getTypeParameters().get(0);
    String name = "parr";
    VariableSymbol paramOfParent = CompSymbolsMill.variableSymbolBuilder()
        .setName(name)
        .setType(SymTypeExpressionFactory.createTypeVariable(parentTypeVar))
        .build();
    parentCompDefinition.getSpannedScope().add(paramOfParent);
    parentCompDefinition.addParameter(paramOfParent);

    ComponentTypeSymbol compDefinition = createComponentWithTypeVar("Comp", "T");
    TypeVarSymbol childTypeVar = compDefinition.getTypeParameters().get(0);
    VariableSymbol paramOfComp = CompSymbolsMill.variableSymbolBuilder()
        .setName(name)
        .setType(SymTypeExpressionFactory.createTypeVariable(childTypeVar))
        .build();
    compDefinition.getSpannedScope().add(paramOfComp);
    compDefinition.addParameter(paramOfComp);
    // bind parent's S with child's T to declare: Comp<T> extends Parent<T>
    SymTypeExpression childTypeVarExpr = SymTypeExpressionFactory.createTypeVariable(childTypeVar);
    CompKindExpression boundParentTypeExpr =
        new CompKindOfGenericComponentType(parentCompDefinition, Lists.newArrayList(childTypeVarExpr));
    compDefinition.setSuperComponentsList(Collections.singletonList(boundParentTypeExpr));

    // create CompTypeExpr representing Comp<int>
    SymTypeExpression intTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    CompKindOfGenericComponentType boundCompTypeExpr =
        new CompKindOfGenericComponentType(compDefinition, Lists.newArrayList(intTypeExpr));

    // When
    Optional<SymTypeExpression> paramTypeExpr = boundCompTypeExpr.getTypeOfParameter(name);

    // Then
    assertTrue(paramTypeExpr.isPresent());
    assertTrue(paramTypeExpr.get() instanceof SymTypePrimitive);
    assertEquals(BasicSymbolsMill.INT, paramTypeExpr.get().print());
  }

  @Test
  public void shouldGetBindingsAsListInCorrectOrder() {
    // Given
    ComponentTypeSymbol comp = createComponentWithTypeVar("Comp", "A", "B", "C");

    SymTypeExpression floatTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.FLOAT);
    SymTypeExpression intTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    SymTypeExpression boolTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    List<SymTypeExpression> typeExprList = Lists.newArrayList(floatTypeExpr, intTypeExpr, boolTypeExpr);

    // When
    CompKindOfGenericComponentType compTypeExpr = new CompKindOfGenericComponentType(comp, typeExprList);

    // Then
    List<SymTypeExpression> returnedBindings = compTypeExpr.getTypeBindingsAsList();
    assertEquals(typeExprList, returnedBindings);
  }

  @Test
  public void shouldGetTypeParamBindingsSkippingSurrogate() {
    // Given
    ComponentTypeSymbol comp = createComponentWithTypeVar("Comp", "A", "B", "C");
    ComponentTypeSymbolSurrogate compSurrogate = CompSymbolsMill
        .componentTypeSymbolSurrogateBuilder()
        .setName("Comp")
        .setEnclosingScope(CompSymbolsMill.globalScope()).build();
    CompSymbolsMill.globalScope().add(comp);
    comp.setEnclosingScope(CompSymbolsMill.globalScope());


    SymTypeExpression floatTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.FLOAT);
    SymTypeExpression intTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.INT);
    SymTypeExpression boolTypeExpr = SymTypeExpressionFactory.createPrimitive(BasicSymbolsMill.BOOLEAN);
    List<SymTypeExpression> typeExprList = Lists.newArrayList(floatTypeExpr, intTypeExpr, boolTypeExpr);

    // When
    CompKindOfGenericComponentType compTypeExpr = new CompKindOfGenericComponentType(compSurrogate, typeExprList);

    // Then
    assertAll(
        () -> assertEquals(floatTypeExpr, compTypeExpr.getTypeBindingFor("A").orElseThrow()),
        () -> assertEquals(intTypeExpr, compTypeExpr.getTypeBindingFor("B").orElseThrow()),
        () -> assertEquals(boolTypeExpr, compTypeExpr.getTypeBindingFor("C").orElseThrow()),
        () -> assertEquals(3, compTypeExpr.getTypeVarBindings().size())
    );

  }

  /**
   * Beware that the created symbol is not enclosed by any scope yet.
   */
  protected static ComponentTypeSymbol createComponentWithTypeVar(@NonNull String compName,
                                                                  @NonNull String... typeVarNames) {
    Preconditions.checkNotNull(compName);
    Preconditions.checkNotNull(typeVarNames);

    List<TypeVarSymbol> typeVars = new ArrayList<>(typeVarNames.length);
    for (String typeVarName : typeVarNames) {
      TypeVarSymbol typeVar = CompSymbolsMill.typeVarSymbolBuilder()
          .setName(typeVarName)
          .build();
      typeVars.add(typeVar);
    }

    return CompSymbolsMill.componentTypeSymbolBuilder()
        .setName(compName)
        .setSpannedScope(CompSymbolsMill.scope())
        .setTypeParameters(typeVars)
        .build();
  }

  /**
   * Beware that the created surrogate is not enclosed by any scope yet.
   */
  protected static ComponentTypeSymbol createSurrogateInGlobalScopeFor(@NonNull ComponentTypeSymbol original) {
    Preconditions.checkNotNull(original);

    return CompSymbolsMill
        .componentTypeSymbolSurrogateBuilder()
        .setName(original.getFullName())
        .setEnclosingScope(CompSymbolsMill.globalScope())
        .build();
  }
}
