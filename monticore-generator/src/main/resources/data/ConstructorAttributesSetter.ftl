<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz", "inheritedAttributes")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#list clazz.getCDAttributeList() as attribute>
  ${tc.include("data.AttributeSetter", attribute)}
</#list>
<#list inheritedAttributes as attribute>
  ${tc.include("data.AttributeSetter", attribute)}
</#list>
