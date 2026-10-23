<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  constructor of a FullReteFactEmitter
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="millFullName" type="java.lang.String" -->
${tc.signature("millFullName")}
  super(network, ${millFullName}.inheritanceTraverser());
  initializeTraverser();
