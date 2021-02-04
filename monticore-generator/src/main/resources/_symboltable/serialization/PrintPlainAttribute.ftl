<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("name", "default", "isObj")}
<#if isObj>
  if(!${default}.equals(${name})) {
<#else>
  if(${default} != ${name}) {
</#if>
    s2j.getJsonPrinter().member("${name}", ${name});
  }