<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("glex", "hpService")}
<#assign hp = hpService.templateHP("WarnOnEmptyBody")>
${glex.replaceTemplate("core.EmptyBody", hp)}
