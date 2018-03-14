<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("method", "ast", "attribute", "cDAndJavaConformName", "isBuilderClass", "isInherited", "methodname")}
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
  </#if>
