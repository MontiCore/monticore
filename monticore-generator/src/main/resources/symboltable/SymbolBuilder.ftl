<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "symbolName", "symbolRule", "imports", "prodSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign ruleName = prodSymbol.getName()>
<#assign scopeName = genHelper.getGrammarSymbol().getName() + "Scope">
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;
import de.monticore.symboltable.modifiers.AccessModifier;
import static de.monticore.symboltable.modifiers.AccessModifier.ALL_INCLUSION;
<#list imports as imp>
  import ${imp}._ast.*;
</#list>

/**
* Builder for {@link ${symbolName}Symbol}.
*/

public class ${className} {


  protected I${scopeName} enclosingScope;

  protected String fullName;

  protected String name;

  protected ${genHelper.getQualifiedGrammarName()?lower_case}._ast.AST${ruleName} node;

  protected String packageName;

  protected AccessModifier accessModifier = ALL_INCLUSION;

  <#if symbolRule.isPresent()>
    ${includeArgs("symboltable.symbols.SymbolRuleForBuilder", symbolRule.get())}
  </#if>

  protected ${className}() {}

  public ${symbolName}Symbol build() {
  ${symbolName}Symbol symbol = new ${symbolName}Symbol(name);
  <#if symbolRule.isPresent()>
    <#list symbolRule.get().getAdditionalAttributeList() as attr>
      symbol.set${attr.getName()?cap_first}(${attr.getName()});
    </#list>
  </#if>
    symbol.setEnclosingScope(this.enclosingScope);
    symbol.setFullName(this.fullName);
    return symbol;
  }

  public I${scopeName} getEnclosingScope(){
    return this.enclosingScope;
  }

  public ${className} setEnclosingScope(I${scopeName} newEnclosingScope){
    this.enclosingScope = newEnclosingScope;
    return this;
  }

  public String getName() {
    return name;
  }

  public ${className} setFullName(String fullName) {
    this.fullName = fullName;
    return this;
  }

  public String getFullName() {
    return fullName;
  }

  public ${className} setName(String name) {
    this.name = name;
    return this;
  }

  <#assign names = glex.getGlobalVar("nameHelper")>
  <#assign astNode = names.getQualifiedName(genHelper.getAstPackage(), "AST" + ruleName)>
  public Optional<${astNode}> getAstNode() {
    return Optional.ofNullable(node);
  }

  public ${className} setAstNode(${astNode} node) {
    this.node = node;
    return this;
  }

  public ${className} setAccessModifier(AccessModifier accessModifier) {
    this.accessModifier = accessModifier;
    return this;
  }

  public AccessModifier getAccessModifier() {
    return this.accessModifier;
  }

<#if symbolRule.isPresent()>
  <#list symbolRule.get().getAdditionalAttributeList() as attr>
    <#assign attrType=genHelper.deriveAdditionalAttributeTypeWithMult(attr)>
  public ${className} set${attr.getName()?cap_first}(${attrType} ${attr.getName()}) {
    this.${attr.getName()} = ${attr.getName()};
    return this;
  }

  </#list>
</#if>
}