/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols._symboltable.ComponentTypeSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Represents a component expression that is solely defined by the component symbol.
 */
public class CompKindOfComponentType extends CompKindExpression {

  public CompKindOfComponentType(@NonNull ComponentTypeSymbol component) {
    super(component);
  }

  @Override
  public String printName() {
    return this.getTypeInfo().getName();
  }

  @Override
  public String printFullName() {
    return this.getTypeInfo().getFullName();
  }

  @Override
  public boolean isComponentType() {
    return true;
  }

  @Override
  public CompKindOfComponentType asComponentType() {
    return this;
  }

  @Override
  public List<CompKindExpression> getSuperComponents() {
    return this.getTypeInfo().getSuperComponentsList();
  }

  @Override
  public Optional<SymTypeExpression> getTypeOfPort(@NonNull String portName) {
    Preconditions.checkNotNull(portName);
    return this.getTypeInfo().getPort(portName, true).map(PortSymbol::getType);
  }

  @Override
  public Optional<SymTypeExpression> getTypeOfParameter(@NonNull String name) {
    Preconditions.checkNotNull(name);
    return this.getTypeInfo().getParameter(name).map(VariableSymbol::getType);
  }

  @Override
  public List<Optional<SymTypeExpression>> getParameterTypes() {
    return this.getTypeInfo().getParameterList()
      .stream().map(VariableSymbol::getType)
      .map(Optional::of)
      .collect(Collectors.toList());
  }

  @Override
  public CompKindOfComponentType deepClone(@NonNull ComponentTypeSymbol component) {
    CompKindOfComponentType clone = new CompKindOfComponentType(component);
    getSourceNode().ifPresent(clone::setSourceNode);
    clone.addArgument(getArguments());
    clone.bindParams();
    return clone;
  }

  @Override
  public boolean deepEquals(@NonNull CompKindExpression component) {
    Preconditions.checkNotNull(component);
    return this.getTypeInfo().equals(component.getTypeInfo());
  }
}
