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

import de.se_rwth.commons.logging.Log;

/**
 *  Delegator visitor for the <code>${genHelper.getCdName()}</code>
 * language.<br/>
 * <br/>
 */
public class ${genHelper.getDelegatorVisitorType()}  implements ${genHelper.getInheritanceVisitorType()} {

  private ${genHelper.getDelegatorVisitorType()} realThis = this;

  @Override
  public void setRealThis(${genHelper.getVisitorType()} realThis) {
    if (this.realThis != realThis) {
      if (!(realThis instanceof ${genHelper.getDelegatorVisitorType()})) {
          Log.error("0xA7111${genHelper.getGeneratedErrorCode(ast)} realThis of ${genHelper.getDelegatorVisitorType()} must be ${genHelper.getDelegatorVisitorType()} itself.");
      }
      this.realThis = (${genHelper.getDelegatorVisitorType()}) realThis;
      // register the known delegates to the realThis (and therby also set their new realThis)
      <#list allCds as cd>
        <#assign delegate = genHelper.getVisitorType(cd, cd?index, allCds)>
        <#assign delegateName = genHelper.getVisitorName(delegate)>
        if (this.${delegateName}.isPresent()) {
          this.set${delegate}(${delegateName}.get());
        }
      </#list>
    }
  }

  public ${genHelper.getDelegatorVisitorType()} getRealThis() {
    return realThis;
  }

  <#list allCds as cd>
    
    <#assign delegate = genHelper.getVisitorType(cd, cd?index, allCds)>
    <#assign delegateName = genHelper.getVisitorName(delegate)>
    <#assign delegateType = genHelper.getQualifiedVisitorType(cd)>
    
    private Optional<${delegateType}> ${delegateName} = Optional.empty();
    
    public void set${delegate}(${delegateType} ${delegate}) {
      this.${delegateName} = Optional.ofNullable(${delegate});
      if (this.${delegateName}.isPresent()) {
        this.${delegateName}.get().setRealThis(getRealThis());
      }
      // register the ${delegateType} also to realThis if not this
      if (getRealThis() != this) {
        // to prevent recursion we must differentiate between realThis being
        // the current this or another instance.
        getRealThis().set${delegate}(${delegate});
      }
    }

    public Optional<${delegateType}> get${delegate}() {
      return ${delegateName};
    }

    <#list cd.getTypes() as type>
      <#if type.isClass() || type.isInterface() >
        <#assign astName = genHelper.getJavaASTName(type)>
        @Override
        public void handle(${astName} node) {
          if (getRealThis().get${delegate}().isPresent()) {
            getRealThis().get${delegate}().get().handle(node);
          }
        }
  
        <#if !type.isInterface() && !type.isAbstract()>
          @Override
          public void traverse(${astName} node) {
            if (getRealThis().get${delegate}().isPresent()) {
              getRealThis().get${delegate}().get().traverse(node);
            }
          }
        </#if>
 
        @Override
        public void visit(${astName} node) {
          if (getRealThis().get${delegate}().isPresent()) {
            getRealThis().get${delegate}().get().visit(node);
          }
        }

        @Override
        public void endVisit(${astName} node) {
          if (getRealThis().get${delegate}().isPresent()) {
            getRealThis().get${delegate}().get().endVisit(node);
          }
        }
      </#if>
    </#list>
  </#list>
  
  <#-- all delegates are fed when ASTNode is visited (e.g., all inheritance
       visitors are interested in this -->

  public void visit(ASTNode node) {
    // delegate to all present delegates
    <#list allCds as cd>
      <#assign delegate = genHelper.getVisitorType(cd, cd?index, allCds)>
      <#assign delegateName = genHelper.getVisitorName(delegate)>
      if (getRealThis().get${delegate}().isPresent()) {
        getRealThis().get${delegate}().get().visit(node);
      }
    </#list>
  }

  public void endVisit(ASTNode node) {
    // delegate to all present delegates 
    <#list allCds?reverse as cd>
      <#assign delegate = genHelper.getVisitorType(cd, allCds?size - cd?index - 1, allCds)>
      <#assign delegateName = genHelper.getVisitorName(delegate)>
      if (getRealThis().get${delegate}().isPresent()) {
        getRealThis().get${delegate}().get().endVisit(node);
      }
    </#list>
    }
}
