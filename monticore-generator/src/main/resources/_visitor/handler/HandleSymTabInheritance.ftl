<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("simpleHandlerName", "superList")}
<#assign reversedList = superList?reverse>
<#list reversedList as superAST>
  getTraverser().visit((${superAST}) node);
</#list>
  ${simpleHandlerName}.super.handle(node);
<#list superList as superAST>
  getTraverser().endVisit((${superAST}) node);
</#list>
