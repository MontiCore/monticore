<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdClass")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
    java.util.LinkedList<de.monticore.ast.ASTNode> result = new java.util.LinkedList<de.monticore.ast.ASTNode>();
    <#list cdClass.getCDAttributeList() as attr>
      <#assign attrGetter = genHelper.getPlainGetter(attr)>
      <#if genHelper.isAstNode(attr)>
        if ( ${attrGetter}() != null ) {
          result.add(${attrGetter}());
        }
      <#elseif genHelper.isOptionalAstNode(attr)>
        if (${attrGetter}().isPresent()) {
          result.add(${attrGetter}().get());
        }
      <#elseif genHelper.isListAstNode(attr)>
        result.addAll(${attrGetter}());
      </#if>
    </#list>
    return result;
