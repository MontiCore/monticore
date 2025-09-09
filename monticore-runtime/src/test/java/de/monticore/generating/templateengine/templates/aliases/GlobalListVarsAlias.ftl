<#-- (c) https://github.com/MontiCore/monticore -->
<#if getGlobalVar("liste")??>
    <#list getGlobalVar("liste") as l>
    ${l}
    </#list>
<#else>
${error("global variable liste does not exist")}
</#if>