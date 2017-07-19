<#if getGlobalVar("test")??>
${error("global test variable does not exist")}
</#if>

<#if getGlobalVar("a")??>
${error("global a variable does not exist")}
</#if>

<#if getGlobalVar("b")??>
${error("global b variable does not exist")}
</#if>

<#if getGlobalVar("asd")??>
${getGlobalVar("asd")}
<#else>
${error("global variables does not exist")}
</#if>
