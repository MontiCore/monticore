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
  ${tc.signature("grammarName", "astClasses", "emfAttributes")}
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
     
    // Add supertypes to classes <#-- TODO GV: check super type EPackageImplInitializeSuperTypes.ftl-->
  <#list astClasses as astClass>
    ${astClass.getName()[3..]?uncap_first}EClass.getESuperTypes().add(theASTENodePackage.getENode());
  </#list>  
  
    // Initialize classes and features; add operations and parameters
  
    // Initialize enums and add enum literals
    <#-- TODO GV -->
    
  <#list astClasses as astClass>
    <#assign className = astClass.getName()>
    <#-- TODO GV -->
    // TODO: ${astClass.getName()}.class vs. ${astClass.getName()[3..]}.class?
    <#if astClass.getModifier().isPresent() && astClass.getModifier().get().isAbstract()>
      <#assign abstract = "IS_ABSTRACT">
    <#else>
      <#assign abstract = "!IS_ABSTRACT">
    </#if>   
    initEClass(${className[3..]?uncap_first}EClass, ${className}.class, "${className}", ${abstract}, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
  </#list>  
  
  <#list emfAttributes as emfAttribute>
    <#if emfAttribute.isAstNode()>
      <#if emfAttribute.isAstList()>
    init${emfAttribute.getEmfType()}(get${emfAttribute.getFullName()}(), this.get${emfAttribute.getEDataType()[3..]}(), null, "${emfAttribute.getAttributeName()?cap_first}", null,
      0, -1, ${emfAttribute.getCdType().getName()}.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      <#else>
    init${emfAttribute.getEmfType()}(get${emfAttribute.getFullName()}(), this.get${emfAttribute.getEDataType()[3..]}(), null, "${emfAttribute.getAttributeName()?cap_first}", null,
      0, 1, ${emfAttribute.getCdType().getName()}.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
      </#if>   
    <#else>
      <#if astHelper.istAstENodeList(emfAttribute.getCdAttribute())>
        <#assign isList = "-1">
      <#else>
        <#assign isList = "1">
      </#if>   
    init${emfAttribute.getEmfType()}(get${emfAttribute.getFullName()}(), ecorePackage.getE${emfAttribute.getEDataType()?cap_first}(), "${emfAttribute.getAttributeName()?cap_first}", null, 
      0, ${isList}, ${emfAttribute.getCdType().getName()}.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    </#if>
  
  </#list>
  
    <#-- TODO GV:   ePackageImplInitiliazeMethod, ast.getMethods() -->
  
    // Create resource
    createResource(eNS_URI);
    
