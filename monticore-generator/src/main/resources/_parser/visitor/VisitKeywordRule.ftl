<#-- (c) https://github.com/MontiCore/monticore -->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="prodname" type="java.lang.String" -->
${tc.signature("prodname")}
return convert${prodname?cap_first}(ctx.mc__internal__token);
