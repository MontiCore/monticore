<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("scopeRule")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#list scopeRule.getAdditionalAttributeList() as attr>
  <#assign attrName=attr.getName()>
  <#assign attrType=stHelper.deriveAdditionalAttributeTypeWithMult(attr)>
  private ${genHelper.getQualifiedASTName(attrType)} _${attrName};
  
</#list>

${includeArgs("symboltable.ScopeRuleGetSet", scopeRule, false)}

<#list scopeRule.getMethodList() as meth>
  ${genHelper.printMethod(meth)}
</#list>