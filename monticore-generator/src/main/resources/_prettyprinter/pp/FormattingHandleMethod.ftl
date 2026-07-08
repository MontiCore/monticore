<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="blockData" type="de.monticore.codegen.prettyprint.data.FormattingBlockData" -->
<#-- @ftlvariable name="astName" type="java.lang.String" -->
<#-- @ftlvariable name="grammarName" type="java.lang.String" -->
<#-- @ftlvariable name="astPackage" type="java.lang.String" -->
<#-- @ftlvariable name="iterators" type="java.util.Set<java.util.Map.Entry<String, de.monticore.codegen.prettyprint.PrettyPrinterGenerationVisitor.IteratorData>>" -->
${tc.signature("blockData", "astName", "grammarName", "astPackage", "iterators")}

if (this.isPrintComments()) {
    de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
}

getPrinter().startProduction("${astName}");

<#list iterators as it>
    ${it.getValue().getType()} iter_${it.getKey()?uncap_first} = node.${it.getValue().getGetter()}().iterator();
</#list>

<#if blockData.getAltDataList()?size == 1 && blockData.getAltDataList()?first.isAlwaysTrue()>
  <#comment>If only one alt is present without condition, skip the condition</#comment>
  ${includeArgs("_prettyprinter.pp.FormattingAlt", blockData.getAltDataList()?first, grammarName, astPackage)}
<#else>
  ${includeArgs("_prettyprinter.pp.FormattingBlock", blockData, grammarName, astPackage)}
</#if>

getPrinter().endProduction();

if (this.isPrintComments()) {
    de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
}