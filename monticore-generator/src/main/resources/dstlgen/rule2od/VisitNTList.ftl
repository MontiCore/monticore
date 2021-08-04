<#-- (c) https://github.com/MontiCore/monticore -->
@Override
public void visit(AST${ast.getName()}_List node) {
  handleList(node);
}

@Override
public void endVisit(AST${ast.getName()}_List node) {
  state.getHierarchyLHS().pop();
  state.getHierarchyRHS().pop();
}
