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
${signature("className", "ruleSymbol", "isScopeSpanningSymbol")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign ruleName = ruleSymbol.getName()?cap_first>
<#assign referencedSymbol = ruleName+"Symbol">
<#assign package = genHelper.getTargetPackage()?lower_case>

<#-- Copyright -->
${tc.defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${package};

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.symboltable.references.CommonSymbolReference;
import de.monticore.symboltable.references.SymbolReference;
<#if isScopeSpanningSymbol>
import java.util.Collection;
</#if>

/**
 * Represents a reference of {@link ${referencedSymbol}}.
 */
public class ${className} extends ${referencedSymbol} implements SymbolReference<${referencedSymbol}> {
  protected final SymbolReference<${referencedSymbol}> reference;

  public ${className}(final String name, final Scope enclosingScopeOfReference) {
    super(name);
    reference = new CommonSymbolReference<>(name, ${referencedSymbol}.KIND, enclosingScopeOfReference);
  }

  /*
   * Methods of SymbolReference interface
   */

  @Override
  public ${referencedSymbol} getReferencedSymbol() {
    return reference.getReferencedSymbol();
  }

  @Override
  public boolean existsReferencedSymbol() {
    return reference.existsReferencedSymbol();
  }

  @Override
  public boolean isReferencedSymbolLoaded() {
    return reference.isReferencedSymbolLoaded();
  }

  /*
  * Methods of Symbol interface
  */

  @Override
  public String getName() {
    return getReferencedSymbol().getName();
  }

  @Override
  public String getFullName() {
    return getReferencedSymbol().getFullName();
  }

  @Override
  public void setEnclosingScope(MutableScope scope) {
    getReferencedSymbol().setEnclosingScope(scope);
  }

  @Override
  public Scope getEnclosingScope() {
    return getReferencedSymbol().getEnclosingScope();
  }

  @Override
  public AccessModifier getAccessModifier() {
    return getReferencedSymbol().getAccessModifier();
  }

  @Override
  public void setAccessModifier(AccessModifier accessModifier) {
    getReferencedSymbol().setAccessModifier(accessModifier);
  }

<#-- TODO PN the methode could lead to type incompatibility, e.g., if
             the referenced symbol returns a specific Scope or MutableScope.
-->
<#if isScopeSpanningSymbol>
  /*
   * Methods of ScopeSpanningSymbol interface
   */
  //@Override
  //public Scope getSpannedScope() {
  //  return getReferencedSymbol().getSpannedScope();
  //}
</#if>

  /*
  * Methods of ${referencedSymbol} class
  */
 <#assign fields = genHelper.ruleComponents2JavaFields(ruleSymbol)>

<#list fields?keys as fname>
  <#assign nonReservedName = genHelper.nonReservedName(fname)>
  <#assign type = fields[fname]>
  <#assign setter = "set"+fname?cap_first>
  <#assign getter = genHelper.getterPrefix(type) + fname?cap_first>
  @Override
  public void ${setter}(${type} ${nonReservedName}) {
    getReferencedSymbol().${setter}(${nonReservedName});
  }

  @Override
  public ${type} ${getter}() {
    return getReferencedSymbol().${getter}();
  }
</#list>

<#if isScopeSpanningSymbol>
  <#assign fields = genHelper.symbolRuleComponents2JavaFields(ruleSymbol)>
  <#list fields?keys as fname>
    <#assign type = fields[fname]>
    <#assign getter = "get"+fname?cap_first>
  @Override
  public Collection<${type}> ${getter}() {
    return getReferencedSymbol().${getter}();
  }

  </#list>
</#if>

}

