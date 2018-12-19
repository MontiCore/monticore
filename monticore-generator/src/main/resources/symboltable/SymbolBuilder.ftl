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
    * Builder for {@link ${className}}.
    */

public class ${className} {

  protected String name;
  
  <#if symbolRule.isPresent()>
  <#list symbolRule.get().getAdditionalAttributeList() as attr>
  <#assign attrType=genHelper.getQualifiedASTName(attr.getGenericType().getTypeName())>
  protected ${attrType} ${attr.getName()};
  </#list>
  </#if>

  protected ${className}() {}

  public ${symbolName}Symbol build() {
    ${symbolName}Symbol ${symbolName?uncap_first}Symbol = new ${symbolName}Symbol(name);
    <#if symbolRule.isPresent()>
    <#list symbolRule.get().getAdditionalAttributeList() as attr>
    <#assign attrType=genHelper.getQualifiedASTName(attr.getGenericType().getTypeName())>
    ${symbolName?uncap_first}Symbol.set${attr.getName()?cap_first}(${attr.getName()});
    </#list>
    </#if>
    return ${symbolName?uncap_first}Symbol;
  }

  public ${className} name(String name) {
    this.name = name;
    return this;
  }
  
  <#if symbolRule.isPresent()>
  <#list symbolRule.get().getAdditionalAttributeList() as attr>
  <#assign attrType=genHelper.getQualifiedASTName(attr.getGenericType().getTypeName())>
  public ${className} ${attr.getName()}(${attrType} ${attr.getName()}) {
    this.${attr.getName()} = ${attr.getName()};
    return this;
  }
  
  </#list>
  </#if>
}