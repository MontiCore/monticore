<#-- (c) https://github.com/MontiCore/monticore -->
<#assign name = attributeHelper.getNameLowerCase(ast)>
<#assign Name = attributeHelper.getNameUpperCase(ast)>
<#assign isAttributeOptional = attributeHelper.isAttributeOptional(ast)>

    // Generating code for attribute ${Name}

    if (node.isPresent${Name}()) {
      if (node.get${Name}().isIdentifierFix()) {
         ASTODAttribute attr_${name} = createAttributeForString("${name}", ${isAttributeOptional?string("true", "false")});
         String fixName = node.get${Name}().getIdentifier();
         if (fixName.startsWith("\\$")) {
           fixName = fixName.substring(1);
         }
         attr_${name}.setSingleValue(createPrimaryExpressionForString(fixName));
         o_lhs.getAttributesList().add(attr_${name});
      } else if (!node.get${Name}().getIdentifier().equals("$_")) {
        String attr_${name}_lhs_variable_name = node.get${Name}().getIdentifier();
        if (state.getVariable2Attributes().contains(attr_${name}_lhs_variable_name)) {
          ITFObject o = state.getVariable2Attributes().getObject(attr_${name}_lhs_variable_name);
          String o_name = state.getNameGen().getNameForElement(o, state.getParents());
          String o_attr_name = state.getVariable2Attributes().getAttributeName(attr_${name}_lhs_variable_name);
          if (o != node || !o_attr_name.equals("${name}<#if attributeHelper.isAttributeOptional(ast)>.get()</#if>")) {
            ASTODAttribute attr_${name} = createAttributeForString("${name}", ${isAttributeOptional?string("true", "false")});
            createQualifiedNameExpressionAsAttributeValue(attr_${name}, o_name, o_attr_name);
            o_lhs.getAttributesList().add(attr_${name});
          } else {
            ASTODAttribute attr_${name} = createAttributeForString("${name}", ${isAttributeOptional?string("true", "false")});
            attr_${name}.setSingleValue(createPrimaryExpressionForString(attr_${name}_lhs_variable_name));
            o_lhs.getAttributesList().add(attr_${name});
          }
        } else {
          ASTODAttribute attr_${name} = createAttributeForString("${name}", ${isAttributeOptional?string("true", "false")});
          attr_${name}.setSingleValue(createPrimaryExpressionForString(attr_${name}_lhs_variable_name));
          o_lhs.getAttributesList().add(attr_${name});
        }
      }

      String attr_${name}_rhs_variable_name;
      boolean attr_${name}_rhs_identifier_fix;
      if (state.getPosition() == Position.RHS) {
          attr_${name}_rhs_identifier_fix = node.isPresent${Name}() && node.get${Name}().isIdentifierFix();
          attr_${name}_rhs_variable_name = node.get${Name}().isPresentIdentifier() ? node.get${Name}().getIdentifier() : null;
      } else {
          attr_${name}_rhs_identifier_fix = node.isPresent${Name}() && node.get${Name}().isPresentNewIdentifier() && node.get${Name}().isNewIdentifierFix();
          attr_${name}_rhs_variable_name = node.get${Name}().isPresentNewIdentifier() ? node.get${Name}().getNewIdentifier() : null;
      }
      if (attr_${name}_rhs_variable_name!= null && attr_${name}_rhs_identifier_fix) {
        ASTODAttribute attr_${name} = createAttributeForString("${name}", ${isAttributeOptional?string("true", "false")});
        String fixName = attr_${name}_rhs_variable_name;
        if (fixName.startsWith("\\$")) {
            fixName = fixName.substring(1);
        }
        attr_${name}.setSingleValue(createPrimaryExpressionForString(fixName));
        o_rhs.getAttributesList().add(attr_${name});
      } else {
        if (state.getVariable2Attributes().contains(attr_${name}_rhs_variable_name)) {
          de.monticore.tf.ast.ITFObject o = state.getVariable2Attributes().getObject(attr_${name}_rhs_variable_name);
          String o_name = state.getNameGen().getNameForElement(o, state.getParents());
          String o_attr_name = state.getVariable2Attributes().getAttributeName(attr_${name}_rhs_variable_name);
          if (o != node || !o_attr_name.equals("${name}<#if isAttributeOptional>.get()</#if>")) {
            ASTODAttribute attr_${name} = createAttributeForString("${name}", ${isAttributeOptional?string("true", "false")});
            createQualifiedNameExpressionAsAttributeValue(attr_${name}, o_name, o_attr_name);
            o_rhs.getAttributesList().add(attr_${name});
          }
        } else if (attr_${name}_rhs_variable_name != null) {
          ASTODAttribute attr_${name} = createAttributeForString("${name}", ${isAttributeOptional?string("true", "false")});
          createQualifiedNameExpressionAsAttributeValue(attr_${name}, attr_${name}_rhs_variable_name);
          o_rhs.getAttributesList().add(attr_${name});
        }
      }
    }
