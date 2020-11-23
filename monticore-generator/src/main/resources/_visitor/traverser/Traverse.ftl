<#-- (c) https://github.com/MontiCore/monticore -->
// One might think that we could call traverse(subelement) immediately,
// but this is not true for interface-types where we do not know the
// concrete type of the element.
// Instead we double-dispatch the call, to call the correctly typed
// traverse(...) method with the elements concrete type.
${tc.signature("cdClass", "isScopeSpanning", "handler", "topCast")}
<#assign genHelper = glex.getGlobalVar("astHelper")>

if (get${handler}().isPresent()) {
  get${handler}().get().traverse(node);
} else {
<#list cdClass.getCDAttributeList() as attr>
  <#assign attrName = genHelper.getNativeAttributeName(attr.getName())>
  <#if genHelper.isSimpleAstNode(attr) || genHelper.isOptionalAstNode(attr) >
    <#assign attrGetter = "get"+ attrName?cap_first>
    <#if genHelper.isOptional(attr.getMCType())>
      if (node.isPresent${attrName?cap_first}()) {
        node.${attrGetter}().accept(${topCast}this);
      }
    <#else>
      if (null != node.${attrGetter}()) {
        node.${attrGetter}().accept(${topCast}this);
      }
    </#if>
  <#elseif genHelper.isListAstNode(attr)>
    <#assign attrGetter = genHelper.getPlainGetter(attr)>
    <#assign astChildTypeName = genHelper.getNativeTypeName(attr.getMCType())>
    {
      Iterator<${astChildTypeName}> iter_${attrName} = node.${attrGetter}().iterator();
      while (iter_${attrName}.hasNext()) {
        iter_${attrName}.next().accept(${topCast}this);
      }
    }
  </#if>
</#list>

<#if isScopeSpanning>
    // although we generally assume that the symbol table is always available,
    // there are cases, where this is not true (for example construction of the
    // symbol table itself. Thus, the null-check is necessary.
    if (node.getSpannedScope() != null) {
      node.getSpannedScope().accept(${topCast}this);
    }
</#if>
}