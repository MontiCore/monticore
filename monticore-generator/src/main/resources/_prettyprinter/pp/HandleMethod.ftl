<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Handle method for pretty Printing
-->
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
