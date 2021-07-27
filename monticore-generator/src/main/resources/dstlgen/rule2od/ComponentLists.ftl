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
    // Generating code for component list  ${Name}

    for (${typePackage}.${attributeHelper.getTypeGrammarName(ast)}tr._ast.ASTITF${Type} child: node.get${Name}List()) {
      if ((isOnLHS()) && ( !(child instanceof IReplacement) || ((IReplacement)child).isPresentLhs())) {
        state.getLhs().getODLinkList().add(createLHSComposition(node,child, "${name}", "${fullType}", true, ${isAttributeOptional?string("true", "false")}));
      }
      if (isOnRHS()) {
        if (!(child instanceof IReplacement)) {
          state.getRhs().getODLinkList().add(createRHSComposition(node, child, "${name}", "${fullType}", true, ${isAttributeOptional?string("true", "false")}));
        }
        else if (((IReplacement) child).isPresentRhs()) {
          IReplacement replacement = (IReplacement) child;
          if (replacement.getReplacementOp().isFirst()) {
            state.getRhs().getODLinkList().add(createLinkInsertAt(node, "${name}", "${fullType}", (ITFObject) replacement.getRhs(), "first", true, ${isAttributeOptional?string("true", "false")}));
          }
          else if (replacement.getReplacementOp().isLast()) {
            state.getRhs().getODLinkList().add(createLinkInsertAt(node, "${name}", "${fullType}", (ITFObject) replacement.getRhs(), "last", true, ${isAttributeOptional?string("true", "false")}));
          }
          else if (replacement.getReplacementOp().isRelative()) {
            ITFElement beforeChild = node.get${Name}(node.get${Name}List().indexOf(child) - 1);
            if(beforeChild instanceof IReplacement)
                beforeChild = ((IReplacement) beforeChild).getRhs();
            state.getRhs().getODLinkList().add(createLinkInsertAt(node, "${name}", "${fullType}", (ITFObject) replacement.getRhs(), "relative", (ITFObject)  beforeChild, true, ${isAttributeOptional?string("true", "false")}));
          }
          else if (replacement.getReplacementOp().isInplace() || ((IReplacement) child).isPresentLhs()) {
            state.getRhs().getODLinkList().add(createLinkInsertAt(node, "${name}", "${fullType}", (ITFObject) replacement.getRhs(), "inplace", (ITFObject)  replacement.getLhs(), true, ${isAttributeOptional?string("true", "false")}));
          }
          else {
            state.getRhs().getODLinkList().add(createRHSComposition(node, child, "${name}", "${fullType}", true, ${isAttributeOptional?string("true", "false")}));
          }
        }
      }
    }
