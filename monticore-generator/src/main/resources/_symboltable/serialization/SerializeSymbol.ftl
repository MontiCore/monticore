<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("symName","nonListAttr", "listAttr", "optAttr")}
<#list nonListAttr as attr>
  serialize${symName?cap_first}${attr.getName()?cap_first}(node.get${attr.getName()?cap_first}());
</#list>
<#list optAttr as attr>
  printer.beginArray();
  serialize${symName?cap_first}${attr.getName()?cap_first}(node.get${attr.getName()?cap_first}Opt());
  printer.endArray();
</#list>
<#list listAttr as attr>
  printer.beginArray();
  serialize${symName?cap_first}${attr.getName()?cap_first}(node.get${attr.getName()?cap_first}List());
  printer.endArray();
</#list>