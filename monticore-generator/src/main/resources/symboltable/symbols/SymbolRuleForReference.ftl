<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("ruleSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#list ruleSymbol.getAdditionalAttributeList() as attr>
  <#assign attrType=genHelper.deriveAdditionalAttributeTypeWithMult(attr)>
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
    return getReferencedSymbol().${methodName}();
  }

</#list>

<#list ruleSymbol.getGrammarMethodList() as meth>
  ${genHelper.printMethod(meth)}
</#list>