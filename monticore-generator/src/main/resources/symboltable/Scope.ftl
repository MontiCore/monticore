<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className","scopeRule", "symbolNames", "superScopeVisitors")}

<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign names = glex.getGlobalVar("nameHelper")>
<#assign languageName = genHelper.getGrammarSymbol().getName()>
<#assign superInterfaces = "implements I"+ className>
<#if scopeRule.isPresent()>
  <#if !scopeRule.get().isEmptySuperInterfaces()>
    <#assign superInterfaces = superInterfaces + ", "+ stHelper.printGenericTypes(scopeRule.get().getSuperInterfaceList())>
  </#if>
  <#if !scopeRule.get().isEmptySuperClasss()>
    <#assign superClass = " extends " + stHelper.printGenericTypes(scopeRule.get().getSuperClassList())>
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
import com.google.common.collect.ListMultimap;
import com.google.common.collect.ArrayListMultimap;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.*;
import de.se_rwth.commons.logging.Log;

public class ${className} ${superInterfaces} {

<#list symbolNames?keys as symbol>
  protected ListMultimap<String, ${symbolNames[symbol]}> ${symbolNames[symbol]?lower_case}s = ArrayListMultimap.create();

</#list>
  protected I${languageName}Scope enclosingScope;

  protected List<I${languageName}Scope> subScopes = new ArrayList<>();

  protected Optional<IScopeSpanningSymbol> spanningSymbol = Optional.empty();

  protected boolean shadowing;

  protected boolean exportsSymbols = true;

  protected Optional<String> name;

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
    this.enclosingScope = enclosingScope;
    this.shadowing = isShadowingScope;
    this.name = Optional.empty();
  }

  @Override
  public void addSubScope(I${languageName}Scope subScope) {
    if (!subScopes.contains(subScope)) {
      this.subScopes.add(subScope);
      subScope.setEnclosingScope(this);
    }
  }

  @Override
  public void removeSubScope(I${languageName}Scope subScope) {
    this.subScopes.remove(subScope);
  }

  public void setSpanningSymbol(IScopeSpanningSymbol symbol) {
    this.spanningSymbol =  Optional.of(symbol);
    setName(symbol.getName());
  }

  @Override
  public void setExportsSymbols(boolean b) {
    this.exportsSymbols = b;
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
    this.subScopes = subScopes;
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
    return <#list symbolNames?keys as symbol>${symbolNames[symbol]?lower_case}s.size()<#sep> + </#sep></#list>;
  }

<#list symbolNames?keys as symbol>
  @Override public void add(${symbolNames[symbol]} symbol) {
    this.${symbolNames[symbol]?lower_case}s.put(symbol.getName(), symbol);
    symbol.setEnclosingScope(this);

  }

  @Override public void remove(${symbolNames[symbol]} symbol) {
    this.${symbolNames[symbol]?lower_case}s.remove(symbol.getName(), symbol);
  }

  public ListMultimap<String, ${symbolNames[symbol]}> get${symbolNames[symbol]}s() {
    return this.${symbolNames[symbol]?lower_case}s;
  }

</#list>
  <#if scopeRule.isPresent()>
    <#list scopeRule.get().getAdditionalAttributeList() as attr>
      <#assign attrName=attr.getName()>
      <#assign attrType=attr.getGenericType().getTypeName()>
      private ${genHelper.getQualifiedASTName(attrType)} ${attrName};
    </#list>

    <#list scopeRule.get().getMethodList() as meth>
      ${genHelper.printMethod(meth)}
    </#list>
  </#if>

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
