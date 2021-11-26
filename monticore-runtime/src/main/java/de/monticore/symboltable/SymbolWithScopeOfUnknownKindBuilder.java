/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;
import de.monticore.ast.ASTNode;

/** A builder for {@link SymbolWithScopeOfUnknownKind}. */
public class SymbolWithScopeOfUnknownKindBuilder {

  protected SymbolWithScopeOfUnknownKindBuilder realBuilder;

  protected String name;
  protected String fullName;
  protected String packageName;

  protected AccessModifier accessModifier;

  protected IScope enclosingScope;

  protected IScope spannedScope;

  public SymbolWithScopeOfUnknownKindBuilder() {
      this.realBuilder = this;
  }

  public SymbolWithScopeOfUnknownKind build() {
    SymbolWithScopeOfUnknownKind symbol = new SymbolWithScopeOfUnknownKind(name);
    symbol.setName(this.name);
    symbol.setFullName(this.fullName);
    symbol.setPackageName(this.packageName);
    symbol.setAccessModifier(this.accessModifier);
    symbol.setEnclosingScope(this.enclosingScope);
    symbol.setSpannedScope(this.spannedScope);
    symbol.setName(this.name);
    symbol.setFullName(this.fullName);
    symbol.setPackageName(this.packageName);
    symbol.setAccessModifier(this.accessModifier);
    symbol.setEnclosingScope(this.enclosingScope);
    return symbol;
  }

  public boolean isValid() {
    return name != null
        && fullName != null
        && packageName != null
        && accessModifier != null
        && enclosingScope != null
        && spannedScope != null;
  }

  public String getName() {
    return this.name;
  }

  public String getFullName() {
    return this.fullName;
  }

  public String getPackageName() {
    return this.packageName;
  }

  /** This method always fails since an unknown symbol may not have a related ASTNode. */
  public ASTNode getAstNode() {
    Log.error("0xA7402 get for AstNode can't return a value. Attribute is empty.");
    throw new IllegalStateException(); // Normally this statement is not reachable
  }

  /** This method always returns {@code false} since an unknown symbol may not have a related ASTNode. */
  public boolean isPresentAstNode() {
    return false;
  }

  public AccessModifier getAccessModifier() {
    return this.accessModifier;
  }

  public IScope getEnclosingScope() {
    return this.enclosingScope;
  }

  public IScope getSpannedScope() {
    return this.spannedScope;
  }

  public SymbolWithScopeOfUnknownKindBuilder setName(String name) {
      this.name = name;
      return this.realBuilder;
  }

  public SymbolWithScopeOfUnknownKindBuilder setFullName(String fullName) {
    this.fullName = fullName;
    return this.realBuilder;
  }

  public SymbolWithScopeOfUnknownKindBuilder setPackageName(String packageName) {
    this.packageName = packageName;
    return this.realBuilder;
  }

  public SymbolWithScopeOfUnknownKindBuilder setAccessModifier(AccessModifier accessModifier) {
    this.accessModifier = accessModifier;
    return this.realBuilder;
  }

  public SymbolWithScopeOfUnknownKindBuilder setEnclosingScope(IScope enclosingScope) {
    this.enclosingScope = enclosingScope;
    return this.realBuilder;
  }

  public SymbolWithScopeOfUnknownKindBuilder setSpannedScope(IScope spannedScope)  {
    this.spannedScope = spannedScope;
    return this.realBuilder;
  }

}
