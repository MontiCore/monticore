<#-- (c) https://github.com/MontiCore/monticore -->

    @Override
    public void endVisit(AST${ast.getName()}_List node) {
        if (isOnLHS()) {
            handleListLHS(node,  node.get${ast.getName()}());
        }

        if (isOnRHS()) {
            handleListRHS(node, node.get${ast.getName()}());
        }
    }
