<#-- (c) https://github.com/MontiCore/monticore -->
<#assign name = attributeHelper.getNameLowerCase(ast)>
<#assign Name = attributeHelper.getNameUpperCase(ast)>
    if (node.isPresent${Name}() && node.get${Name}().isPresentIdentifier()) {
        if (state.getRHS()) {
            state.addVarOnRHS(node.get${Name}().getIdentifier());
        } else {
            state.addVarOnLHS(node.get${Name}().getIdentifier());
        }
    }
    if (node.isPresent${Name}() && node.get${Name}().isPresentNewIdentifier()) {
        state.addVarOnRHS(node.get${Name}().getNewIdentifier());
    }
