<#-- (c) https://github.com/MontiCore/monticore -->
${glex.defineGlobalVar("v1",[])}
${glex.addToGlobalVar("v1",16)}
${glex.addToGlobalVar("v1",17)}
A:<#list glex.getGlobalVar("v1") as x>${x},</#list>
${glex.defineGlobalVar("v2",[23,25])}
${glex.addToGlobalVar("v2",27)}
B:<#list glex.getGlobalVar("v2") as x>${x},</#list>
${glex.defineGlobalVar("v3",34)}
${glex.addToGlobalVar("v3",36)}
C:<#list glex.getGlobalVar("v3") as x>${x},</#list>
