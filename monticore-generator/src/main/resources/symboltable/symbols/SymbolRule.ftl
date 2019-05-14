<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("ruleSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#list ruleSymbol.getAdditionalAttributeList() as attr>
  <#assign attrName="_" + attr.getName()>
  <#assign attrType=attr.getMCType().getBaseName()>
  private ${genHelper.getQualifiedASTName(attrType)} ${attrName};
</#list>

<#list ruleSymbol.getAdditionalAttributeList() as attr>
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
  
  public void set${attrName?cap_first}(${attrType} ${attrName}) {
    this._${attrName} = ${attrName};
  }
  
</#list>

<#list ruleSymbol.getMethodList() as meth>
  ${genHelper.printMethod(meth)}
</#list>