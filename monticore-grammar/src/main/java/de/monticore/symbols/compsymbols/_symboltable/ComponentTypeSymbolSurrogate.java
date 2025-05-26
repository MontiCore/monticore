/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symbols.compsymbols._symboltable;

import de.monticore.symbols.basicsymbols._symboltable.TypeVarSymbol;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.compsymbols.CompSymbolsMill;
import de.monticore.symbols.compsymbols._ast.ASTComponentType;
import de.se_rwth.commons.logging.Log;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class ComponentTypeSymbolSurrogate extends ComponentTypeSymbolSurrogateTOP {

  public ComponentTypeSymbolSurrogate(@NonNull String name) {
    super(name);
    this.spannedScope = CompSymbolsMill.scope();
  }

  protected Optional<ComponentTypeSymbol> getDelegate() {
    return this.delegate;
  }

  protected void setDelegate(@Nullable ComponentTypeSymbol delegate) {
    this.delegate = Optional.ofNullable(delegate);
  }

  @Override
  public ComponentTypeSymbol lazyLoadDelegate() {
    if (this.getDelegate().isEmpty()) {
      this.setDelegate(this.getEnclosingScope().resolveComponentType(this.getName()).orElse(tryGeneric().orElse(null)));
    }

    if (this.getDelegate().isPresent()) {
      return this.getDelegate().get();
    } else {
      // Copied error message from the original lazyLoadDelegate
      Log.error("0xA1038 " + ComponentTypeSymbolSurrogate.class.getSimpleName() +
        " Could not load full information of '" + name +
        "' (Kind " + "de.monticore.symbols.compsymbols._symboltabl.ComponentTypeSymbol" + ")."
      );
      return this;
    }
  }

  protected Optional<ComponentTypeSymbol> tryGeneric() {
    Optional<TypeVarSymbol> resolvedTypeSymbol = this.getEnclosingScope().resolveTypeVar(this.getName());
    if (resolvedTypeSymbol.isPresent()) {
      ComponentTypeSymbol resolvedSymbol = this.getEnclosingScope().resolveComponentType(resolvedTypeSymbol.get().getSuperTypes(0).printFullName()).orElse(null);
      return Optional.ofNullable(resolvedSymbol);
    }
    return Optional.empty();
  }

  @Override
  public void setSpannedScope(@NonNull ICompSymbolsScope spannedScope) {
    if (checkLazyLoadDelegate()) {
      this.lazyLoadDelegate().setSpannedScope(spannedScope);
    } else {
      super.setSpannedScope(spannedScope);  // Avoid infinite recursion with this case
    }
  }

  @Override
  public ICompSymbolsScope getSpannedScope() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getSpannedScope() :
      super.getSpannedScope();  // Avoid infinite recursion with this case
  }

  @Override
  public boolean isInnerComponent() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().isInnerComponent() :
      super.isInnerComponent();  // Avoid infinite recursion with this case
  }

  @Override
  public Optional<ComponentTypeSymbol> getOuterComponent() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getOuterComponent() :
      super.getOuterComponent();  // Avoid infinite recursion with this case
  }

  @Override
  public void setOuterComponent(@Nullable ComponentTypeSymbol outerComponent) {
    if (checkLazyLoadDelegate()) {
      this.lazyLoadDelegate().setOuterComponent(outerComponent);
    } else {
      super.setOuterComponent(outerComponent);  // Avoid infinite recursion with this case
    }
  }

  @Override
  public List<VariableSymbol> getParameterList() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getParameterList() :
      super.getParameterList();  // Avoid infinite recursion with this case
  }

  @Override
  public boolean addParameter(@NonNull VariableSymbol parameter) {
    if (checkLazyLoadDelegate()) {
      return this.lazyLoadDelegate().addParameter(parameter);
    } else {
      return super.addParameter(parameter);  // Avoid infinite recursion with this case
    }
  }

  @Override
  public Set<PortSymbol> getAllPorts() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getAllPorts() :
      super.getAllPorts();  // Avoid infinite recursion with this case
  }

  @Override
  public ASTComponentType getAstNode() {
    return checkLazyLoadDelegate() ?
      this.lazyLoadDelegate().getAstNode() :
      super.getAstNode();  // Avoid infinite recursion with this case
  }
}