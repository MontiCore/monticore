<#-- (c) https://github.com/MontiCore/monticore -->
  <#assign name = attributeHelper.getNameLowerCase(ast)>
  <#assign Name = attributeHelper.getNameUpperCase(ast)>
  <#assign Type = attributeHelper.getType(ast)>
  <#assign typePackage = attributeHelper.getTypePackage(ast)>
  <#assign isAttributeOptional = attributeHelper.isAttributeOptional(ast)>
  <#assign fullType = typePackage?remove_ending(".tr") + "." + attributeHelper.getTypeGrammarName(ast) + "._ast.AST" + Type >
  <#if attributeHelper.isAttributeExternal(ast)>
    <#assign fullType = fullType + "Ext" >
  </#if>



    // Generating code for component node  ${Name}
    if (node.isPresent${Name}()) {
      ${typePackage}.${attributeHelper.getTypeGrammarName(ast)}tr._ast.ASTITF${Type} child_${name} = node.get${Name}();
      // a replacement, also within an optional or a list, may lack a side (e.g. opt [[ [[ x :- ]] ]])
      if (isOnLHS() && isPresentOnLHS(child_${name})) {
        state.getLhs().getODLinkList().add(createLHSComposition(node,child_${name},"${name}", "${fullType}", false, ${isAttributeOptional?string("true", "false")}));
      }
      if (isOnRHS() && isPresentOnRHS(child_${name})) {
         state.getRhs().getODLinkList().add(createRHSComposition(node,child_${name},"${name}", "${fullType}", false, ${isAttributeOptional?string("true", "false")}));
      }
    }
