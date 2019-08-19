<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("nodeName", "simpleVisitorName", "superAST")}
  getRealThis().visit((ASTNode) node);
  getRealThis().visit((${nodeName}) node);
  <#if superAST.isPresent()>getRealThis().visit((${superAST.get()}) node);</#if>
  ${simpleVisitorName}.super.handle(node);
  <#if superAST.isPresent()>getRealThis().endVisit((${superAST.get()}) node);</#if>
  getRealThis().endVisit((${nodeName}) node);
  getRealThis().endVisit((ASTNode) node);