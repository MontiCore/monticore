<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millAttributeList")}
  this.mill = mill;
<#list millAttributeList as attribute>
  this.${attribute.getName()} = mill;
</#list>