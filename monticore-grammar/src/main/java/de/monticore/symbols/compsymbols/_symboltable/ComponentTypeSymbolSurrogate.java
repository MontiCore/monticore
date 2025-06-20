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