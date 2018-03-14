<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "methodName")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  return ${methodName}().isPresent();