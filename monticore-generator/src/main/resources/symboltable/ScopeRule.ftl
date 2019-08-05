<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("scopeRule")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#list scopeRule.getAdditionalAttributeList() as attr>
  <#assign attrName=attr.getName()>
  <#assign attrType=stHelper.deriveAdditionalAttributeTypeWithMult(attr)>
  private ${genHelper.getQualifiedASTName(attrType)} _${attrName};

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

<#list scopeRule.getMethodList() as meth>
  ${genHelper.printMethod(meth)}
</#list>