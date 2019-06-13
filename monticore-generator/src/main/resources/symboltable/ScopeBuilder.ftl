<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "scopeName")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;

/**
* Builder for {@link ${scopeName}}.
*/

public class ${className} {


  protected I${languageName}Scope enclosingScope;

  protected List<I${languageName}Scope> subScopes = new ArrayList<>();

  protected Optional<IScopeSpanningSymbol> spanningSymbol = Optional.empty();

  protected boolean shadowing;

  protected boolean exportsSymbols = true;

  protected Optional<String> name;

  protected ASTNode astNode;

  protected ${className}() {}

  public ${scopeName} build() {
    ${scopeName} scope = new ${scopeName}();
    scope.setSpanningSymbol(this.spanningSymbol);
    scope.setExportsSymbols(this.exportsSymbols);
    scope.setEnclosingScope(this.enclosingScope);
    scope.setSubScopes(this.subScopes);
    scope.setShadowing(this.shadowing);
    scope.setAstNode(this.astNode);
    scope.setName(this.name);
  }

  @Override
  public ${className} addSubScope(I${languageName}Scope subScope) {
    if (!subScopes.contains(subScope)) {
      this.subScopes.add(subScope);
      subScope.setEnclosingScope(this);
    }
    return this;
  }

  @Override
  public ${className} removeSubScope(I${languageName}Scope subScope) {
    this.subScopes.remove(subScope);
    return this;
  }

  public ${className} setSpanningSymbol(IScopeSpanningSymbol symbol) {
    this.spanningSymbol =  Optional.of(symbol);
    setName(symbol.getName());
    return this;
  }

  @Override
  public ${className} setExportsSymbols(boolean b) {
    this.exportsSymbols = b;
    return this;
  }

  public Optional<IScopeSpanningSymbol> getSpanningSymbol() {
    return this.spanningSymbol;
  }

  public Optional<I${languageName}Scope> getEnclosingScope() {
    return Optional.ofNullable(enclosingScope);
  }

  public ${className} setEnclosingScope(I${languageName}Scope newEnclosingScope) {
    if ((this.enclosingScope != null) && (newEnclosingScope != null)) {
      if (this.enclosingScope == newEnclosingScope) {
        return this;
      }
      Log.warn("0xA1042 Scope \"" + getName() + "\" has already an enclosing scope.");
    }

    // remove this scope from current (old) enclosing scope, if exists.
    if (this.enclosingScope != null) {
      this.enclosingScope.removeSubScope(this);
    }

    // add this scope to new enclosing scope, if exists.
    if (newEnclosingScope != null) {
      newEnclosingScope.addSubScope(this);
    }

    // set new enclosing scope (or null)
    this.enclosingScope = newEnclosingScope;
    return this;
  }

  public List<I${languageName}Scope> getSubScopes() {
    return ImmutableList.copyOf(subScopes);
  }

  public ${className} setSubScopes(List<I${languageName}Scope> subScopes) {
    this.subScopes = subScopes;
    return this;
  }

  @Override
  public boolean isShadowingScope() {
    return shadowing;
  }

  @Override
  public boolean isSpannedBySymbol() {
    return spanningSymbol.isPresent();
  }

  @Override
  public boolean exportsSymbols() {
    return exportsSymbols;
  }

  @Override
  public ${className} setAstNode(ASTNode node) {
    this.astNode = node;
    return this;
  }

  @Override
  public Optional<ASTNode> getAstNode() {
    return Optional.ofNullable(astNode);
  }

  @Override
  public ${className} setName(String name) {
    this.name = Optional.ofNullable(name);
    return this;
  }

  @Override
  public Optional<String> getName() {
    return this.name;
  }

}