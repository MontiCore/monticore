<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("attribute", "cDAndJavaConformName", "isBuilderClass", "isInherited", "methodname")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#if isInherited>
    super.${methodname}(${cDAndJavaConformName});
<#elseif genHelper.isOptional(attribute)>
    this.${cDAndJavaConformName} = Optional.ofNullable(${cDAndJavaConformName});
<#else>
    this.${cDAndJavaConformName} = ${cDAndJavaConformName};
</#if>
<#if isBuilderClass>
    return this;
<#elseif genHelper.isReferencedSymbolAttribute(attribute)>
<#if astHelper.isListType(attribute.printType())>
  ${cDAndJavaConformName}Map.clear();
<#else >
  ${cDAndJavaConformName}Symbol = Optional.empty();
</#if>
</#if>


