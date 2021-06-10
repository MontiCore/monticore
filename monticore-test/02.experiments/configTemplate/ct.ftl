<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("glex", "hpService")}
<#assign hp = hpService.templateHP("WarnOnEmptyBody.ftl")>
${glex.replaceTemplate("core.EmptyBody", hp)}
