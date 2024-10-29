<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Constructor for a FullPrettyPrinter
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="grammarPackage" type="String" -->
<#-- @ftlvariable name="grammarName" type="String" -->
${tc.signature("grammarName", "grammarPackage")}


${glex.defineHookPoint(tc,"<Statement>*FPPConstructor:begin", ast)}
this.printer = printer;
this.traverser = ${grammarPackage}.${grammarName}Mill.traverser();

this.initializeTraverser(printComments);

${glex.defineHookPoint(tc,"<Statement>*FPPConstructor:end", ast)}
