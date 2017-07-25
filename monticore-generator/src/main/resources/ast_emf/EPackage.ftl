<#--
****************************************************************************
MontiCore Language Workbench, www.monticore.de
Copyright (c) 2017, MontiCore, All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
this list of conditions and the following disclaimer in the documentation
and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
contributors may be used to endorse or promote products derived from this
software without specific prior written permission.

This software is provided by the copyright holders and contributors
"as is" and any express or implied warranties, including, but not limited
to, the implied warranties of merchantability and fitness for a particular
purpose are disclaimed. In no event shall the copyright holder or
contributors be liable for any direct, indirect, incidental, special,
exemplary, or consequential damages (including, but not limited to,
procurement of substitute goods or services; loss of use, data, or
profits; or business interruption) however caused and on any theory of
liability, whether in contract, strict liability, or tort (including
negligence or otherwise) arising in any way out of the use of this
software, even if advised of the possibility of such damage.
****************************************************************************
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
