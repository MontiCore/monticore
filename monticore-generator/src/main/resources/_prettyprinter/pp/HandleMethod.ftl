<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Handle method for pretty Printing
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="blockData" type="de.monticore.codegen.prettyprint.data.BlockData" -->
<#-- @ftlvariable name="astName" type="String" -->
<#-- @ftlvariable name="grammarName" type="String" -->
<#-- @ftlvariable name="astPackage" type="String" -->
<#-- @ftlvariable name="iterators" type="java.util.Set<java.util.Map.Entry<String, de.monticore.codegen.prettyprint.PrettyPrinterGenerationVisitor.IteratorData>>" -->
${tc.signature("blockData", "astName", "grammarName", "astPackage", "iterators")}
if (this.isPrintComments()) {
    de.monticore.prettyprint.CommentPrettyPrinter.printPreComments(node, getPrinter());
}
<#list iterators as it>
    ${it.getValue().getType()} iter_${it.getKey()?uncap_first} = node.${it.getValue().getGetter()}().iterator();
</#list>

<#if blockData.getAltDataList()?size == 1 && !blockData.getAltDataList()?first.getExpressionList()?has_content> <#comment>If only one alt is present without condition, skip the condition</#comment>
    ${includeArgs("Alt", ast, blockData.getAltDataList()?first, grammarName, astPackage)}
<#else>
    ${includeArgs("Block", ast, blockData, grammarName, astPackage)}

</#if>
if (this.isPrintComments()) {
    de.monticore.prettyprint.CommentPrettyPrinter.printPostComments(node, getPrinter());
}
