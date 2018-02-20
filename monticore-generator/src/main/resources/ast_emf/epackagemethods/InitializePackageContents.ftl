<#-- (c) https://github.com/MontiCore/monticore -->
  ${tc.signature("method", "clazz", "grammarName", "superGrammars", "astClasses", "emfAttributes", "externalTypes", "literals")}
  <#assign genHelper = glex.getGlobalVar("astHelper")>
/**
 * Complete the initialization of the package and its meta-model.  This
 * method is guarded to have no affect on any invocation but its first.
*/
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
     <#list superGrammars as superGrammar>
       <#assign qualifiedName = genHelper.getEPackageName(superGrammar)>
       <#assign identifierName = astHelper.getIdentifierName(superGrammar)>
    ${qualifiedName} the${identifierName?lower_case?cap_first + "Package"} = 
      (${qualifiedName})EPackage.Registry.INSTANCE.getEPackage(
      ${qualifiedName}.eNS_URI); 
     </#list>
  
  <#list astClasses as astClass>
    <#assign className = astHelper.getPlainName(astClass)>
    <#if !astClass.getSymbol().get().getSuperTypes()?has_content>
    ${className[3..]?uncap_first}EClass.getESuperTypes().add(theASTENodePackage.getENode()); 
    <#else>
      <#list astClass.getSymbol().get().getSuperTypes() as superType>
        <#assign sGrammarName = nameHelper.getSimpleName(superType.getModelName())>
        <#if superType.getModelName()?lower_case==astHelper.getQualifiedCdName()?lower_case>
          <#assign package = "this.get">
        <#else>
          <#assign identifierName = astHelper.getIdentifierName(superType.getModelName())>
          <#assign package = "the" + identifierName?lower_case?cap_first + "Package.get">
        </#if>
    ${className[3..]?uncap_first}EClass.getESuperTypes().add(${package}${superType.getName()[3..]}()); 
      </#list>
    </#if>
  </#list>  
  
    // Initialize classes and features; add operations and parameters
  
    // Initialize enums and add enum literals
    initEEnum(constants${grammarName}EEnum, ${grammarName}Literals.class, "${grammarName}Literals");
  <#list literals as literal>   
    addEEnumLiteral(constants${grammarName}EEnum, ${grammarName}Literals.${literal});
  </#list>
    
  <#list astClasses as astClass>
    <#assign className = astHelper.getPlainName(astClass)>
    <#if astClass.getSymbol().get().isInterface()>
      <#assign abstract = "IS_ABSTRACT">
      <#assign interface = "IS_INTERFACE">
    <#else>
      <#assign interface = "!IS_INTERFACE">
      <#if astClass.isPresentModifier() && astClass.getModifier().isAbstract()>
        <#assign abstract = "IS_ABSTRACT">
      <#else>
        <#assign abstract = "!IS_ABSTRACT">
      </#if> 
    </#if>  
    initEClass(${className[3..]?uncap_first}EClass, ${className}.class, "${className}", ${abstract}, ${interface}, IS_GENERATED_INSTANCE_CLASS);
  </#list>  
  <#list emfAttributes as emfAttribute>
    <#if emfAttribute.isExternal()>
      <#assign get = "theASTENodePackage.getENode">
    <#elseif emfAttribute.isInherited()>
      <#assign sGrammarName = astHelper.getIdentifierName(emfAttribute.getDefinedGrammar())>
      <#assign get = "the" + sGrammarName?cap_first + "Package.get" + emfAttribute.getEDataType()[3..]>
    <#else>
      <#assign get = "this.get" + emfAttribute.getEDataType()[3..]>
    </#if>
    <#if emfAttribute.isAstNode()>
      <#assign isList = "1">
    <#elseif emfAttribute.isAstList()>
      <#assign isList = "-1">
    <#elseif astHelper.istJavaList(emfAttribute.getCdAttribute())>
      <#assign isList = "-1">
    <#else>
      <#assign isList = "1">
    </#if>    
    <#if emfAttribute.isAstNode() || emfAttribute.isAstList()>
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
    
