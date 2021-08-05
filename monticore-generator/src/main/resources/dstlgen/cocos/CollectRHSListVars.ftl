<#-- (c) https://github.com/MontiCore/monticore -->
<#assign name = attributeHelper.getNameLowerCase(ast)>
<#assign Name = attributeHelper.getNameUpperCase(ast)>
    for (ASTTfIdentifier n : node.get${Name}List()) {
        if (n.isPresentIdentifier()) {
            if (state.getRHS()) {
                state.addVarOnRHS(n.getIdentifier());
            } else {
                state.addVarOnLHS(n.getIdentifier());
            }
        }
        if (n.isPresentNewIdentifier()) {
            state.addVarOnRHS(n.getIdentifier());
        }
    }
