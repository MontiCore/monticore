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
${tc.signature("astType", "astPackage", "allCds")}
<#assign genHelper = glex.getGlobalVar("visitorHelper")>

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getVisitorPackage()};

import java.util.Optional;

import de.monticore.ast.ASTNode;

/**
 * Delegator visitor for the <code>${genHelper.getCdName()}</code> language.<br/>
 * <br/>
 * Delegator visitors allow visitor composition. This comes in handy for reuse
 * of visitors implemented for a super language. Note that it is not allowed to
 * run visitors of a super-grammar on an AST of a sub language. Here, a
 * delegator visitor must wrap such a visitor and run it.<br/>
 * Also, note that visiting and endVisiting {@code ASTNode} is delegated to all
 * other registered delegates.<br/><br/>
 * realThis has to be of type ${genHelper.getDelegatorVisitorType()}. Note that
 * the delegator visitors of sublanguages do inherit from this interface and
 * hence can be used.<br/>
 * Implementing classes must implement the realThis-Pattern. Delegates are
 * always registered to {#getRealThis()}. When changing realThis, the
 * implementing class should put all registered delegates to the new realThis
 * (which also will change the realThis of the delegates themselves).
 * 
 * @see ${genHelper.getVisitorType()}
 * @see Common${genHelper.getDelegatorVisitorType()}
 */
public interface ${genHelper.getDelegatorVisitorType()} ${genHelper.getDelegatorVisitorSuperInterfaces()} {

  @Override
  public ${genHelper.getDelegatorVisitorType()} getRealThis();
  
  <#list allCds as cd>
    
    <#assign delegate = genHelper.getQualifiedVisitorNameAsJavaName(cd)>
    <#assign delegateType = genHelper.getQualifiedVisitorType(cd)>
    
    public void set_${delegate}(${delegateType} ${delegate});

    public Optional<${delegateType}> get_${delegate}();
    
    <#list cd.getTypes() as type>
      <#if type.isClass() || type.isInterface() >
        <#assign astName = genHelper.getJavaASTName(type)>
        @Override
        public default void handle(${astName} node) {
          if (getRealThis().get_${delegate}().isPresent()) {
            getRealThis().get_${delegate}().get().handle(node);
          }
        }
  
        <#if !type.isInterface() && !type.isAbstract()>
          @Override
          public default void traverse(${astName} node) {
            if (getRealThis().get_${delegate}().isPresent()) {
              getRealThis().get_${delegate}().get().traverse(node);
            }
          }
        </#if>
 
        @Override
        public default void visit(${astName} node) {
          if (getRealThis().get_${delegate}().isPresent()) {
            getRealThis().get_${delegate}().get().visit(node);
          }
        }

        @Override
        public default void endVisit(${astName} node) {
          if (getRealThis().get_${delegate}().isPresent()) {
            getRealThis().get_${delegate}().get().endVisit(node);
          }
        }
      </#if>
    </#list>
  </#list>

  <#-- all delegates are fed when ASTNode is visited (e.g., all inheritance
       visitors are interested in this -->

  @Override
  public default void visit(ASTNode node) {
    // delegate to all present delegates
    <#list allCds as cd>
      <#assign delegate = genHelper.getQualifiedVisitorNameAsJavaName(cd)>
      if (getRealThis().get_${delegate}().isPresent()) {
        getRealThis().get_${delegate}().get().visit(node);
      }
    </#list>
  }

  @Override
  public default void endVisit(ASTNode node) {
    // delegate to all present delegates 
    <#list allCds?reverse as cd>
      <#assign delegate = genHelper.getQualifiedVisitorNameAsJavaName(cd)>
      if (getRealThis().get_${delegate}().isPresent()) {
        getRealThis().get_${delegate}().get().endVisit(node);
      }
    </#list>
    }
}
