<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("scopeRule")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#list scopeRule.getAdditionalAttributeList() as attr>
  <#assign attrName=attr.getName()>
  <#assign attrType=stHelper.deriveAdditionalAttributeTypeWithMult(attr)>
  <#assign attrValue = "">
  <#if genHelper.isAdditionalAttributeTypeList(attr)>
    <#assign attrValue = " = new java.util.ArrayList<>()">
  </#if>
  <#if genHelper.isAdditionalAttributeTypeOptional(attr)>
    <#assign attrValue = " = Optional.empty()">
  </#if>
  private ${genHelper.getQualifiedASTName(attrType)} _${attrName} ${attrValue};
  
</#list>

${includeArgs("symboltable.ScopeRuleGetSet", scopeRule, false)}

<#list scopeRule.getGrammarMethodList() as meth>
  ${genHelper.printMethod(meth)}
</#list>