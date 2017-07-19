<#--
***************************************************************************************
Copyright (c) 2017, MontiCore
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
<#assign genHelper = glex.getGlobalVar("astHelper")>
  
<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

${tc.signature("ast", "grammarName", "packageURI", "astTypes")}

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EDataType;
import de.monticore.emf._ast.ASTEPackage;

public interface ${ast.getName()} extends ASTEPackage {
  // The package name.
  String eNAME = "${grammarName}";
  // The package namespace URI.
  String eNS_URI = "${packageURI}";
  // The package namespace name.
  String eNS_PREFIX = "${grammarName}";
  // The singleton instance of the package.
  ${ast.getName()} eINSTANCE = ${grammarName}PackageImpl.init();
  
  int Constants${grammarName} = 0;
    
  <#list astTypes as astClass>
  int ${astClass} = ${astClass?counter};
  </#list>
  
   <#-- generate all attributes -->  
  <#list ast.getCDAttributes() as attribute>
    <#if !genHelper.isInherited(attribute)>
    ${tc.include("ast.Constant", attribute)}
    </#if>
  </#list>
    
    // Returns the factory that creates the instances of the model.
  ${grammarName}NodeFactory get${grammarName}Factory();
  
  EEnum getConstants${grammarName}();
    
  <#list astTypes as astClass>
  EClass get${astClass[3..]}();
  </#list>
    
  <#-- generate all methods -->  
  <#list ast.getCDMethods() as method>
  ${tc.includeArgs("ast.ClassMethod", [method, ast])}
  </#list>
     
  /**
   * <!-- begin-user-doc -->
   * Defines literals for the meta objects that represent
   * <ul>
   *   <li>each class,</li>
   *   <li>each feature of each class,</li>
   *   <li>each enum,</li>
   *   <li>and each data type</li>
   * </ul>
   * <!-- end-user-doc -->
  */
  interface Literals {
  
    EEnum Constants${grammarName} = eINSTANCE.getConstants${grammarName}();
    
  <#list astTypes as astClass>
    EClass ${astClass} = eINSTANCE.get${astClass[3..]}();
    <#--  ${ePackageLiteralMain} --> 
  </#list>
  }
}
