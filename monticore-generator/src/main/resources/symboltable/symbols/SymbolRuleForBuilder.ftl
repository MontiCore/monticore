<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("ruleSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#list ruleSymbol.getAdditionalAttributeList() as attr>
    <#assign attrName = attr.getName()>
    <#assign attrType=genHelper.deriveAdditionalAttributeTypeWithMult(attr)>
    <#assign attrValue = "">
    <#if genHelper.isAdditionalAttributeTypeList(attr)>
        <#assign attrValue = " = new java.util.ArrayList<>()">
    </#if>
    <#if genHelper.isAdditionalAttributeTypeOptional(attr)>
        <#assign attrValue = " = Optional.empty()">
    </#if>
  protected ${attrType} ${attrName} ${attrValue};
</#list>