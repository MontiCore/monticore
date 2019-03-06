<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("clazz")}
<#list clazz.getCDAttributeList() as attribute>
set${attribute.getName()?cap_first}(${attribute.getName()});
</#list>
