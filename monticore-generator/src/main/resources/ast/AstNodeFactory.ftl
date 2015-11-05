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
${tc.signature("ast", "astImports", "astClasses")}
<#assign genHelper = glex.getGlobalValue("astHelper")>
  
<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getAstPackage()};

import java.util.ArrayList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import de.se_rwth.commons.logging.Log;

<#-- handle ast imports from the super grammars -->
<#list astImports as astImport>
import ${astImport};
</#list>


public class ${ast.getName()} extends EFactoryImpl implements ${genHelper.getCdName()}Factory {
  
  static ${ast.getName()} getFactory() {
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
      case ${genHelper.getCdName()}Package.${astClass}: return ${genHelper.getCdName()}NodeFactory.create${astClass}();
    </#list>
    <#-- eFactoryImplReflectiveCreateMethod -->
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

<#list ast.getCDAttributes() as attribute>
  ${tc.include("ast.Attribute", attribute)}
</#list>

  protected ${ast.getName()} () {}

  <#-- generate all methods -->
<#list ast.getCDMethods() as method>
  ${tc.includeArgs("ast.ClassMethod", [method, ast])}
</#list>

}
