<#-- (c) https://github.com/MontiCore/monticore -->
<#assign name = attributeHelper.getNameLowerCase(ast)>
<#assign Name = attributeHelper.getNameUpperCase(ast)>
   if(!state.isNegative()){
      if (node.isPresent${Name}()
                            && node.get${Name}().getIdentifier().startsWith("$")
                            && ! node.get${Name}().getIdentifier().equals("$_")
                            && ! state.getVariable2Attributes().contains(node.get${Name}().getIdentifier())) {
          state.getVariable2Attributes().addDeclaration(node.get${Name}().getIdentifier(), node, "${name}");
      }
    }
