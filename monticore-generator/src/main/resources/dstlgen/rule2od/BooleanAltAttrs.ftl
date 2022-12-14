<#-- (c) https://github.com/MontiCore/monticore -->
<#assign name = attributeHelper.getNameLowerCase(ast)>
<#assign Name = attributeHelper.getNameUpperCase(ast)>

    // Generating code for attribute ${Name}

  if(isOnLHS()) {
    if (node.isPresent${Name}() && node.get${Name}() instanceof AST${grammarName}_${Name}_Constant_Pat) {
      AST${grammarName}_${Name}_Constant_Pat mt_${name} = (AST${grammarName}_${Name}_Constant_Pat) node.get${Name}();

      ASTMCPrimitiveType type = ODRulesMill.mCPrimitiveTypeBuilder().uncheckedBuild();
      type.setPrimitive(ASTConstantsMCBasicTypes.INT);
      ASTODAttribute attribute = ODRulesMill.oDAttributeBuilder().uncheckedBuild();
      ASTCardinality cardinality = ODRulesMill.cardinalityBuilder().uncheckedBuild();
      cardinality.setOne(true);
      attribute.setAttributeCardinality(cardinality);
      attribute.setName("${name}");
      attribute.setMCType(type);
      attribute.setSingleValue(createExpressionForInt(mt_${name}.get${Name}()));
      o_lhs.addAttributes(attribute);
    }
  }
  if(isOnRHS()) {
    if (node.isPresent${Name}() && node.get${Name}() instanceof AST${grammarName}_${Name}_Constant_Pat) {
      AST${grammarName}_${Name}_Constant_Pat mt_${name} = (AST${grammarName}_${Name}_Constant_Pat) node.get${Name}();

      ASTMCPrimitiveType type = ODRulesMill.mCPrimitiveTypeBuilder().uncheckedBuild();
      type.setPrimitive(ASTConstantsMCBasicTypes.INT);
      ASTODAttribute attribute = ODRulesMill.oDAttributeBuilder().uncheckedBuild();
      ASTCardinality cardinality = ODRulesMill.cardinalityBuilder().uncheckedBuild();
      cardinality.setOne(true);
      attribute.setAttributeCardinality(cardinality);
      attribute.setName("${name}");
      attribute.setMCType(type);
      attribute.setSingleValue(createExpressionForInt(mt_${name}.get${Name}()));
      o_rhs.addAttributes(attribute);
    }
  }
