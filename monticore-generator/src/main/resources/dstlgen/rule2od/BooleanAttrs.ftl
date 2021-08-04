<#-- (c) https://github.com/MontiCore/monticore -->
<#assign name = attributeHelper.getNameLowerCase(ast)>
<#assign Name = attributeHelper.getNameUpperCase(ast)>
<#assign isAttributeOptional = ast.getSymbol().isIsOptional()>
<#if attrIterated>
  <#assign get = "get(0)" >
  <#assign object = "constant" >
<#else >
  <#assign object = "node.get"+Name+"()">
</#if>


    // Generating code for attribute ${Name}

<#if attrIterated>
  if (node.get${Name}List() != null && node.get${Name}List().size()>0 && !(node.get${Name}List().get(0) instanceof IAttributeOptional)) {
    for(ASTITF${grammarName}_${Name}_Constant ${object} : node.get${Name}List()){
<#else>
  if (node.isPresent${Name}() && !(node.get${Name}() instanceof IAttributeOptional)) {
</#if>
    boolean attr_${name}_lhs = isOnLHS()  &&
    (!(${object} instanceof IAttributeReplacement)
       || ((IAttributeReplacement)${object}).isPresentLhs());
    boolean attr_${name}_rhs =
    (!(${object} instanceof IAttributeReplacement)
       || ((IAttributeReplacement)${object}).isPresentRhs());

    if (attr_${name}_lhs && attr_${name}_rhs) {
      ASTODAttribute od_attr_${name}_lhs = createAttributeForBoolean("${name}", createExpressionForBoolean(ASTConstantsMCCommonLiterals.TRUE), ${isAttributeOptional?string("true", "false")});
      if (${object} instanceof AST${grammarName}_${Name}_Constant_Neg) {
        od_attr_${name}_lhs.setSingleValue(createExpressionForBoolean(ASTConstantsMCCommonLiterals.FALSE));
      }
      o_lhs.getAttributesList().add(od_attr_${name}_lhs);
    }

    if (attr_${name}_lhs && !attr_${name}_rhs) {
      ASTODAttribute od_attr_${name}_lhs = createAttributeForBoolean("${name}", createExpressionForBoolean(ASTConstantsMCCommonLiterals.TRUE), ${isAttributeOptional?string("true", "false")});
      if (${object} instanceof AST${grammarName}_${Name}_Constant_Neg) {
        od_attr_${name}_lhs.setSingleValue(createExpressionForBoolean(ASTConstantsMCCommonLiterals.FALSE));
      }
      o_lhs.getAttributesList().add(od_attr_${name}_lhs);
      ASTODAttribute od_attr_${name}_rhs = createAttributeForBoolean("${name}", createExpressionForBoolean(ASTConstantsMCCommonLiterals.FALSE), ${isAttributeOptional?string("true", "false")});
      o_rhs.getAttributesList().add(od_attr_${name}_rhs);
    }

    if (!attr_${name}_lhs && attr_${name}_rhs) {
      if (((de.monticore.tf.ast.ITFAttribute) ${object}).getTFElement() instanceof AST${grammarName}_${Name}_Constant_Neg) {
        ASTODAttribute od_attr_${name}_lhs = createAttributeForBoolean("${name}", createExpressionForBoolean(ASTConstantsMCCommonLiterals.FALSE), ${isAttributeOptional?string("true", "false")});
        o_lhs.getAttributesList().add(od_attr_${name}_lhs);
      }
      ASTODAttribute od_attr_${name}_rhs = createAttributeForBoolean("${name}", createExpressionForBoolean(ASTConstantsMCCommonLiterals.TRUE), ${isAttributeOptional?string("true", "false")});
      o_rhs.getAttributesList().add(od_attr_${name}_rhs);
    }
   <#if attrIterated>
    }
   </#if>
  }
