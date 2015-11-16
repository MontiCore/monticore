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
  
<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

${tc.signature("ast", "grammarName", "packageURI", "astClasses")}

package ${genHelper.getAstPackage()};

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import de.monticore.emf._ast.ASTENodePackage;

public class ${ast.getName()} {
 
    private static final ${ast.getName()} controller = new ${ast.getName()}();
 
    // Private constructor for Singleton-Pattern
    private ${ast.getName()}() {
    }
 
    public static ${ast.getName()} getInstance() {
        return controller;
    }
    
    public void serializeAstToECoreModelFile() {
        serializeAstToECoreModelFile("");
    }
    
    public void serializeAstToECoreModelFile(String path) {
    
        // Create a resource set. 
        ResourceSet resourceSet = new ResourceSetImpl(); 
        // Register the default resource factory -- only needed for standalone 
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
          Resource.Factory.Registry.DEFAULT_EXTENSION, new XMIResourceFactoryImpl()); 
        
        // For Current Package 
        URI fileURI = URI.createFileURI(new File(path + "${grammarName}.ecore").getAbsolutePath());
        // Create a resource for this file. 
        Resource resource = resourceSet.createResource(fileURI);
        // Add instance of package to the contents.
        resource.getContents().add(${grammarName}Package.eINSTANCE);
        
        // For ASTNodePackage
        URI fileURIASTNode = URI.createFileURI(new File(path + "ASTENode.ecore").getAbsolutePath()); 
        Resource resourceASTNode = resourceSet.createResource(fileURIASTNode); 
        resourceASTNode.getContents().add(ASTENodePackage.eINSTANCE);
        
        // For SuperGrammars
<#-- TODO GV: super grammars eCoreFileSuperGrammarInit, ast.getSuperGrammars()) -->
           
        // Save the contents of the resources to the file system. 
        try { 
            resource.save(Collections.EMPTY_MAP);
            resourceASTNode.save(Collections.EMPTY_MAP);
<#-- TODO GV: super grammars eCoreFileSuperGrammarSave, ast.getSuperGrammars() -->
        } 
        catch (IOException e) {e.printStackTrace();}
    
    }
    
<#-- serializeInstance -->
    
<#-- deserializeInstance -->

}
