<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "interfaceName","scopeRules", "symbolNames", "superScopes", "superScopeVisitors", "hasHWC")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign names = glex.getGlobalVar("nameHelper")>
<#assign languageName = genHelper.getGrammarSymbol().getName()>
<#assign superInterfaces = "implements "+ interfaceName>
<#assign superClass = "">
<#if (scopeRules?size >0) >
  <#list scopeRules[0].getSuperInterfaceList() as s>
    <#assign superInterfaces = superInterfaces + ", "+ s.printType()>
  </#list>
  <#if !scopeRules[0].isEmptySuperClasss()>
    <#assign superClass = " extends " + scopeRules[0].getSuperClass(0).printType()>
  </#if>
</#if>


<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedListMultimap;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.*;
import de.se_rwth.commons.logging.Log;

public <#if hasHWC>abstract</#if> class ${className} ${superClass} ${superInterfaces} {

<#list symbolNames?keys as symbol>
  protected LinkedListMultimap<String, ${symbolNames[symbol]}> ${symbol?lower_case}s = LinkedListMultimap.create();

  protected boolean ${symbol?lower_case}AlreadyResolved = false;

</#list>
  protected I${languageName}Scope enclosingScope;

  protected List<I${languageName}Scope> subScopes = new ArrayList<>();

  protected Optional<IScopeSpanningSymbol> spanningSymbol = Optional.empty();

  protected boolean shadowing;

  protected boolean exportsSymbols = true;

  protected Optional<String> name = Optional.empty();

  protected ASTNode astNode;

  public ${className}() {
    super();
    this.name = Optional.empty();
  }

  public ${className}(boolean isShadowingScope) {
    this.shadowing = isShadowingScope;
    this.name = Optional.empty();
  }

  public ${className}(I${languageName}Scope enclosingScope) {
    this(enclosingScope, false);
  }

  public ${className}(I${languageName}Scope enclosingScope, boolean isShadowingScope) {
    this.setEnclosingScope(enclosingScope);
    this.shadowing = isShadowingScope;
    this.name = Optional.empty();
  }

  @Override
  public void addSubScope(I${languageName}Scope subScope) {
    if (!this.subScopes.contains(subScope)) {
      this.subScopes.add(subScope);
      subScope.setEnclosingScope(this);
    }
  }

  @Override
  public void removeSubScope(I${languageName}Scope subScope) {
    this.subScopes.remove(subScope);
    if (subScope.getEnclosingScope().isPresent() && subScope.getEnclosingScope().get() == this) {
      subScope.setEnclosingScope(null);
    }
  }

  public void setSpanningSymbol(IScopeSpanningSymbol symbol) {
    this.spanningSymbol =  Optional.of(symbol);
    setName(symbol.getName());
  }

  @Override
  public void setExportsSymbols(boolean b) {
    this.exportsSymbols = b;
  }

  @Override
  public void setShadowing(boolean b) {
    this.shadowing = b;
  }

   public Optional<IScopeSpanningSymbol> getSpanningSymbol() {
    return this.spanningSymbol;
  }

  public Optional<I${languageName}Scope> getEnclosingScope() {
    return Optional.ofNullable(enclosingScope);
  }

  public void setEnclosingScope(I${languageName}Scope newEnclosingScope) {
    if ((this.enclosingScope != null) && (newEnclosingScope != null)) {
      if (this.enclosingScope == newEnclosingScope) {
        return;
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
  }

  public List<I${languageName}Scope> getSubScopes() {
    return ImmutableList.copyOf(subScopes);
  }

  public void setSubScopes(List<I${languageName}Scope> subScopes) {
    this.subScopes.forEach(this::removeSubScope);
    subScopes.forEach(this::addSubScope);
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
  public void setAstNode(ASTNode node) {
    this.astNode = node;
  }

  @Override
  public Optional<ASTNode> getAstNode() {
    return Optional.ofNullable(astNode);
  }

  @Override
  public void setName(String name) {
    this.name = Optional.ofNullable(name);
  }

  @Override
  public Optional<String> getName() {
    return this.name;
  }

  @Override public int getSymbolsSize() {
    <#if (symbolNames?keys?size gt 0)>
    return <#list symbolNames?keys as symbol>${symbol?lower_case}s.size()<#sep> + </#sep></#list>;
    <#else>
      return 0;
    </#if>
  }

<#list symbolNames?keys as symbol>
  @Override public void add(${symbolNames[symbol]} symbol) {
    this.${symbol?lower_case}s.put(symbol.getName(), symbol);
    symbol.setEnclosingScope(this);

  }

  @Override public void remove(${symbolNames[symbol]} symbol) {
    this.${symbol?lower_case}s.remove(symbol.getName(), symbol);
  }

  public LinkedListMultimap<String, ${symbolNames[symbol]}> get${symbol}s() {
    return this.${symbol?lower_case}s;
  }

  @Override
  public boolean is${symbol}AlreadyResolved() {
    return ${symbol?lower_case}AlreadyResolved;
  }

  @Override
  public void set${symbol}AlreadyResolved(boolean symbolAlreadyResolved) {
    ${symbol?lower_case}AlreadyResolved = symbolAlreadyResolved;
  }

</#list>
  <#list scopeRules as scopeRule>
    ${includeArgs("symboltable.ScopeRule", scopeRule)}
  </#list>

  <#assign langVisitorType = names.getQualifiedName(genHelper.getVisitorPackage(), genHelper.getGrammarSymbol().getName() + "ScopeVisitor")>
    public void accept(${langVisitorType} visitor) {
  <#if genHelper.isSupertypeOfHWType(className, "")>
    <#assign plainName = className?remove_ending("TOP")>
    if (this instanceof ${plainName}) {
      visitor.handle((${plainName}) this);
    } else {
      throw new UnsupportedOperationException("0xA7010${genHelper.getGeneratedErrorCode(ast)} Only handwritten class ${plainName} is supported for the visitor");
    }
  <#else>
    visitor.handle(this);
  </#if>
    }

  <#list superScopes as superScope>
  @Override
  public void addSubScope(${superScope} subScope) {
    this.addSubScope((I${languageName}Scope) subScope);
  }

  @Override
  public void removeSubScope(${superScope} subScope) {
    this.removeSubScope((I${languageName}Scope) subScope);
  }

  @Override
  public void setEnclosingScope(${superScope} newEnclosingScope) {
    this.setEnclosingScope((I${languageName}Scope) newEnclosingScope);
  }
  </#list>

  <#list superScopeVisitors as superScopeVisitor>
  public void accept(${superScopeVisitor} visitor) {
    if (visitor instanceof ${langVisitorType}) {
      accept((${langVisitorType}) visitor);
    } else {
      throw new UnsupportedOperationException("0xA7010${genHelper.getGeneratedErrorCode(ast)} Scope node type ${className} expected a visitor of type ${langVisitorType}, but got ${superScopeVisitor}.");
    }
  }
  </#list>
    }
