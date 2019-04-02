<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz", "inheritedAttributes")}
<#list clazz.getCDAttributeList() as attribute>
set${attribute.getName()?cap_first}(${attribute.getName()});
</#list>
<#list inheritedAttributes as attribute>
  set${attribute.getName()?cap_first}(${attribute.getName()});
</#list>
