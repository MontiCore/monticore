<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("scopeRule", "isInterface")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#list scopeRule.getAdditionalAttributeList() as attr>
  <#assign attrName=attr.getName()>
  <#assign attrType=stHelper.deriveAdditionalAttributeTypeWithMult(attr)>
  <#if attrType == "boolean" || attrType == "Boolean">
    <#if attr.getName()?starts_with("is")>
      <#assign methodName=attr.getName()>
    <#else>
      <#assign methodName="is" + attr.getName()?cap_first>
    </#if>
  <#else>
    <#assign methodName="get" + attr.getName()?cap_first>
  </#if>
  <#if isInterface>
  public ${attrType} ${methodName}();
  <#else>
  public ${attrType} ${methodName}() {
    return this._${attrName};
  }
  </#if> 
  
  <#if isInterface>
  public void set${attrName?cap_first}(${attrType} ${attrName});
  <#else>
  public void set${attrName?cap_first}(${attrType} ${attrName}) {
    this._${attrName} = ${attrName};
  }
  </#if> 

</#list>