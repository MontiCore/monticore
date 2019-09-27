<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millAttributeList")}
  mill = a;
<#list millAttributeList as attribute>
  ${attribute.getName()} = mill;
</#list>