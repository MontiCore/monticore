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
      if ((isOnLHS()) && ( !(child_${name} instanceof IReplacement) || ((IReplacement)child_${name}).isPresentLhs())) {
        state.getLhs().getODLinkList().add(createLHSComposition(node,child_${name},"${name}", "${fullType}", false, ${isAttributeOptional?string("true", "false")}));
      }
      if ((isOnRHS()) && (!(child_${name} instanceof IReplacement) || ((IReplacement)child_${name}).isPresentRhs()) ) {
         state.getRhs().getODLinkList().add(createRHSComposition(node,child_${name},"${name}", "${fullType}", false, ${isAttributeOptional?string("true", "false")}));
      }
    }
