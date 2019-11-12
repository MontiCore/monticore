<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("millAttributeList", "superMillList")}
  mill = null;
<#list millAttributeList as attribute>
  ${attribute.getName()} = null;
</#list>
<#list superMillList as mill>
  ${mill}.reset();
</#list>