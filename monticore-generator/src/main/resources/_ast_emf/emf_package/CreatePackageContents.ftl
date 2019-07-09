<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarName", "astClasses")}
<#assign service = glex.getGlobalVar("service")>
<#assign genHelper = glex.getGlobalVar("astHelper")>
// Creates the meta-model objects for the package.  This method is
// guarded to have no affect on any invocation but its first.

if (isCreated) {
  return;
}
isCreated = true;

// Create classes and their features
constants${grammarName} = createEEnum(Constants${grammarName});

<#list astClasses as astClass>
    ${astClass.getName()?uncap_first} = createEClass(${astClass.getName()});
</#list>

<#list astClasses as astClass>
    <#list astClass.getCDAttributeList() as attribute>
        <#if genHelper.isAstNode(attribute) || genHelper.isOptionalAstNode(attribute)
            || genHelper.isListAstNode(attribute)>
          createEReference(${astClass.getName()?uncap_first}, ${astClass.getName()}_${attribute.getName()?cap_first});
          <#else >
          createEAttribute(${astClass.getName()?uncap_first}, ${astClass.getName()}_${attribute.getName()?cap_first});
        </#if>
    </#list>
</#list>
<#--Todo understand what this is used for-->
<#--<#list externalTypes as externalType>-->
<#--    ${externalType?uncap_first}EDataType = createEDataType(${externalType});-->
<#--</#list>-->