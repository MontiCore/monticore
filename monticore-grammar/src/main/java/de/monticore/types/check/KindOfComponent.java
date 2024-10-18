/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbolTOP;
import de.monticore.symbols.compsymbols._symboltable.ComponentSymbol;
import de.monticore.symbols.compsymbols._symboltable.PortSymbol;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Optional;

/**
 * Represents a component expression that is solely defined by the component symbol.
 */
public class KindOfComponent extends CompKindExpression {

  public KindOfComponent(@NonNull ComponentSymbol component) {
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
    return this.getTypeInfo().getParameter(name).map(VariableSymbolTOP::getType);
  }

  @Override
  public KindOfComponent deepClone(@NonNull ComponentSymbol component) {
    return new KindOfComponent(component);
  }

  @Override
  public boolean deepEquals(@NonNull CompKindExpression component) {
    Preconditions.checkNotNull(component);
    return this.getTypeInfo().equals(component.getTypeInfo());
  }
}
