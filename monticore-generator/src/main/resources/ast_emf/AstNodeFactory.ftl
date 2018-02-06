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
${tc.signature("ast", "astImports", "astClasses")}
<#assign genHelper = glex.getGlobalVar("astHelper")>
  
<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getAstPackage()};

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import de.se_rwth.commons.logging.Log;

<#-- handle ast imports from the super grammars -->
<#list astImports as astImport>
import ${astImport};
</#list>


public class ${ast.getName()} extends EFactoryImpl {
  
  // Creates the default factory implementation.
  public static ${ast.getName()} getFactory() {
    try {
      ${ast.getName()} eFactory = (${ast.getName()})EPackage.Registry.INSTANCE.getEFactory("${genHelper.getPackageURI()}"); 
      if (eFactory != null) {
        return eFactory;
     }
    }
    catch (Exception exception) {
      EcorePlugin.INSTANCE.log(exception);
    }
    if (factory == null) {
      factory = new ${ast.getName()}();
    }
    return factory;
  }

  
  protected static ${ast.getName()} factory = null;
  
  @Override
  public EObject create(EClass eClass) {
    switch (eClass.getClassifierID()) {
    <#list astClasses as astClass>
      case ${genHelper.getCdName()}Package.${astClass}: return ${ast.getName()}.create${astClass}();
    </#list>
    <#-- eFactoryImplReflectiveCreateMethod -->
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }
  
  // Returns the package supported by this factory.
  ${genHelper.getCdName()}Package get${genHelper.getCdName()}Package() {
    return (${genHelper.getCdName()}Package)getEPackage();
  }
    
<#list ast.getCDAttributeList() as attribute>
  ${tc.includeArgs("ast.Attribute", [attribute, ast])}
</#list>

  protected ${ast.getName()} () {}

  <#-- generate all methods -->
<#list ast.getCDMethodList() as method>
  ${tc.includeArgs("ast.ClassMethod", [method, ast])}
</#list>

}
