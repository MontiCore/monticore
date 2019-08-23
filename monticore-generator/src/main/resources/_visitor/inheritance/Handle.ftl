<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("nodeName", "simpleVisitorName", "superASTList")}
  getRealThis().visit((ASTNode) node);
  getRealThis().visit((${nodeName}) node);
<#assign reversedList = superASTList?reverse>
<#list reversedList as superAST>
  getRealThis().visit((${superAST}) node);
</#list>
  ${simpleVisitorName}.super.handle(node);
<#list superASTList as superAST>
  getRealThis().endVisit((${superAST}) node);
</#list>
  getRealThis().endVisit((${nodeName}) node);
  getRealThis().endVisit((ASTNode) node);