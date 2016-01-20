<#--
***************************************************************************************
Copyright (c) 2015, MontiCore
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its contributors
may be used to endorse or promote products derived from this software
without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY,
OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE.
***************************************************************************************
-->
  ${tc.signature("grammarName", "superGrammars", "astClasses", "emfAttributes")}
  <#assign genHelper = glex.getGlobalValue("astHelper")>
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
       <#assign packageName = superGrammar?lower_case + "._ast.">
       <#assign simpleName = nameHelper.getSimpleName(superGrammar)>
       <#assign qualifiedName = packageName + simpleName?cap_first + "Package">
    ${qualifiedName} the${simpleName?lower_case?cap_first + "Package"} = 
      (${qualifiedName})EPackage.Registry.INSTANCE.getEPackage(
      ${qualifiedName}.eNS_URI); 
     </#list>
  
    // Add supertypes to classes <#-- TODO GV: check super type EPackageImplInitializeSuperTypes.ftl-->
  <#list astClasses as astClass>
    <#assign className = astHelper.getPlainName(astClass)>
    <#if !astClass.getSymbol().get().getSuperTypes()?has_content>
    ${className[3..]?uncap_first}EClass.getESuperTypes().add(theASTENodePackage.getENode()); 
    <#else>
      <#list astClass.getSymbol().get().getSuperTypes() as superType>
        <#assign sGrammarName = nameHelper.getSimpleName(superType.getModelName())>
        <#if sGrammarName==grammarName>
          <#assign package = "this.get">
        <#else>
          <#assign package = "the" + sGrammarName?lower_case?cap_first + "Package.get">
        </#if>
    ${className[3..]?uncap_first}EClass.getESuperTypes().add(${package}${superType.getName()[3..]}()); 
      </#list>
    </#if>
  </#list>  
  
    // Initialize classes and features; add operations and parameters
  
    // Initialize enums and add enum literals
    <#-- TODO GV -->
    
  <#list astClasses as astClass>
    <#assign className = astHelper.getPlainName(astClass)>
    <#if astClass.getSymbol().get().isInterface()>
      <#assign abstract = "IS_ABSTRACT">
      <#assign interface = "IS_INTERFACE">
    <#else>
      <#assign interface = "!IS_INTERFACE">
      <#if astClass.getModifier().isPresent() && astClass.getModifier().get().isAbstract()>
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
      <#assign sGrammarName = nameHelper.getSimpleName(emfAttribute.getDefinedGrammarName())>
      <#assign get = "the" + sGrammarName?cap_first + "Package.get" + emfAttribute.getEDataType()[3..]>
    <#else>
      <#assign get = "this.get" + emfAttribute.getEDataType()[3..]>
    </#if>
    <#if emfAttribute.isAstNode()>
      <#assign isList = "-1">
    <#elseif emfAttribute.isAstList()>
      <#assign isList = "1">
    init${emfAttribute.getEmfType()}(get${emfAttribute.getFullName()}(), ${get}(), null, "${emfAttribute.getAttributeName()?cap_first}", null,
      0, ${isList}, ${emfAttribute.getCdType().getName()}.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    <#else>
      <#if astHelper.istAstENodeList(emfAttribute.getCdAttribute())>
        <#assign isList = "-1">
      <#else>
        <#assign isList = "1">
      </#if>  
      <#if emfAttribute.isExternal1() || emfAttribute.isEnum()>
        <#assign get = "this.get" + emfAttribute.getEDataType()?cap_first>
      <#else>
        <#assign get = "ecorePackage.getE" + emfAttribute.getEDataType()?cap_first>
    </#if> 
    init${emfAttribute.getEmfType()}(get${emfAttribute.getFullName()}(), ${get}(), "${emfAttribute.getAttributeName()?cap_first}", null, 
      0, ${isList}, ${emfAttribute.getCdType().getName()}.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    </#if>
  
  </#list>
  
    <#-- TODO GV:   ePackageImplInitiliazeMethod, ast.getMethods() -->
  
    // Create resource
    createResource(eNS_URI);
    
