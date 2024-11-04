<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Handle method for pretty printing, where automatic printer generation fails
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="errorMessage" type="java.lang.String" -->
<#-- @ftlvariable name="astName" type="java.lang.String" -->
<#-- @ftlvariable name="node" type="de.monticore.ast.ASTNode" -->
<#-- @ftlvariable name="blockData" type="java.lang.String" -->

${tc.signature("errorMessage", "astName", "node", "blockData")}
<#assign service = glex.getGlobalVar("service")>

// TODO: Implement me
/*
 * ${blockData?replace("*/","* - /", "g")}
 *
 * ${grammarPrinter.prettyprint(node)?replace("*/","* - /", "g")}
*/
Log.error("0xA1067${service.getGeneratedErrorCode(astName + blockData)} ${errorMessage}");
throw new IllegalStateException("${errorMessage}");
