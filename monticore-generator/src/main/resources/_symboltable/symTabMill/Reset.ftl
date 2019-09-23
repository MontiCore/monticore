<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millAttributeList", "superMillList")}
  this.mill = null;
<#list millAttributeList as attribute>
  this.${attribute.getName()} = null;
</#list>
<#list superMillList as mill>
  ${mill}.reset();
</#list>