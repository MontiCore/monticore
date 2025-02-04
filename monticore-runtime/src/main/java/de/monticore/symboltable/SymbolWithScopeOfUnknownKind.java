/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable;

import java.util.*;

import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import de.monticore.ast.ASTNode;

/** Represents a symbol of a kind unknown to a language. */
public class SymbolWithScopeOfUnknownKind implements IScopeSpanningSymbol {

  protected String name;

  protected IScope enclosingScope;

  protected AccessModifier accessModifier = AccessModifier.ALL_INCLUSION;

  protected String fullName;

  protected String packageName;

  protected IScope spannedScope;

  public SymbolWithScopeOfUnknownKind(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @Override
  public IScope getEnclosingScope() {
    return this.enclosingScope;
  }

  public void setEnclosingScope(IScope enclosingScope) {
    this.enclosingScope = enclosingScope;
  }

  /** This method always fails since an unknown symbol may not have a related ASTNode. */
  @Override
  public ASTNode getAstNode() {
    Log.error("0xA7400 get for AstNode can't return a value. Attribute is empty.");
    throw new IllegalStateException(); // Normally this statement is not reachable
  }

  /** This method always returns {@code false} since an unknown symbol may not have a related ASTNode. */
  @Override
  public boolean isPresentAstNode() {
    return false;
  }

  @Override
  public AccessModifier getAccessModifier() {
    return this.accessModifier;
  }

  @Override
  public void setAccessModifier(AccessModifier accessModifier) {
    this.accessModifier = accessModifier;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @Override
  public String getFullName() {
    if (this.fullName == null) {
      this.fullName = determineFullName();
    }
    return this.fullName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  @Override
  public String getPackageName() {
    if (this.packageName == null) {
      this.packageName = determinePackageName();
    }
    return this.packageName;
  }

  @Override
  public void accept(ITraverser visitor) {
    visitor.handle(this);
  }

  protected String determinePackageName() {
    IScope optCurrentScope = this.enclosingScope;

    while (optCurrentScope != null) {
      final IScope currentScope = optCurrentScope;

      if (currentScope.isPresentSpanningSymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, take its
        // package name. This check is important, since the package name of the
        // enclosing symbol might be set manually.
        return currentScope.getSpanningSymbol().getPackageName();
      } else if (currentScope instanceof IArtifactScope) {
        return ((IArtifactScope) currentScope).getPackageName();
      }

      optCurrentScope = currentScope.getEnclosingScope();
    }
    return "";
  }

  protected String determineFullName() {
    if (this.enclosingScope == null) {
      // There should not be a symbol that is not defined in any scope. This case should only
      // occur while the symbol is built (by the symbol table creator). So, here the full name
      // should not be cached yet.
      return this.name;
    }

    final Deque<String> nameParts = new ArrayDeque<>();
    nameParts.addFirst(this.name);

    IScope optCurrentScope = this.enclosingScope;

    while (optCurrentScope != null) {
      final IScope currentScope = optCurrentScope;
      if (currentScope.isPresentSpanningSymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, the full name
        // of that symbol is the missing prefix, and hence, the calculation
        // ends here. This check is important, since the full name of the enclosing
        // symbol might be set manually.
        nameParts.addFirst(currentScope.getSpanningSymbol().getFullName());
        break;
      }

      if (!(currentScope instanceof IGlobalScope)) {
        if (currentScope instanceof IArtifactScope) {
          // We have reached the artifact scope. Get the package name from the
          // symbol itself, since it might be set manually.
          if (!getPackageName().isEmpty()) {
            nameParts.addFirst(getPackageName());
          }
        } else {
          if (currentScope.isPresentName()) {
            nameParts.addFirst(currentScope.getName());
          }
          // ...else stop? If one of the enclosing scopes is unnamed,
          //         the full name is same as the simple name.
        }
        optCurrentScope = currentScope.getEnclosingScope();
      } else {
        break;
      }
    }

    return Names.constructQualifiedName(nameParts);
  }

  public IScope getSpannedScope() {
    if (this.spannedScope == null) throw new IllegalStateException();
    return this.spannedScope;
  }

  public void setSpannedScope(IScope scope) {
    if (scope != null) {
      this.spannedScope = scope;
      this.spannedScope.setSpanningSymbol(this);
    }
  }

}
