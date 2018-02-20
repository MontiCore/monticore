<#-- (c) https://github.com/MontiCore/monticore -->
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

