<#-- (c) https://github.com/MontiCore/monticore -->
// One might think that we could call traverse(subelement) immediately,
// but this is not true for interface-types where we do not know the
// concrete type of the element.
// Instead we double-dispatch the call, to call the correctly typed
// traverse(...) method with the elements concrete type.
${tc.signature("cdClass")}

<#list cdClass.getCDAttributeList() as attr>
  <#if genHelper.isAstNode(attr) || genHelper.isOptionalAstNode(attr) >
    <#assign attrGetter = "get"+ attr.getName()?cap_first>
    <#if genHelper.isOptional(attr.getMCType())>
      if (node.${attrGetter}().isPresent()) {
        node.${attrGetter}().get().accept(getRealThis());
      }
    <#else>
      if (null != node.${attrGetter}()) {
        node.${attrGetter}().accept(getRealThis());
      }
    </#if>
  <#elseif genHelper.isListAstNode(attr)>
    <#assign attrGetter = "get"+ attr.getName()?cap_first + "List">
    <#assign astChildTypeName = attr.printType()>
    {
      Iterator<${astChildTypeName}> iter_${attr.getName()} = node.${attrGetter}().iterator();
      while (iter_${attr.getName()}.hasNext()) {
        iter_${attr.getName()}.next().accept(getRealThis());
      }
    }
  </#if>
</#list>
