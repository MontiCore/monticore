<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  Handle method for pretty Printing of overriden productions which inherit the right side
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="glex" type="de.monticore.generating.templateengine.GlobalExtensionManagement" -->
<#-- @ftlvariable name="astclassname" type="java.lang.String" -->

${tc.signature("astclassname")}
getTraverser().handle( (${astclassname}) node );
