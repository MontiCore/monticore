<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symName", "attrList")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
<#list attrList as attr>
<#if genHelper.isOptional(attr.getMCType())>
    if (node.isPresent${attr.getName()?cap_first}()) {
    ${symName}${attr.getName()?cap_first}(Optional.of(node.${genHelper.getPlainGetter(attr)}()));
    }
<#else>
    ${symName}${attr.getName()?cap_first}(node.${genHelper.getPlainGetter(attr)}());
</#if>
</#list>
