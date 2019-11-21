<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attributes")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#list attributes as attribute>
  <#assign methName = genHelper.getNativeAttributeName(attribute.getName())?cap_first>
  <#if genHelper.isListType(attribute.printType())>
    set${methName?remove_ending("s")}List(${attribute.getName()});
  <#elseif genHelper.isOptional(attribute.getMCType())>
    if (${attribute.getName()}.isPresent()) {
      set${methName}(${attribute.getName()}.get());
    } else {
      set${methName}Absent();
    }
  <#else>
    set${methName}(${attribute.getName()});
  </#if>
</#list>

