<#-- (c) https://github.com/MontiCore/monticore -->
// One might think that we could call traverse(subelement) immediately,
// but this is not true for interface-types where we do not know the
// concrete type of the element.
// Instead we double-dispatch the call, to call the correctly typed
// traverse(...) method with the elements concrete type.
${tc.signature("cdClass", "isScopeSpanning")}
<#assign genHelper = glex.getGlobalVar("astHelper")>

<#list cdClass.getCDAttributeList() as attr>
  <#assign attrName = genHelper.getNativeAttributeName(attr.getName())>
  <#if genHelper.isSimpleAstNode(attr) || genHelper.isOptionalAstNode(attr) >
    <#assign attrGetter = "get"+ attrName?cap_first>
    <#if genHelper.isOptional(attr.getMCType())>
      if (node.isPresent${attrName?cap_first}()) {
        node.${attrGetter}().accept(getRealThis());
      }
    <#else>
      if (null != node.${attrGetter}()) {
        node.${attrGetter}().accept(getRealThis());
      }
    </#if>
  <#elseif genHelper.isListAstNode(attr)>
    <#assign attrGetter = "get"+ attrName?remove_ending("s")?cap_first + "List">
    <#assign astChildTypeName = genHelper.getNativeTypeName(attr.getMCType())>
    {
      Iterator<${astChildTypeName}> iter_${attrName} = node.${attrGetter}().iterator();
      while (iter_${attrName}.hasNext()) {
        iter_${attrName}.next().accept(getRealThis());
      }
    }
  </#if>
</#list>

<#if isScopeSpanning>
    // traverse spanned scope if present
    if (node.getSpannedScope() != null) {
      node.getSpannedScope().accept(getRealThis());
    }
</#if>