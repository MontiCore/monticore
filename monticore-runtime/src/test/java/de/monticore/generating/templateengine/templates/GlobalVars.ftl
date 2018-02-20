<#-- (c) https://github.com/MontiCore/monticore -->
<#if getGlobalVar("test")??>
${getGlobalVar("test")}
<#else>
${error("global test variable does not exist")}
</#if>

<#if getGlobalVar("asd")??>
${getGlobalVar("asd")}
<#else>
${error("global asd variable does not exist")}
</#if>