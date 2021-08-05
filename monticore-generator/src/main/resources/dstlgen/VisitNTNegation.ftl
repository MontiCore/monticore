<#-- (c) https://github.com/MontiCore/monticore -->
  @Override
  public void visit(AST${ast.getName()}_Neg node) {
    state.setNegative(true);
  }

  @Override
  public void endVisit(AST${ast.getName()}_Neg node) {
    state.setNegative(false);
  }
