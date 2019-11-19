<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symName", "attrList")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
<#list attrList as attr>
    <#if genHelper.isListType(attr.printType())>
  ${symName}${attr.getName()?cap_first}(node.${genHelper.getPlainGetter(attr)}());
    <#elseif genHelper.isOptional(attr.getMCType())>
      if (node.isPresent${attr.getName()?cap_first}()) {
        ${symName}${attr.getName()?cap_first}(Optional.of(node.${genHelper.getPlainGetter(attr)}()));
      } else {
        ${symName}${attr.getName()?cap_first}(Optional.empty());
      }
    <#else>
      printer.beginArray("${attr.getName()}");
        ${symName}${attr.getName()?cap_first}(node.${genHelper.getPlainGetter(attr)}());
      printer.endArray();
    </#if>
</#list>
