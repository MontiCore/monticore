<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("cdClass", "classFullName")}
<#assign service = glex.getGlobalVar("service")>
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign isPresentModifier = cdClass.isPresentModifier()>

  String name = de.se_rwth.commons.StringTransformations.uncapitalize(reporting.getASTNodeNameFormatted(node));
  printObject(name, "${classFullName}");
  pp.indent();
<#if isPresentModifier && service.hasSymbolStereotype(cdClass.getModifier())>
  if (node.isPresentSymbol()) {
    String symName = de.se_rwth.commons.StringTransformations.uncapitalize(reporting.getSymbolNameFormatted(node.getSymbol()));
    pp.println("symbol = " + symName + ";");
  } else if (printEmptyOptional) {
    pp.println("symbol = absent;");
  }
</#if>
  String scopeName = de.se_rwth.commons.StringTransformations.uncapitalize(reporting.getScopeNameFormatted(node.getEnclosingScope()));
  pp.println("enclosingScope = " + scopeName + ";");
<#if isPresentModifier && service.hasScopeStereotype(cdClass.getModifier())>
  String spannedScopeName = de.se_rwth.commons.StringTransformations.uncapitalize(reporting.getScopeNameFormatted(node.getSpannedScope()));
  pp.println("spanningScope = " + spannedScopeName + ";");
</#if>
<#list cdClass.getCDAttributeList() as attr>
    <#assign attrGetter = genHelper.getPlainGetter(attr)>
    <#if genHelper.isOptionalAstNode(attr)>
  if (node.isPresent${genHelper.getNativeAttributeName(attr.getName())?cap_first}()) {
    pp.print("${attr.getName()}");
    pp.print(" = ");
    node.${attrGetter}().accept(getRealThis());
    pp.println(";");
  } else if (printEmptyOptional) {
    pp.println("${attr.getName()} = absent;");
  }
    <#elseif genHelper.isSimpleAstNode(attr)>
  if (null != node.${attrGetter}()) {
    pp.print("${attr.getName()}");
    pp.print(" = ");
    node.${attrGetter}().accept(getRealThis());
    pp.println(";");
  }
    <#elseif genHelper.isListAstNode(attr)>
        <#assign astChildTypeName = genHelper.getAstClassNameForASTLists(attr)>
  {
    Iterator<${astChildTypeName}> iter_${attr.getName()} = node.${attrGetter}().iterator();
    boolean isEmpty = true;
    if (iter_${attr.getName()}.hasNext()) {
      pp.print("${attr.getName()}");
      pp.print(" = [");
      pp.println("// *size: " + node.${attrGetter}().size());
      pp.indent();
      isEmpty = false;
    } else if (printEmptyList) {
      pp.print("${attr.getName()}");
      pp.println(" = [];");
    }
    boolean isFirst = true;
    while (iter_${attr.getName()}.hasNext()) {
      if (!isFirst) {
        pp.println(",");
      }
      isFirst = false;
      iter_${attr.getName()}.next().accept(getRealThis());
    }
    if (!isEmpty) {
      pp.println("];");
      pp.unindent();
    }
  }
    <#elseif genHelper.isOptional(attr.getMCType())>
  if (node.isPresent${genHelper.getNativeAttributeName(attr.getName())?cap_first}()) {
    printAttribute("${attr.getName()}", "\"" + String.valueOf(node.${attrGetter}()) + "\"");
  } else if (printEmptyOptional) {
    pp.println("${attr.getName()} = absent;");
  }
    <#elseif genHelper.isListType(attr.getMCType())>
  {
    String sep = "";
      <#if genHelper.isListOfString(attr)>
    String str = "\"";
      <#else>
    String str = "";
      </#if>
    Iterator<?> it = node.${attrGetter}().iterator();
    boolean isEmpty = true;
    if (it.hasNext() || printEmptyList) {
      pp.print("${attr.getName()}" + " = [");
      isEmpty = false;
    }
    while (it.hasNext()) {
      pp.print(sep);
      pp.print(str + String.valueOf(it.next()) + str);
      sep = ", ";
    }
    if (!isEmpty) {
      pp.println("];");
    }
  }
  <#else>
    <#if genHelper.isString(attr.getMCType())>
  printAttribute("${attr.getName()}", "\"" + String.valueOf(node.${attrGetter}()) + "\"");
    <#else>
  printAttribute("${attr.getName()}", String.valueOf(node.${attrGetter}()));
    </#if>
  </#if>
</#list>
  pp.unindent();
  pp.print("}");