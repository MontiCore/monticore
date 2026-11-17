<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  resolveConstantInSuperLanguages of a ReteFeatures class: asks the direct
  super languages one after another (these delegate to their super languages)
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="superFeatures" type="java.util.List<String>" -->
<#-- full names of the ReteFeatures classes of the direct super languages -->
${tc.signature("superFeatures")}
return java.util.Optional.<String>empty()<#list superFeatures as features>
    .or(() -> ${features}.resolveConstant(astType, attributeName))</#list>;
