<#-- (c) https://github.com/MontiCore/monticore -->
<#--
  resolveConstant of a ReteFeatures class: the qualified name of the feature
  constant of an attribute of an AST type. Attributes whose feature is declared
  by a type of this language are resolved directly, the others via the super
  languages. Types of this language list all their visible attributes, so an
  unknown attribute has no feature; types of other languages may be known by
  the super languages.
-->
<#-- @ftlvariable name="tc" type="de.monticore.generating.templateengine.TemplateController" -->
<#-- @ftlvariable name="declaringTypes" type="java.util.Map<String, java.util.Map<String, String>>" -->
<#-- @ftlvariable name="constants" type="java.util.Map<String, java.util.Map<String, String>>" -->
<#-- declaringTypes: AST type -> attribute name -> AST type declaring the feature -->
<#-- constants: AST type of this language -> attribute name -> feature constant -->
${tc.signature("declaringTypes", "constants")}
return switch (astType) {
<#list declaringTypes as astType, attributes>
  case "${astType}" -> switch (attributeName) {
  <#list attributes as attributeName, declaringType>
    <#if constants[declaringType]??>
    case "${attributeName}" -> java.util.Optional.of("${constants[declaringType][attributeName]}");
    <#else>
    case "${attributeName}" -> resolveConstantInSuperLanguages("${declaringType}", "${attributeName}");
    </#if>
  </#list>
    default -> java.util.Optional.empty();
  };
</#list>
  default -> resolveConstantInSuperLanguages(astType, attributeName);
};
