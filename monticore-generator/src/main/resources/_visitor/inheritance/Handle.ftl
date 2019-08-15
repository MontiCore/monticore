<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("nodeName", "simpleVisitorName")}
  getRealThis().visit((ASTNode) node);
  getRealThis().visit((${nodeName}) node);
  ${simpleVisitorName}.super.handle(node);
  getRealThis().endVisit((${nodeName}) node);
  getRealThis().endVisit((ASTNode) node);