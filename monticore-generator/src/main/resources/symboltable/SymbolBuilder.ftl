<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "symbolName", "symbolRule", "imports")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};

import java.util.Optional;
<#list imports as imp>
  import ${imp}._ast.*;
</#list>

/**
* Builder for {@link ${symbolName}}.
*/

public class ${className} {


  protected I${languageName}Scope enclosingScope;

  protected String fullName;

  protected String name;

  <#if symbolRule.isPresent()>
    <#list symbolRule.get().getAdditionalAttributeList() as attr>
      <#assign attrType=genHelper.getQualifiedASTName(attr.getMCType().getBaseName())>
      protected ${attrType} ${attr.getName()};
    </#list>
  </#if>

  protected ${className}() {}

  public ${symbolName}Symbol build() {
  ${symbolName}Symbol ${symbolName?uncap_first}Symbol = new ${symbolName}Symbol(name);
  <#if symbolRule.isPresent()>
    <#list symbolRule.get().getAdditionalAttributeList() as attr>
      <#assign attrType=genHelper.getQualifiedASTName(attr.getMCType().getBaseName())>
      ${symbolName?uncap_first}Symbol.set${attr.getName()?cap_first}(${attr.getName()});
    </#list>
  </#if>
    ${symbolName?uncap_first}Symbol.setEnclosingScope(this.enclosingScope);
    ${symbolName?uncap_first}Symbol.setFullName(this.fullName);
    ${symbolName?uncap_first}Symbol.name(this.name);
    return ${symbolName?uncap_first}Symbol;
  }

  public I${languageName}Scope getEnclosingScope(){
    return this.enclosingScope;
  }

  public ${className} setEnclosingScope(I${languageName}Scope newEnclosingScope){
    this.enclosingScope = newEnclosingScope;
    return this;
  }

  @Override
  public ${className} setAccessModifier(AccessModifier accessModifier) {
    //TODO
    return this;
  }

  @Override public String getName() {
    return name;
  }

  public ${className} setFullName(String fullName) {
    this.fullName = fullName;
    return this;
  }

  public String getFullName() {
    if (fullName == null) {
      fullName = determineFullName();
    }
    return fullName;
  }

  public ${className} name(String name) {
    this.name = name;
    return this;
  }

<#if symbolRule.isPresent()>
  <#list symbolRule.get().getAdditionalAttributeList() as attr>
    <#assign attrType=genHelper.getQualifiedASTName(attr.getMCType().getBaseName())>
    public ${className} ${attr.getName()}(${attrType} ${attr.getName()}) {
    this.${attr.getName()} = ${attr.getName()};
    return this;
    }

  </#list>
</#if>
}