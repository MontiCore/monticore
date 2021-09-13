<#-- (c) https://github.com/MontiCore/monticore -->
    @Override
    public void visit(AST${ast.getName()}_Pat node) {
        // create objects
      ASTODObject o_lhs = ODRulesMill.oDObjectBuilder().uncheckedBuild();
      if (isOnLHS()) {
        handleLHS(node, o_lhs);
        addObjectToLHS(o_lhs);
      }
      ASTODObject o_rhs = ODRulesMill.oDObjectBuilder().uncheckedBuild();

      <#if ast.getSymbol().isIsInterface()  >
        // This thing does not have a builder class, store this on the stereotype
        if(!o_rhs.isPresentStereotype())
          o_rhs.setStereotype(de.monticore.tf.odrules.ODRulesMill.stereotypeBuilder().build());
        o_rhs.getStereotype().addValues(de.monticore.tf.odrules.ODRulesMill.stereoValueBuilder().setName("noBuilder").setContent("i").build());
      </#if>

      if (isOnRHS()) {
        handleRHS(node, o_rhs);
        addObjectToRHS(o_rhs);
      }


        // process attributes
            <#if grammarInfo.getStringAttrs(ast.getName())?? >
        ${tc.include(process_string_attrs, grammarInfo.getStringAttrs(ast.getName()))}

        ${tc.include(process_stringlist_attrs, grammarInfo.getStringListAttrs(ast.getName()))}

         ${glex.setGlobalValue("attrIterated", false)}
        ${tc.include(process_boolean_alt_attrs_pattern, grammarInfo.getBooleanAltAttrs(ast.getName()))}
        ${tc.include(process_boolean_attrs_pattern, grammarInfo.getBooleanAttrs(ast.getName()))}

        ${glex.setGlobalValue("attrIterated", true)}
        ${tc.include(process_boolean_attrs_pattern, grammarInfo.getBooleanListAttrs(ast.getName()))}

        // create compositions
        ${tc.include(process_component_lists_pattern, grammarInfo.getComponentLists(ast.getName()))}
        ${tc.include(process_component_nodes_pattern, grammarInfo.getComponentNodes(ast.getName()))}
            </#if>
    }
