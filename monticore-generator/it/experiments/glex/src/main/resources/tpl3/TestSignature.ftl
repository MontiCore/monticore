<#-- (c) https://github.com/MontiCore/monticore -->
${signature("a1")}
<#if a1?has_content>
  A:OK:${a1.name}
<#else>
  A:ERROR
</#if>
<#if ast?has_content>
  B:ERROR
<#else>
  B:OK
</#if>
