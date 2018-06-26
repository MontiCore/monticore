<#-- (c) https://github.com/MontiCore/monticore -->

${tc.signature("ruleSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>

<#list ruleSymbol.getAdditionalAttributeList() as attr>
  <#assign attrName=attr.getName()>
  <#assign attrType=attr.getGenericType().getTypeName()>
  private ${genHelper.getQualifiedASTName(attrType)} ${attrName};
</#list>

<#list ruleSymbol.getMethodList() as meth>
  ${genHelper.printMethod(meth)}
</#list>
