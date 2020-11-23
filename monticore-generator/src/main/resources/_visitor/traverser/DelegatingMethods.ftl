<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleVisitorNameList", "methodName")}
<#list simpleVisitorNameList as simpleVisitorName>
  get${simpleVisitorName}List().forEach(v -> v.endVisit(node));
</#list>