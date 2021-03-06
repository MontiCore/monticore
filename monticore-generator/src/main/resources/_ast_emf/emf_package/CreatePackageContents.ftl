<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarName", "definition")}
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

<#list definition.getCDClassesList() as astClass>
    ${astClass.getName()?uncap_first} = createEClass(${astClass.getName()});
</#list>

<#list definition.getCDInterfacesList() as astInterface>
    ${astInterface.getName()?uncap_first} = createEClass(${astInterface.getName()});
</#list>

<#list definition.getCDClassesList()  as astClass>
    <#list astClass.getCDAttributeList() as attribute>
        <#if genHelper.isAstNode(attribute) || genHelper.isOptionalAstNode(attribute)
            || genHelper.isListAstNode(attribute)>
          createEReference(${astClass.getName()?uncap_first}, ${astClass.getName()}_${attribute.getName()?cap_first});
          <#else >
          createEAttribute(${astClass.getName()?uncap_first}, ${astClass.getName()}_${attribute.getName()?cap_first});
        </#if>
    </#list>
</#list>

<#list definition.getCDInterfacesList()  as astInterface>
    <#list astInterface.getCDAttributeList() as attribute>
      <#if genHelper.isAstNode(attribute) || genHelper.isOptionalAstNode(attribute)
      || genHelper.isListAstNode(attribute)>
        createEReference(${astInterface.getName()?uncap_first}, ${astInterface.getName()}_${attribute.getName()?cap_first});
      <#else>
        createEAttribute(${astInterface.getName()?uncap_first}, ${astInterface.getName()}_${attribute.getName()?cap_first});
      </#if>
    </#list>
</#list>

<#list service.getEDataTypes(definition) as dataType>
    <#assign simpleType = genHelper.getSimpleNativeType(dataType)>
    ${simpleType?uncap_first} = createEDataType(${simpleType});
</#list>