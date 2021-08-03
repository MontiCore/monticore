<#-- (c) https://github.com/MontiCore/monticore -->
  public void visit(AST${ast.getName()}_Opt node) {
    handleOpt(node);
  }


  public void endVisit(AST${ast.getName()}_Opt node) {
    state.getHierarchyLHS().pop();
    state.getHierarchyRHS().pop();
  }
