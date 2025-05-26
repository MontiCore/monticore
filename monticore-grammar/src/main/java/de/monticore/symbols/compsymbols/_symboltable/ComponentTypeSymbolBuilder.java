/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import com.google.common.base.Preconditions;
import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class ComponentTypeSymbolBuilder extends ComponentTypeSymbolBuilderTOP {

  protected ComponentTypeSymbol outerComponent;
  protected List<TypeVarSymbol> typeParameters;

  public ComponentTypeSymbolBuilder() {
    super();
  }

  @Override
  public ComponentTypeSymbolBuilder setName(@NonNull String name) {
    Preconditions.checkNotNull(name);
    return super.setName(name);
  }

  @Override
  public ComponentTypeSymbolBuilder setSpannedScope(@NonNull ICompSymbolsScope spannedScope) {
    Preconditions.checkNotNull(spannedScope);
    return super.setSpannedScope(spannedScope);
  }

  public ComponentTypeSymbol getOuterComponent() {
    return this.outerComponent;
  }

  public ComponentTypeSymbolBuilder setOuterComponent(@NonNull ComponentTypeSymbol outerComponent) {
    Preconditions.checkArgument(!(outerComponent instanceof ComponentTypeSymbolSurrogate));
    this.outerComponent = outerComponent;
    return this.realBuilder;
  }

  public List<TypeVarSymbol> getTypeParameters() {
    return this.typeParameters;
  }

  public ComponentTypeSymbolBuilder setTypeParameters(@NonNull List<TypeVarSymbol> typeParameters) {
    Preconditions.checkNotNull(typeParameters);
    Preconditions.checkArgument(!typeParameters.contains(null));
    this.typeParameters = typeParameters;
    return this.realBuilder;
  }

  @Override
  public ComponentTypeSymbol build() {
    Preconditions.checkState(isValid());
    return doBuild(new ComponentTypeSymbol(this.name));
  }

  protected ComponentTypeSymbol doBuild(@NonNull ComponentTypeSymbol symbol) {
    Preconditions.checkNotNull(symbol);
    Preconditions.checkState(isValid());
    symbol.setSuperComponentsList(this.superComponents);
    symbol.setRefinementsList(this.refinements);
    symbol.setName(this.name);
    symbol.setFullName(this.fullName);
    symbol.setPackageName(this.packageName);
    if (this.astNode.isPresent()) {
      symbol.setAstNode(this.astNode.get());
    } else {
      symbol.setAstNodeAbsent();
    }
    symbol.setAccessModifier(this.accessModifier);
    symbol.setEnclosingScope(this.enclosingScope);
    symbol.setSpannedScope(this.spannedScope);
    if (this.parameter != null) {
      this.parameter.forEach(this.getSpannedScope()::add);
      symbol.addAllParameter(this.parameter);
    }
    symbol.setNumOptParams(this.numOptParams);
    if (this.typeParameters != null) {
      this.getTypeParameters().forEach(symbol.getSpannedScope()::add);
    }
    symbol.setOuterComponent(this.getOuterComponent());
    return symbol;
  }

  @Override
  public boolean isValid() {
    return this.name != null
      && this.spannedScope != null;
  }

  protected final boolean isValidNumOptParams() {
    return this.parameter.size() >= this.numOptParams;
  }
}
