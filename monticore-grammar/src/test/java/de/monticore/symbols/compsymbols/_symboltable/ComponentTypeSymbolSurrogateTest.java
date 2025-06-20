/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symboltable.modifiers.BasicAccessModifier;
import de.monticore.types.check.CompKindExpression;
import de.monticore.types.check.CompKindOfComponentType;
import de.monticore.types.check.SymTypeExpressionFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ComponentTypeSymbolSurrogateTest {
  
  @Test
  public void setSpannedScopeShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ICompSymbolsScope scopeToSet = CompSymbolsMill.scope();

    // When
    surrogate.setSpannedScope(scopeToSet);

    // Then
    Assertions.assertSame(scopeToSet, comp.getSpannedScope());
  }

  @Test
  public void getSpannedScopeShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    // When
    ICompSymbolsScope scope = surrogate.getSpannedScope();

    // Then
    Assertions.assertSame(comp.getSpannedScope(), scope);
  }

  @Test
  public void getPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  public void getPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    Optional<PortSymbol> portOpt = surrogate.getPort("myPort");

    // Then
    Assertions.assertTrue(portOpt.isPresent(), "Port is not present");
    Assertions.assertSame(port, portOpt.get());
  }

  @Test
  public void getInheritedPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol parent = CompSymbolsMill.componentTypeSymbolBuilder()
      .setName("Parent")
      .setSpannedScope(CompSymbolsMill.scope())
      .build();
    comp.setSuperComponentsList(Collections.singletonList(new CompKindOfComponentType(parent)));

    PortSymbol port = addIncomingPortTo(parent, "parentPort");

    // When
    Optional<PortSymbol> portOpt = surrogate.getPort("parentPort", true);

    // Then
    Assertions.assertTrue(portOpt.isPresent(), "Port is not present");
    Assertions.assertSame(port, portOpt.get());
  }

  @Test
  public void getIncomingPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getIncomingPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  public void getIncomingPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    Optional<PortSymbol> portOpt = surrogate.getIncomingPort("myPort");

    // Then
    Assertions.assertTrue(portOpt.isPresent(), "Port is not present");
    Assertions.assertSame(port, portOpt.get());
  }

  @Test
  public void getInheritedIncomingPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    comp.setSuperComponentsList(Collections.singletonList(new CompKindOfComponentType(parent)));

    PortSymbol port = addIncomingPortTo(parent, "parentPort");

    // When
    Optional<PortSymbol> portOpt = surrogate.getIncomingPort("parentPort", true);

    // Then
    Assertions.assertTrue(portOpt.isPresent(), "Port is not present");
    Assertions.assertSame(port, portOpt.get());
  }
  
  @Test
  void isPresentParentShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    comp.setSuperComponentsList(Collections.singletonList(new CompKindOfComponentType(parent)));

    // When
    boolean parentIsPresent = !surrogate.isEmptySuperComponents();

    // Then
    Assertions.assertTrue(parentIsPresent, "No parent present");
  }

  
  @Test
  void getParentShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    CompKindExpression parentExpr = new CompKindOfComponentType(parent);
    comp.setSuperComponentsList(Collections.singletonList(parentExpr));

    // When
    CompKindExpression parentCalculated = surrogate.getSuperComponents(0);

    // Then
    Assertions.assertSame(parentExpr, parentCalculated);
  }

  
  @Test
  void setParentShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    CompKindExpression parentExpr = new CompKindOfComponentType(parent);

    // When
    surrogate.setSuperComponentsList(Collections.singletonList(parentExpr));

    // Then
    Assertions.assertSame(parentExpr, comp.getSuperComponents(0));
  }

  @Test
  void isPresentRefinementShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair = createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol abstraction = createCompWithSurrogate("Abstraction").getKey();
    CompKindExpression abstractionExpr = new CompKindOfComponentType(abstraction);
    comp.setRefinementsList(Collections.singletonList(abstractionExpr));

    // When
    boolean refinedCompIsPresent = !surrogate.isEmptyRefinements();

    // Then
    Assertions.assertTrue(refinedCompIsPresent, "No refined component present");
  }


  @Test
  void getRefinementShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair = createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol abstraction = createCompWithSurrogate("Abstraction").getKey();
    CompKindExpression abstractionExpr = new CompKindOfComponentType(abstraction);
    comp.setRefinementsList(Collections.singletonList(abstractionExpr));

    // When
    CompKindExpression parentCalculated = surrogate.getRefinements(0);

    // Then
    Assertions.assertSame(abstractionExpr, parentCalculated);
  }


  @Test
  void setRefinementShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair = createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol abstraction = createCompWithSurrogate("Abstraction").getKey();
    CompKindExpression abstractionExpr = new CompKindOfComponentType(abstraction);

    // When
    surrogate.setRefinementsList(Collections.singletonList(abstractionExpr));

    // Then
    Assertions.assertSame(abstractionExpr, comp.getRefinements(0));
  }
  
  @Test
  void getParameterListShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol param = addParameterTo(comp, "myParam");

    // When
    List<VariableSymbol> params = surrogate.getParameterList();

    // Then
    Assertions.assertArrayEquals(new VariableSymbol[] {param}, params.toArray());
  }

  @Test
  void getParameterShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol param = addParameterTo(comp, "myParam");

    // When
    Optional<VariableSymbol> paramOpt = surrogate.getParameter("myParam");

    // Then
    Assertions.assertTrue(paramOpt.isPresent(), "No parameter");
    Assertions.assertSame(param, paramOpt.get());
  }

  @Test
  void addParameterShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol param = CompSymbolsMill
      .variableSymbolBuilder()
      .setName("param")
      .setType(SymTypeExpressionFactory.createObscureType())
      .build();

    // When
    surrogate.getSpannedScope().add(param);
    surrogate.addParameter(param);

    // Then
    Assertions.assertArrayEquals(new VariableSymbol[] {param}, comp.getParameterList().toArray());
  }


  @Test
  void addParametersShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol param = CompSymbolsMill
      .variableSymbolBuilder()
      .setName("param")
      .setType(SymTypeExpressionFactory.createObscureType())
      .build();

    // When
    surrogate.getSpannedScope().add(param);
    surrogate.addAllParameter(Collections.singletonList(param));

    // Then
    Assertions.assertArrayEquals(new VariableSymbol[] {param}, comp.getParameterList().toArray());
  }

  @Test
  void hasParametersShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    addParameterTo(comp, "param");

    // When
    boolean hasParameters = surrogate.hasParameters();

    // Then
    Assertions.assertTrue(hasParameters, "No parameters found");
  }

  @Test
  void getTypeParametersShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    TypeVarSymbol typeParam = addTypeParameterTo(comp, "T");

    // When
    List<TypeVarSymbol> typeParams = surrogate.getTypeParameters();

    // Then
    Assertions.assertArrayEquals(new TypeVarSymbol[] {typeParam}, typeParams.toArray());
  }

  @Test
  void hasTypeParameterShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    addTypeParameterTo(comp, "T");

    // When
    boolean hasTypeParams = surrogate.hasTypeParameter();

    // then
    Assertions.assertTrue(hasTypeParams, "No type parameters found");
  }

  @Test
  void getFieldsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol field = addFieldTo(comp, "myField");

    // When
    List<VariableSymbol> fields = surrogate.getFields();

    // Then
    Assertions.assertArrayEquals(new VariableSymbol[] {field}, fields.toArray());
  }

  @Test
  void getFieldByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    VariableSymbol field = addFieldTo(comp, "myField");

    // When
    Optional<VariableSymbol> fieldOpt = surrogate.getField("myField");


    // Then
    Assertions.assertTrue(fieldOpt.isPresent(), "No field");
    Assertions.assertSame(field, fieldOpt.get());
  }

  @Test
  void getOutgoingPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addOutgoingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getOutgoingPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getOutgoingPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addOutgoingPortTo(comp, "myPort");

    // When
    Optional<PortSymbol> portOpt = surrogate.getOutgoingPort("myPort");

    // Then
    Assertions.assertTrue(portOpt.isPresent(), "Port is not present");
    Assertions.assertSame(port, portOpt.get());
  }

  @Test
  void getInheritedOutgoingPortByNameShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    ComponentTypeSymbol parent = createCompWithSurrogate("Parent").getKey();
    CompKindExpression parentExpr = new CompKindOfComponentType(parent);
    comp.setSuperComponentsList(Collections.singletonList(parentExpr));

    PortSymbol port = addOutgoingPortTo(parent, "myPort");

    // When
    Optional<PortSymbol> portOpt = surrogate.getOutgoingPort("myPort", true);

    // Then
    Assertions.assertTrue(portOpt.isPresent(), "Port is not present");
    Assertions.assertSame(port, portOpt.get());
  }

  @Test
  void getPortsWithDirectionShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();


    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getPorts(true, false);

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getAllIncomingPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    Set<PortSymbol> ports = surrogate.getAllIncomingPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getAllOutgoingPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addOutgoingPortTo(comp, "myPort");

    // When
    List<PortSymbol> ports = surrogate.getOutgoingPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getAllPortsWithDirectionShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addIncomingPortTo(comp, "myPort");

    // When
    Set<PortSymbol> ports = surrogate.getAllPorts(true, false);

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getAllPortsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    PortSymbol port = addOutgoingPortTo(comp, "myPort");

    // When
    Set<PortSymbol> ports = surrogate.getAllPorts();

    // Then
    Assertions.assertArrayEquals(new PortSymbol[] {port}, ports.toArray());
  }

  @Test
  void getSubComponentsShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    SubcomponentSymbol sub = addSubComponentTo(comp, "sub");

    // When
    List<SubcomponentSymbol> subs = surrogate.getSubcomponents();

    // Then
    Assertions.assertArrayEquals(new SubcomponentSymbol[] {sub}, subs.toArray());
  }

  @Test
  void getSubComponentShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    SubcomponentSymbol sub = addSubComponentTo(comp, "mySub");

    // When
    Optional<SubcomponentSymbol> subOpt = surrogate.getSubcomponents("mySub");

    // Then
    Assertions.assertTrue(subOpt.isPresent(), "Sub component is not present");
    Assertions.assertSame(sub, subOpt.get());
  }


  @Test
  void isDecomposedShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    addSubComponentTo(comp, "sub");

    // When
    boolean isDecomposed = surrogate.isDecomposed();

    // Then
    Assertions.assertTrue(isDecomposed, "Should be decomposed");
  }

  @Test
  void isAtomicShouldSkipSurrogate() {
    // Given
    Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> pair =  createCompWithSurrogate("Comp");
    ComponentTypeSymbol comp = pair.getKey();
    ComponentTypeSymbolSurrogate surrogate = pair.getValue();

    addSubComponentTo(comp, "sub");

    // When
    boolean isAtomic = surrogate.isAtomic();

    // Then
    Assertions.assertFalse(isAtomic, "Should not be atomic");
  }

  /**
   * Adds an incoming port symbol to the spanned scope of the component. The port type is only mocked.
   * @return the created Port
   */
  protected PortSymbol addIncomingPortTo(@NonNull ComponentTypeSymbol compType, @NonNull String portName) {
    return addPortTo(compType, portName, true);
  }

  /**
   * Adds an outgoing port symbol to the spanned scope of the component. The port type is only mocked.
   * @return the created Port
   */
  protected PortSymbol addOutgoingPortTo(@NonNull ComponentTypeSymbol compType, @NonNull String portName) {
    return addPortTo(compType, portName, false);
  }

  /**
   * Adds a port symbol to the spanned scope of the component. The port type is only mocked.
   * @return the created Port
   */
  protected PortSymbol addPortTo(@NonNull ComponentTypeSymbol compType, @NonNull String portName, boolean isIncoming) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(portName);

    PortSymbol port = CompSymbolsMill
      .portSymbolBuilder()
      .setName(portName)
      .setIncoming(isIncoming)
      .setOutgoing(!isIncoming)
      .setType(SymTypeExpressionFactory.createObscureType())
      .setAccessModifier(BasicAccessModifier.PUBLIC)
      .build();

    compType.getSpannedScope().add(port);

    return port;
  }

  /**
   * Adds a subcomponent to the spanned scope of the component. The sub component's type type is only mocked.
   * @return the created subcomponent
   */
  protected SubcomponentSymbol addSubComponentTo(@NonNull ComponentTypeSymbol compType, @NonNull String subCompName) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(subCompName);

    SubcomponentSymbol subComp = CompSymbolsMill
      .subcomponentSymbolBuilder()
      .setName(subCompName)
      .setType(new CompKindOfComponentType(CompSymbolsMill.componentTypeSymbolSurrogateBuilder().setName("empty").setEnclosingScope(compType.getSpannedScope()).build()))
      .setAccessModifier(BasicAccessModifier.PUBLIC)
      .build();

    compType.getSpannedScope().add(subComp);

    return subComp;
  }

  /**
   * Adds an inner component type symbol to the spanned scope of the component.
   * @return the created inner component type
   */
  protected ComponentTypeSymbol addInnerComponentTypeTo(@NonNull ComponentTypeSymbol compType, @NonNull String innerCompTypeName) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(innerCompTypeName);

    ComponentTypeSymbol innerComp =  CompSymbolsMill
      .componentTypeSymbolBuilder()
      .setName(innerCompTypeName)
      .setSpannedScope(CompSymbolsMill.scope())
      .setAccessModifier(BasicAccessModifier.PUBLIC)
      .build();

    compType.getSpannedScope().add(innerComp);

    return innerComp;
  }

  /**
   * Adds a field to the spanned scope of the component. The field type is only mocked.
   * @return the created field.
   */
  protected VariableSymbol addFieldTo(@NonNull ComponentTypeSymbol compType, @NonNull String fieldName) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(fieldName);

    VariableSymbol field = CompSymbolsMill
      .variableSymbolBuilder()
      .setName(fieldName)
      .setType(SymTypeExpressionFactory.createObscureType())
      .setAccessModifier(BasicAccessModifier.PUBLIC)
      .build();

    compType.getSpannedScope().add(field);

    return field;
  }

  /**
   * Adds a parameter to the spanned scope of the component. The parameter type is only mocked.
   * @return the created parameter.
   */
  protected VariableSymbol addParameterTo(@NonNull ComponentTypeSymbol compType, @NonNull String paramName) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(paramName);

    VariableSymbol param = CompSymbolsMill
      .variableSymbolBuilder()
      .setName(paramName)
      .setType(SymTypeExpressionFactory.createObscureType())
      .setAccessModifier(BasicAccessModifier.PUBLIC)
      .build();

    compType.getSpannedScope().add(param);
    compType.addParameter(param);

    return param;
  }

  /**
   * Adds a type parameter to the component.
   * @return the created type parameter
   */
  protected TypeVarSymbol addTypeParameterTo(@NonNull ComponentTypeSymbol compType,
                                             @NonNull String typeParamName) {
    Preconditions.checkNotNull(compType);
    Preconditions.checkNotNull(typeParamName);

    TypeVarSymbol typeVar = CompSymbolsMill
      .typeVarSymbolBuilder()
      .setName(typeParamName)
      .setAccessModifier(BasicAccessModifier.PUBLIC)
      .build();

    compType.getSpannedScope().add(typeVar);

    return typeVar;
  }

  protected static Map.Entry<ComponentTypeSymbol, ComponentTypeSymbolSurrogate> createCompWithSurrogate(
    @NonNull String compName) {
    Preconditions.checkNotNull(compName);

    ICompSymbolsScope commonScope = CompSymbolsMill.scope();

    ComponentTypeSymbol symbol = CompSymbolsMill.componentTypeSymbolBuilder()
      .setName(compName)
      .setSpannedScope(CompSymbolsMill.scope())
      .build();

    commonScope.add(symbol);

    ComponentTypeSymbolSurrogate surrogate = CompSymbolsMill.componentTypeSymbolSurrogateBuilder()
      .setName(compName)
      .setEnclosingScope(commonScope)
      .build();

    return Map.entry(symbol, surrogate);
  }
}
