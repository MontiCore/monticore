<#-- (c) https://github.com/MontiCore/monticore -->
<#assign name = attributeHelper.getNameLowerCase(ast)>
<#assign Name = attributeHelper.getNameUpperCase(ast)>
<#assign nameList = name + "List">
<#assign isAttributeOptional = attributeHelper.isAttributeOptional(ast)>

    // Generating code for attribute ${Name}
    if (node.get${Name}List() != null && !node.get${Name}List().isEmpty()) {
      ASTODAttribute attr_${name}_lhs =  ODRulesMill.oDAttributeBuilder().uncheckedBuild();
      ASTCardinality cardinality = ODRulesMill.cardinalityBuilder().uncheckedBuild();
      cardinality.setMany(${isAttributeOptional?string("true", "false")});
      cardinality.setOneToMany(!${isAttributeOptional?string("true", "false")});
      attr_${name}_lhs.setAttributeCardinality(cardinality);

      ASTMCBasicGenericType attr_${name}_type = ODRulesMill.mCBasicGenericTypeBuilder().uncheckedBuild();
      attr_${name}_type.setNameList(Splitters.DOT.splitToList("java.util.List"));

      ASTMCQualifiedName attr_${name}_qual_name = ODRulesMill.mCQualifiedNameBuilder().uncheckedBuild();
      attr_${name}_qual_name.setPartsList(Splitters.DOT.splitToList("String"));
      ASTMCQualifiedType attr_${name}_qual_type = ODRulesMill.mCQualifiedTypeBuilder().uncheckedBuild();
      attr_${name}_qual_type.setMCQualifiedName(attr_${name}_qual_name);

      ASTMCCustomTypeArgument attr_${name}_typeArgument = ODRulesMill.mCCustomTypeArgumentBuilder().uncheckedBuild();
      attr_${name}_typeArgument.setMCType(attr_${name}_qual_type);

      attr_${name}_type.addMCTypeArgument(attr_${name}_typeArgument);
      attr_${name}_lhs.setMCType(attr_${name}_type);

      ASTArrayInit attr_${name}_lhs_value = ODRulesMill.arrayInitBuilder().uncheckedBuild();
      attr_${name}_lhs.setList(attr_${name}_lhs_value);


      attr_${name}_lhs.setName("${name}");
      for (ASTTfIdentifier n : node.get${Name}List()) {
        if (n.isIdentifierFix()) {
          String fixName = n.getIdentifier();
          if (fixName.startsWith("\\$")) {
              fixName = fixName.substring(1);
          }
          ASTStringLiteral value = ODRulesMill.stringLiteralBuilder().uncheckedBuild();
          value.setSource(fixName);
          ASTLiteralExpression literalExpression = ODRulesMill.literalExpressionBuilder().setLiteral(value).build();
          ASTVariableInit expression = ODRulesMill.simpleInitBuilder().setExpression(literalExpression).build();
          attr_${name}_lhs_value.getVariableInitList().add(expression);
        } else if (!n.getIdentifier().equals("$_")) {
          String attr_${name}_lhs_variable_name = n.getIdentifier();
          if (state.getVariable2Attributes().contains(attr_${name}_lhs_variable_name)) {
            ITFObject o = state.getVariable2Attributes().getObject(attr_${name}_lhs_variable_name);
            String o_name = state.getNameGen().getNameForElement(o,state.getParents());
            String o_attr_${name} = state.getVariable2Attributes().getAttributeName(attr_${name}_lhs_variable_name);
            // check whether this is a different object
            if (o != node || !(o_attr_${name}.equals("${name}") || o_attr_${name}.startsWith("${nameList}.get("))) {
              List<String> attr_${name}_value_string = Arrays.asList(o_name, o_attr_${name});
              ASTVariableInit expression = ODRulesMill.simpleInitBuilder()
                                    .setExpression(createQualifiedNameExpression(attr_${name}_value_string)).build();
              attr_${name}_lhs_value .getVariableInitList().add(expression);
            }else {
              ASTVariableInit expression = ODRulesMill.simpleInitBuilder()
                      .setExpression(createPrimaryExpressionForString(attr_${name}_lhs_variable_name)).build();
              attr_${name}_lhs_value.getVariableInitList().add(expression);
            }
          }else {
            ASTVariableInit expression = ODRulesMill.simpleInitBuilder()
                       .setExpression(createPrimaryExpressionForString(attr_${name}_lhs_variable_name)).build();
            attr_${name}_lhs_value.getVariableInitList().add(expression);
          }
        }
      }
      if(!attr_${name}_lhs_value.getVariableInitList().isEmpty()){
        o_lhs.getAttributesList().add(attr_${name}_lhs);
      }

      ASTODAttribute attr_${name}_rhs = ODRulesMill.oDAttributeBuilder().uncheckedBuild();
      cardinality = ODRulesMill.cardinalityBuilder().uncheckedBuild();
      cardinality.setMany(${isAttributeOptional?string("true", "false")});
      cardinality.setOneToMany(!${isAttributeOptional?string("true", "false")});
      attr_${name}_rhs.setAttributeCardinality(cardinality);
      attr_${name}_rhs.setMCType(attr_${name}_type.deepClone());

      ASTArrayInit attr_${name}_rhs_value = ODRulesMill.arrayInitBuilder().uncheckedBuild();
      attr_${name}_rhs.setList(attr_${name}_rhs_value);

      for (ASTTfIdentifier n : node.get${Name}List()) {
        String attr_${name}_rhs_variable_name;
        boolean attr_${name}_rhs_identifier_fix;
        attr_${name}_rhs.setName("${name}");


        if (state.getPosition() == Position.RHS) {
          attr_${name}_rhs_identifier_fix = n.isIdentifierFix();
          attr_${name}_rhs_variable_name = n.isPresentIdentifier() ? n.getIdentifier() : null;
        } else {
          attr_${name}_rhs_identifier_fix = n != null && n.isPresentNewIdentifier() && n.isNewIdentifierFix();
          attr_${name}_rhs_variable_name = n.isPresentNewIdentifier() ? n.getNewIdentifier() : null;
        }
        if (attr_${name}_rhs_variable_name!= null && attr_${name}_rhs_identifier_fix) {
          attr_${name}_rhs.setName("${name}");
          String fixName = attr_${name}_rhs_variable_name;
          if (fixName.startsWith("\\$")) {
            fixName = fixName.substring(1);
          }
          ASTStringLiteral value = ODRulesMill.stringLiteralBuilder().uncheckedBuild();
          value.setSource(fixName);
          ASTLiteralExpression literalExpression = ODRulesMill.literalExpressionBuilder().setLiteral(value).build();
          ASTVariableInit expression = ODRulesMill.simpleInitBuilder().setExpression(literalExpression).build();
          attr_${name}_rhs_value.getVariableInitList().add(expression);
        } else {
          if (state.getVariable2Attributes().contains(attr_${name}_rhs_variable_name)) {
            ITFObject o = state.getVariable2Attributes().getObject(attr_${name}_rhs_variable_name);
            String o_name = state.getNameGen().getNameForElement(o, state.getParents());
            String o_attr_${name} = state.getVariable2Attributes().getAttributeName(attr_${name}_rhs_variable_name);
            // check whether this is a different object
            if (o != node || !o_attr_${name}.equals("${name}")) {
              List<String> attr_${name}_value_string = Arrays.asList(o_name, o_attr_${name});
              ASTVariableInit expression = ODRulesMill.simpleInitBuilder()
                                    .setExpression(createQualifiedNameExpression(attr_${name}_value_string)).build();
              attr_${name}_rhs_value .getVariableInitList().add(expression);
            }
          } else if (attr_${name}_rhs_variable_name != null) {
            ASTODAttribute attr_${name} = ODRulesMill.oDAttributeBuilder().uncheckedBuild();
            cardinality = ODRulesMill.cardinalityBuilder().uncheckedBuild();
            cardinality.setMany(${isAttributeOptional?string("true", "false")});
            cardinality.setOneToMany(!${isAttributeOptional?string("true", "false")});
            attr_${name}.setAttributeCardinality(cardinality);
            List<String> attr_${name}_value_string = Arrays.asList(attr_${name}_rhs_variable_name);
            ASTStringLiteral value = ODRulesMill.stringLiteralBuilder().uncheckedBuild();
            value.setSource(Names.getQualifiedName(attr_${name}_value_string));
            ASTLiteralExpression literalExpression = ODRulesMill.literalExpressionBuilder().setLiteral(value).build();
            ASTVariableInit expression = ODRulesMill.simpleInitBuilder()
                    .setExpression(literalExpression).build();
            attr_${name}_rhs_value.getVariableInitList().add(expression);
          }
        }
      }
      if(!attr_${name}_rhs_value .getVariableInitList().isEmpty()){
        o_rhs.getAttributesList().add(attr_${name}_rhs);
      }
    }
