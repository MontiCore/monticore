<#-- (c) https://github.com/MontiCore/monticore -->

    @Override
    public void traverse(AST${ast.getName()}_Rep node) {
      if(node.isPresentLhs()){
        node.getLhs().accept(this.getTraverser());
      }
    }
