<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("glex", "hpService")}
<#assign hp = hpService.templateHP("foo.WarnOnEmptyBody")>
${glex.replaceTemplate("core.EmptyBody", hp)}
