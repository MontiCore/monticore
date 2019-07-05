<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("grammarName", "superGrammars", "astClasses", "emfAttributes", "externalTypes", "definition")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign service = glex.getGlobalVar("service")>

// Complete the initialization of the package and its meta-model.  This
// method is guarded to have no affect on any invocation but its first.

if (isInitialized) {
  return;
}
isInitialized = true;

// Initialize package
setName(eNAME);
setNsPrefix(eNS_PREFIX);
setNsURI(eNS_URI);

<#-- (ePackageImplInitializePackageContentsMain, ast) -->

EOperation op;
// Obtain other dependent packages
ASTENodePackage theASTENodePackage = (ASTENodePackage)EPackage.Registry.INSTANCE.getEPackage(ASTENodePackage.eNS_URI);
<#list service.getSuperCDs() as superCD>
    <#assign qualifiedName = service.getQualifiedPackageImplName(superCD)>
    <#assign identifierName = service.getSimplePackageImplName(superCD)>
    ${qualifiedName} ${identifierName?uncap_first} =
  (${qualifiedName})EPackage.Registry.INSTANCE.getEPackage(
    ${qualifiedName}.eNS_URI);
</#list>

<#list astClasses as astClass>
    <#assign className = astClass.getName()>
    <#if !astClass.getSymbol().getSuperTypes()?has_content>
        ${className?uncap_first}.getESuperTypes().add(theASTENodePackage.getENode());
    <#else>
        <#list astClass.getSymbol() as superClass>
            <#if superClass.getModelName()?lower_case==service.getQualifiedCDName()?lower_case>
                <#assign package = "this.get">
            <#else>
                <#assign identifierName = astHelper.getIdentifierName(superClass.getModelName())>
                <#assign package = identifierName?lower_case?cap_first + ".get">
            </#if>
            ${className?uncap_first}.getESuperTypes().add(${package}${superClass.getName()}());
        </#list>
    </#if>
</#list>

// Initialize classes and features; add operations and parameters

// Initialize enums and add enum literals
initEEnum(constants${grammarName}, ${grammarName}Literals.class, "${grammarName}Literals");
<#list definition.getCDEnumList() as literal>
  addEEnumLiteral(constants${grammarName}, ${grammarName}Literals.${literal.getName()}Literals);
</#list>

<#list definition.getCDInterfaceList() as cdInterface>
    <#assign interfaceName = cdInterface.getName()>
  initEClass(${className?uncap_first}, ${className}.class, "${className}", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
</#list>

<#list definition.getCDClassList() as astClass>
    <#assign className = astClass.getName()>
    <#if astClass.isPresentModifier() && astClass.getModifier().isAbstract()>
        <#assign abstract = "IS_ABSTRACT">
    <#else>
        <#assign abstract = "!IS_ABSTRACT">
    </#if>
    initEClass(${className?uncap_first}, ${className}.class, "${className}", ${abstract}, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
</#list>

<#list definition.getCDEnumList() as cdEnum>
    <#assign className = cdEnum.getName()>
    initEClass(${className?uncap_first}, ${className}.class, "${className}", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
</#list>

<#list emfAttributes as emfAttribute>
    <#if emfAttribute.isExternal()>
        <#assign get = "theASTENodePackage.getENode">
    <#elseif emfAttribute.isInherited()>
        <#assign sGrammarName = astHelper.getIdentifierName(emfAttribute.getDefinedGrammar())>
        <#if emfAttribute.hasExternalType()>
        <#-- Delete 4 characters: "AST" +"E" -->
            <#assign get = "the" + sGrammarName?cap_first + "Package.get" + emfAttribute.getEDataType()[4..]>
        <#else>
            <#assign get = "the" + sGrammarName?cap_first + "Package.get" + emfAttribute.getEDataType()[3..]>
        </#if>
    <#else>
        <#assign get = "this.get" + emfAttribute.getEDataType()[3..]>
    </#if>

    <#if genHelper.isListType(emfAttribute)>
        <#assign isList = "-1">
    <#else>
        <#assign isList = "1">
    </#if>
    <#if genHelper.isAstNode(emfAttribute) || genHelper.isListAstNode(emfAttribute)>
      init${emfAttribute.getEmfType()}(get${emfAttribute.getFullName()}(), ${get}(), null, "${emfAttribute.getAttributeName()?cap_first}", null,
      0, ${isList}, ${emfAttribute.getCdType().getName()}.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, <#if isList == "1">!</#if>IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    <#else>
        <#if emfAttribute.isEnum()>
            <#if isList == "-1">
                <#assign get = "this.getE" + emfAttribute.getEDataType()?cap_first>
            <#else>
                <#assign get = "this.get" + emfAttribute.getEDataType()?cap_first>
            </#if>
        <#elseif emfAttribute.hasExternalType()>
            <#assign get = "this.get" + emfAttribute.getEDataType()?cap_first>
        <#else>
            <#assign get = "ecorePackage.getE" + emfAttribute.getEDataType()?cap_first>
        </#if>
      init${emfAttribute.getEmfType()}(get${emfAttribute.getFullName()}(), ${get}(), "${emfAttribute.getAttributeName()?cap_first}", null,
      0, ${isList}, ${emfAttribute.getCdType().getName()}.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, <#if isList == "1">!</#if>IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    </#if>
</#list>

<#list externalTypes?keys as externalType>
  initEDataType(${externalTypes[externalType]?uncap_first}EDataType, ${externalType}.class, "${externalTypes[externalType]}", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
</#list>

<#-- TODO GV:   ePackageImplInitiliazeMethod, ast.getMethods() -->

// Create resource
createResource(eNS_URI);

