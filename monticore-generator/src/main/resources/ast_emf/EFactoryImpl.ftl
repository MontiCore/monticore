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
<#assign genHelper = glex.getGlobalValue("astHelper")>
${tc.signature("ast", "grammarName", "packageURI")}
  
<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getAstPackage()};

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

public class ${ast.getName()} extends EFactoryImpl implements ${grammarName}Factory {

    // Creates the default factory implementation.
    public static ${grammarName}Factory init() {
        try {
            ${grammarName}Factory the${grammarName}Factory = (${grammarName}Factory)EPackage.Registry.INSTANCE.getEFactory("${packageURI}"); 
            if (the${grammarName}Factory != null) {
                return the${grammarName}Factory;
            }
        }
        catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new ${ast.getName()}();
    }
    
    // Creates an instance of the factory.
    private ${ast.getName()}() {
        super();
    }

    @Override
    public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
           <#-- ${op.includeTemplates(eFactoryImplReflectiveCreateMethod, ast.getFiles())} -->
            default:
                throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }
    
     public ${grammarName}Package get${grammarName}Package() {
        return (${grammarName}Package)getEPackage();
    }
    
    @Deprecated
    public static ${grammarName}Package getPackage() {
        return ${grammarName}Package.eINSTANCE;
    }
    
    <#--
    
    ${op.includeTemplates(eFactoryImplFile, ast.getFiles())}
    ${op.includeTemplates(eFactoryImplCreateConvertStringMethods, ast)}
    
   -->

}