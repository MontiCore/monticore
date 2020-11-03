<#-- (c) https://github.com/MontiCore/monticore -->
${glex.defineGlobalVar("v1",16)}
${glex.changeGlobalVar("v1",22)}
A:${glex.getGlobalVar("v1")}
${glex.changeGlobalVar("v2",22)}
<#if glex.hasGlobalVar("v2")>
 B:ERROR
<#else>
 B:OK
</#if>
