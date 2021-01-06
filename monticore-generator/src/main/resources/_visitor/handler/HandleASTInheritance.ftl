<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("nodeName", "simpleHandlerName", "superASTList")}
  getTraverser().visit((ASTNode) node);
  getTraverser().visit((${nodeName}) node);
<#assign reversedList = superASTList?reverse>
<#list reversedList as superAST>
  getTraverser().visit((${superAST}) node);
</#list>
  ${simpleHandlerName}.super.handle(node);
<#list superASTList as superAST>
  getTraverser().endVisit((${superAST}) node);
</#list>
  getTraverser().endVisit((${nodeName}) node);
  getTraverser().endVisit((ASTNode) node);