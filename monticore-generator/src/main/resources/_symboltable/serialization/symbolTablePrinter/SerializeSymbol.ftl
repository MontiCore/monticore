<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symName","nonListAttr", "listAttr")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
<#list nonListAttr as attr>
  ${symName}${attr.getName()?cap_first}(node.${genHelper.getPlainGetter(attr)}());
</#list>
<#list listAttr as attr>
  printer.beginArray("${attr.getName()}");
  ${symName}${attr.getName()?cap_first}(node.${genHelper.getPlainGetter(attr)}());
  printer.endArray();
</#list>