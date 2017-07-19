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


${tc.signature("astType", "astPackage", "cd")}

<#assign genVisitorHelper = glex.getGlobalVar("visitorHelper")>
<#assign genHelper = glex.getGlobalVar("typeResolverHelper")>
<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTypeResolverPackage()};

import de.monticore.ast.ASTNode;
import ${genVisitorHelper.getVisitorPackage()}.${genVisitorHelper.getVisitorType()};

/**
* Type system is implemented in two steps. First step is to resolve type for AST nodes
* and second step is to check type rules in CoCos by using resolved types.
* To implement type resolving for AST nodes this abstract class must be extended by a handwritten class.
* This abstract class extends de.monticore.types.TypeResolver<T>  and implements Visitor
    * All visit, endVisit, traverse methods of Visitor interface
    * is overwritten as final with empty body to prevent user from using those methods.
    * Only handle methods for each AST node should be overwritten in
    * the handwritten class to provide type resolving.
    */

    abstract class ${genHelper.getTypeResolver()}<T> extends de.monticore.types.TypeResolver<T> implements ${genVisitorHelper.getVisitorType()}{

        @Override
        public final void visit(ASTNode node) {}

        @Override
        public final void endVisit(ASTNode node) {}

    <#list cd.getTypes() as type>
        <#if type.isClass() || type.isInterface() >
            <#assign astName = genHelper.getJavaASTName(type)>
            @Override
            public final void visit(${astName} node) {}

            @Override
            public final void endVisit(${astName} node) {}

            @Override
            public void handle(${astName} node) {}
        </#if>
        <#if type.isClass() && !type.isAbstract()>

            @Override
            public final void traverse(${astName} node) {}

        </#if>
    </#list>
        }
