<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "scopeName", "scopeRule")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import de.monticore.ast.ASTNode;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.se_rwth.commons.logging.Log;

import com.google.common.collect.ImmutableList;

/**
* Builder for {@link ${scopeName}}.
*/

public class ${className} {


  protected I${scopeName} enclosingScope;

  protected List<I${scopeName}> subScopes = new ArrayList<>();

  protected Optional<IScopeSpanningSymbol> spanningSymbol = Optional.empty();

  protected boolean shadowing;

  protected boolean exportsSymbols = true;

  protected Optional<String> name = Optional.empty();

  protected ASTNode astNode;

<#if scopeRule.isPresent()>
  <#list scopeRule.get().getAdditionalAttributeList() as attr>
    <#assign attrName="_" + attr.getName()>
    <#assign attrType=attr.getMCType().getBaseName()>
  protected ${genHelper.getQualifiedASTName(attrType)} ${attrName};
  </#list>
</#if>

  protected ${className}() {}

  public ${scopeName} build() {
    ${scopeName} scope = new ${scopeName}(shadowing);
    this.spanningSymbol.ifPresent(scope::setSpanningSymbol);
    scope.setExportsSymbols(this.exportsSymbols);
    scope.setEnclosingScope(this.enclosingScope);
    scope.setSubScopes(this.subScopes);
    scope.setAstNode(this.astNode);
    this.name.ifPresent(scope::setName);
    this.subScopes.forEach(s -> s.setEnclosingScope(scope));
  <#if scopeRule.isPresent()>
    <#list scopeRule.get().getAdditionalAttributeList() as attr>
      <#assign attrType=genHelper.getQualifiedASTName(attr.getMCType().getBaseName())>
      scope.set${attr.getName()?cap_first}(${attr.getName()});
    </#list>
  </#if>
    return scope;
  }

  public ${className} addSubScope(I${scopeName} subScope) {
    if (!this.subScopes.contains(subScope)) {
      this.subScopes.add(subScope);
    }
    return this;
  }

  public ${className} removeSubScope(I${scopeName} subScope) {
    this.subScopes.remove(subScope);
    return this;
  }

  public ${className} setSpanningSymbol(IScopeSpanningSymbol symbol) {
    this.spanningSymbol =  Optional.of(symbol);
    setName(symbol.getName());
    return this;
  }

  public ${className} setExportsSymbols(boolean b) {
    this.exportsSymbols = b;
    return this;
  }

  public Optional<IScopeSpanningSymbol> getSpanningSymbol() {
    return this.spanningSymbol;
  }

  public Optional<I${scopeName}> getEnclosingScope() {
    return Optional.ofNullable(enclosingScope);
  }

  public ${className} setEnclosingScope(I${scopeName} newEnclosingScope) {
    this.enclosingScope = newEnclosingScope;
    return this;
  }

  public List<I${scopeName}> getSubScopes() {
    return ImmutableList.copyOf(subScopes);
  }

  public ${className} setSubScopes(List<I${scopeName}> subScopes) {
    this.subScopes = subScopes;
    return this;
  }

  public boolean isShadowingScope() {
    return shadowing;
  }

  public boolean isSpannedBySymbol() {
    return spanningSymbol.isPresent();
  }

  public boolean exportsSymbols() {
    return exportsSymbols;
  }

  public ${className} setAstNode(ASTNode node) {
    this.astNode = node;
    return this;
  }

  public Optional<ASTNode> getAstNode() {
    return Optional.ofNullable(astNode);
  }

  public ${className} setName(String name) {
    this.name = Optional.ofNullable(name);
    return this;
  }

  public Optional<String> getName() {
    return this.name;
  }

  <#if scopeRule.isPresent()>
    <#list scopeRule.get().getAdditionalAttributeList() as attr>
      <#assign attrName=attr.getName()>
      <#assign attrType=attr.getMCType().getBaseName()>
      <#if attrType == "boolean" || attrType == "Boolean">
        <#if attr.getName()?starts_with("is")>
          <#assign methodName=attr.getName()>
        <#else>
          <#assign methodName="is" + attr.getName()?cap_first>
        </#if>
      <#else>
        <#assign methodName="get" + attr.getName()?cap_first>
      </#if>
  public ${attrType} ${methodName}() {
    return this._${attrName};
  }

  public ${className} set${attrName?cap_first}(${attrType} ${attrName}) {
    this._${attrName} = ${attrName};
    return this;
  }

    </#list>
  </#if>
}