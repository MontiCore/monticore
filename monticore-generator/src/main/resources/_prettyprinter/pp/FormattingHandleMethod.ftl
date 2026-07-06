<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="blockData" type="de.monticore.codegen.prettyprint.data.BlockData" -->
<#-- @ftlvariable name="astName" type="java.lang.String" -->
<#-- @ftlvariable name="grammarName" type="java.lang.String" -->
<#-- @ftlvariable name="astPackage" type="java.lang.String" -->
<#-- @ftlvariable name="iterators" type="java.util.Map" -->
<#-- @ftlvariable name="ruleName" type="java.lang.String" -->
<#-- @ftlvariable name="helper" type="de.monticore.codegen.prettyprint.FormattingPrettyPrinterGenerationVisitor.FormattingHelper" -->
${tc.signature("blockData", "astName", "grammarName", "astPackage", "iterators", "ruleName", "helper")}

${helper.reset()}
getPrinter().startProduction("${astName}");

<#assign emitPos = helper.next()>
getPrinter().emit("${astName?uncap_first}", "${ruleName}", "${emitPos}");

${includeArgs("FormattingBlock", ast, blockData, grammarName, astPackage, helper)}

getPrinter().endProduction();