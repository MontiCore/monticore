<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("glex", "hpService", "cd")}

<#-- Override the general empty method body with a warning -->
<#assign hpWarn = hpService.templateHP("WarnOnEmptyBody")>
${glex.replaceTemplate("cd2java.EmptyBody", hpWarn)}


<#-- Override the specific sizeStates method of the automaton to always return 10 -->
<#assign sizeStateMethod = cd.getCDElementList()?first.getCDElementList()?first.getCDMemberList()[17]>
<#assign hpSize = hpService.templateHP("SizeStateTen")>
${glex.replaceTemplate("cd2java.EmptyBody", sizeStateMethod, hpSize)}

<#--   Add method and attribute to automaton -->
<#assign autClass = cd.getCDElementList()?first.getCDElementList()?first>
${cd4c.addAttribute(autClass, "int counter = 0;")}
${cd4c.addMethod(autClass, "Counter")}