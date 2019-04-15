<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributes")}
<#assign genHelper = glex.getGlobalVar("astHelper")>

<#list attributes as attribute>
  <#if genHelper.isListType(attribute.printType())>
    set${attribute.getName()?cap_first}List(${attribute.getName()});
  <#elseif genHelper.isOptional(attribute.getType())>
    set${attribute.getName()?cap_first}Opt(${attribute.getName()});
  <#else>
    set${attribute.getName()?cap_first}(${attribute.getName()});
  </#if>
</#list>

