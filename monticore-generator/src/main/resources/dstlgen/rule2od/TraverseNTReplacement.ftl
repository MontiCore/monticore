<#-- (c) https://github.com/MontiCore/monticore -->
    @Override
    public void traverse(AST${ast.getName()}_Rep node) {
      // as long as nested replacements are forbidden, this should be BOTH
    Position oldPos = state.getPosition();
    state.setPosition(Position.LHS);
    if(node.isPresentLhs()){
      node.getLhs().accept(getTraverser());
    }
    state.setPosition(Position.RHS);
    if(node.isPresentRhs()){
      node.getRhs().accept(getTraverser());
    }
    state.setPosition(oldPos);
    }
