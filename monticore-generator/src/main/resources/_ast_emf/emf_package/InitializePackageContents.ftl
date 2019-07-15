<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("definition", "litealsEnum")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
<#assign service = glex.getGlobalVar("service")>
<#assign grammarName = definition.getName()>
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

org.eclipse.emf.ecore.EOperation op;
// Obtain other dependent packages
de.monticore.emf._ast.ASTENodePackage theASTENodePackage = (de.monticore.emf._ast.ASTENodePackage)org.eclipse.emf.ecore.EPackage.
    Registry.INSTANCE.getEPackage(de.monticore.emf._ast.ASTENodePackage.eNS_URI);
<#list service.getSuperCDs() as superCD>
    <#assign qualifiedName = service.getQualifiedPackageImplName(superCD)>
    <#assign identifierName = service.getSimplePackageImplName(superCD)>
    ${qualifiedName} ${identifierName?uncap_first} =
  (${qualifiedName})org.eclipse.emf.ecore.EPackage.Registry.INSTANCE.getEPackage(
    ${qualifiedName}.eNS_URI);
</#list>

<#--add super types if existend, if not standard ENode-->
<#list definition.getCDClassList() as astClass>
    <#assign interfaceName = astClass.getName()>
    <#if !astClass.getSymbol().getSuperTypes()?has_content>
        ${interfaceName?uncap_first}.getESuperTypes().add(theASTENodePackage.getENode());
    <#else>
        <#list astClass.getSymbol().getSuperTypes() as superType>
            <#assign package = service.getClassPackage(superType)>
            ${interfaceName?uncap_first}.getESuperTypes().add(${package}.get${superType.getName()}());
        </#list>
    </#if>
</#list>

// Initialize classes and features; add operations and parameters

// Initialize enums and add enum literals
<#--initialisation for Literal Constants-->
initEEnum(constants${grammarName}, ${grammarName}Literals.class, "${grammarName}Literals");
<#list litealsEnum.getCDEnumConstantList() as enumConstant>
    addEEnumLiteral(constants${grammarName}, ${grammarName}Literals.${enumConstant.getName()});
</#list>

<#--initialisation for all classes-->
<#list definition.getCDClassList() as cdClass>
    <#assign className = cdClass.getName()>
    <#assign abstract = service.determineAbstractString(cdClass)>
    initEClass(${className?uncap_first}, ${className}.class, "${className}", ${abstract}, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
</#list>

<#list definition.getCDInterfaceList() as cdInterface>
 <#if cdInterface.getName() != "AST"+ definition.getName() + "Node">
    <#assign interfaceName = cdInterface.getName()>
    initEClass(${interfaceName?uncap_first}, ${interfaceName}.class, "${interfaceName}", IS_ABSTRACT, IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
 </#if>
</#list>

<#list definition.getCDClassList() as cdClass>
    <#list cdClass.getCDAttributeList() as attribute>
        <#assign get = service.determineGetEmfMethod(attribute, definition)>
        <#assign isList = service.determineListInteger(attribute.getType())>
        <#if genHelper.isSimpleAstNode(attribute) || genHelper.isListAstNode(attribute) ||genHelper.isOptionalAstNode(attribute)>
            initEReference(get${cdClass.getName()}_${attribute.getName()?cap_first}(), ${get}(), null, "${attribute.getName()?cap_first}", null,
            0, ${isList}, ${cdClass.getName()}.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, <#if isList == "1">!</#if>IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        <#else>
            initEAttribute(get${cdClass.getName()}_${attribute.getName()?cap_first}(), ${get}(), "${attribute.getName()?cap_first}", null,
            0, ${isList}, ${cdClass.getName()}.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, <#if isList == "1">!</#if>IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        </#if>
    </#list>
</#list>

<#list service.getEDataTypes(definition) as dataType>
  initEDataType(${service.getSimpleNativeAttributeType(dataType)?uncap_first}, ${dataType}.class, "${service.getSimpleNativeAttributeType(dataType)}", IS_SERIALIZABLE, !IS_GENERATED_INSTANCE_CLASS);
</#list>

// Create resource
createResource(eNS_URI);

