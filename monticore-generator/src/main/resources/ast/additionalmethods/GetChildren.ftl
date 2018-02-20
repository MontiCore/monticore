<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "type")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
    java.util.LinkedList<de.monticore.ast.ASTNode> result = new java.util.LinkedList<de.monticore.ast.ASTNode>();
    <#list type.getAllVisibleFields() as field>
      <#assign attrGetter = genHelper.getPlainGetter(field)>
      <#if genHelper.isAstNode(field)>
        if ( ${attrGetter}() != null ) {
          result.add(${attrGetter}());
        }
      <#elseif genHelper.isOptionalAstNode(field)>
        if (${attrGetter}().isPresent()) {
          result.add(${attrGetter}().get());
        }
      <#elseif genHelper.isListAstNode(field)>
        result.addAll(${attrGetter}());
      </#if>
    </#list>
    return result;
