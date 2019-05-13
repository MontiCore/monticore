<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributes")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#list attributes as attribute>
  <#assign methName = genHelper.getNativeAttributeName(attribute.getName())?cap_first>
  <#if genHelper.isListType(attribute.printType())>
    set${methName?remove_ending("s")}List(${attribute.getName()});
  <#elseif genHelper.isOptional(attribute.getType())>
    set${methName}Opt(${attribute.getName()});
  <#else>
    set${methName}(${attribute.getName()});
  </#if>
</#list>

