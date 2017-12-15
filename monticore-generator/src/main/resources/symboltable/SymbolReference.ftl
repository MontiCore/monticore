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
${signature("className", "ruleSymbol", "isScopeSpanningSymbol", "hwSymbolExists")}
<#assign genHelper = glex.getGlobalVar("stHelper")>
<#assign ruleName = ruleSymbol.getName()?cap_first>
<#assign referencedSymbol = ruleName+"Symbol">
<#assign package = genHelper.getTargetPackage()?lower_case>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

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


<#if !hwSymbolExists>

<#-- the method could lead to type incompatibility, e.g., if
     the referenced symbol returns a specific Scope or MutableScope. -->

<#if isScopeSpanningSymbol>
  /*
   * Methods of ScopeSpanningSymbol interface
   */
  @Override
  public Scope getSpannedScope() {
    return getReferencedSymbol().getSpannedScope();
  }
</#if>


</#if>

}

