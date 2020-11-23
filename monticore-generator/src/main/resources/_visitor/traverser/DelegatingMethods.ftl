<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleHandlerNameList", "methodName")}
<#list simpleHandlerNameList as simpleVisitorName>
  if (get${simpleVisitorName}().isPresent()) {
    get${simpleVisitorName}().get().${methodName}(node);
  }
</#list>