<#-- (c) https://github.com/MontiCore/monticore -->
${signature("className", "languageName")}

<#assign genHelper = glex.getGlobalVar("stHelper")>

<#-- Copyright -->
${defineHookPoint("JavaCopyright")}

<#-- set package -->
package ${genHelper.getTargetPackage()};


import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.se_rwth.commons.logging.Log;

public class ${className} {
  
  protected boolean areSymbolsFound = false;
  
  /**
   * A list of scopes that where involved in the resolving process until now.
   */
  private final List<I${languageName}Scope> involvedScopes = new ArrayList<>();
  
  
  public boolean areSymbolsFound() {
    return areSymbolsFound;
  }
  
  public void updateSymbolsFound(boolean areSymbolsFound) {
    this.areSymbolsFound = this.areSymbolsFound || areSymbolsFound;
  }
  
  public void addInvolvedScope(final I${languageName}Scope scope) {
    involvedScopes.add(Log.errorIfNull(scope));
  }
  
  public List<I${languageName}Scope> getInvolved${languageName}Scopes() {
    return ImmutableList.copyOf(involvedScopes);
  }
  
}