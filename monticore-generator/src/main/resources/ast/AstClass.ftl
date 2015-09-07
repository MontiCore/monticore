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
<#--
  Generates a Java class
  
  @params    ASTCDClass $ast
             ASTCDClass $astBuilder
  @result    mc.javadsl.JavaDSL.CompilationUnit
  
-->
${tc.signature("ast", "astBuilder")}
<#assign genHelper = glex.getGlobalValue("astHelper")>
  
<#-- set package -->
package ${genHelper.getAstPackage()};

<#-- handle imports from model -->
${tc.include("ast.AstImports")}

${ast.printModifier()} class ${ast.getName()} extends ${tc.include("ast.AstSuperTypes")} {
  <#-- generate all attributes -->  
  <#list ast.getCDAttributes() as attribute>
    <#if !genHelper.isInherited(attribute)>
  ${tc.include("ast.Attribute", attribute)}
    </#if>
  </#list>
  <#-- generate all constructors -->  
  <#list ast.getCDConstructors() as constr>
    ${tc.includeArgs("ast.Constructor", [constr, ast])}
  </#list>
  
  <#-- generate all methods -->
  <#list ast.getCDMethods() as method>
    ${tc.includeArgs("ast.ClassMethod", [method, ast])}
  </#list>
   
  ${tc.include("ast.ClassContent")}
  <#if astBuilder.isPresent()>
    ${tc.includeArgs("ast.AstBuilder", [astBuilder.get(), ast])}
  </#if>
  
}
