<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("methodName", "parameters")}
  return getResolvedOrThrowException(${methodName}Many(<#list parameters as param>${param.getName()}<#if !param?is_last>,</#if> </#list>));