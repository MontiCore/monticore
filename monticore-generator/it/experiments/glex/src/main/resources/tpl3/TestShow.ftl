<#-- (c) https://github.com/MontiCore/monticore -->
${glex.requiredGlobalVar("v3")}

${glex.defineGlobalVar("v1",33+2)}
  Var v1 is ${glex.getGlobalVar("v1")}

<#if glex.hasGlobalVar("v1")>
  Ok.
  ${glex.changeGlobalVar("v1","Aha")}
</#if>

${glex.defineGlobalVar("v2",[])}
${glex.addToGlobalVar("v2",16)}
${glex.addToGlobalVar("v2",[18,19])}
${glex.addToGlobalVar("v2",17)}
<#list glex.getGlobalVar("v2") as elem> ${elem},</#list>
