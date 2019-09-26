<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleVisitorNameList", "methodName")}
<#list simpleVisitorNameList as simpleVisitorName>
  if (getRealThis().get${simpleVisitorName}().isPresent()) {
    getRealThis().get${simpleVisitorName}().get().${methodName}(node);
  }
</#list>