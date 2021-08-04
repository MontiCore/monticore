<#-- (c) https://github.com/MontiCore/monticore -->
<#assign name = attributeHelper.getNameLowerCase(ast)>
<#assign Name = attributeHelper.getNameUpperCase(ast)>
if(!state.isNegative()) {
  for (ASTTfIdentifier n : node.get${Name}List()) {
    if (n.isPresentIdentifier()
         && n.getIdentifier().startsWith("$")
         && !n.getIdentifier().equals("$_")
         && !state.getVariable2Attributes().contains(n.getIdentifier())) {
            state.getVariable2Attributes().addDeclaration(n.getIdentifier(), node,
            "${name}List.get(" + node.get${Name}List().indexOf(n) + ")", true);
    }
  }
}
