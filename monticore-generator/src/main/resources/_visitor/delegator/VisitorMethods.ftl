<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleVisitorNameList", "methodName")}
<#list simpleVisitorNameList as simpleVisitorName>
  if (getRealThis().get${simpleVisitorName}().isPresent()) {
    getRealThis().getget${simpleVisitorName}().get().get${methodName}(node);
  }
</#list>