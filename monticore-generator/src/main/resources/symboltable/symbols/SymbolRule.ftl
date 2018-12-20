<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("ruleSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#list ruleSymbol.getAdditionalAttributeList() as attr>
  <#assign attrName=attr.getName()>
  <#assign attrType=attr.getGenericType().getTypeName()>
  private ${genHelper.getQualifiedASTName(attrType)} ${attrName};
</#list>

<#list ruleSymbol.getAdditionalAttributeList() as attr>
  <#assign attrName=attr.getName()>
  <#assign attrType=attr.getGenericType().getTypeName()>
  <#if attrType == "boolean" || attrType == "Boolean">
    <#assign methodName="is" + attr.getName()?cap_first>
  <#else>
    <#assign methodName="get" + attr.getName()?cap_first>
  </#if>
  public ${attrType} ${methodName}() {
    return this.${attrName};
  }
  
  public void set${attrName?cap_first}(${attrType} ${attrName}) {
    this.${attrName} = ${attrName};
  }
  
</#list>

<#list ruleSymbol.getMethodList() as meth>
  ${genHelper.printMethod(meth)}
</#list>