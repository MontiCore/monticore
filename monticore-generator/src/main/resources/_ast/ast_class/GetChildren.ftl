<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdClass")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
    java.util.LinkedList<de.monticore.ast.ASTNode> result = new java.util.LinkedList<de.monticore.ast.ASTNode>();
    <#list cdClass.getCDAttributeList() as attr>
      <#assign attrGetter = genHelper.getPlainGetter(attr)>
      <#if genHelper.isSimpleAstNode(attr)>
        if ( ${attrGetter}() != null ) {
          result.add(${attrGetter}());
        }
      <#elseif genHelper.isOptionalAstNode(attr)>
        if (isPresent${genHelper.getNativeAttributeName(attr.getName())?cap_first}()) {
          result.add(${attrGetter}());
        }
      <#elseif genHelper.isListAstNode(attr)>
        result.addAll(${attrGetter}());
      </#if>
    </#list>
    return result;
