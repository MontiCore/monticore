<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleHandlerNameList", "methodName")}
<#list simpleHandlerNameList as simpleVisitorName>
  if (getRealThis().get${simpleVisitorName}().isPresent()) {
    getRealThis().get${simpleVisitorName}().get().${methodName}(node);
  }
</#list>