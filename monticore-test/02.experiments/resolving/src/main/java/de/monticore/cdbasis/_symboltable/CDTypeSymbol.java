/* (c) https://github.com/MontiCore/monticore */
package de.monticore.cdbasis._symboltable;

import com.google.common.collect.Lists;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.IArtifactScope;
import de.monticore.symboltable.IScope;

import java.util.List;

public class CDTypeSymbol extends CDTypeSymbolTOP {
  public CDTypeSymbol(String name) {
    super(name);
  }

  /** get a list of all the fields the type definition can access */
  public List<FieldSymbol> getFieldList() {
    if (spannedScope == null) {
      return Lists.newArrayList();
    }
    return getSpannedScope().getLocalFieldSymbols();
  }

  /** search in the scope for methods with a specific name */
  public List<FieldSymbol> getFieldList(String fieldname) {
    return getSpannedScope().resolveFieldMany(fieldname);
  }

  @Override
  protected String determinePackageName() {
    IScope optCurrentScope = enclosingScope;
    while (optCurrentScope != null) {
      final IScope currentScope = optCurrentScope;
      if (currentScope.isPresentSpanningSymbol()) {
        // If one of the enclosing scope(s) is spanned by a symbol, take its
        // package name. This check is important, since the package name of the
        // enclosing symbol might be set manually.
        return currentScope.getSpanningSymbol().getPackageName();
      } else if (currentScope instanceof IArtifactScope) {
        return ((IArtifactScope) currentScope).getFullName();
      }
      optCurrentScope = currentScope.getEnclosingScope();
    }
    return "";
  }

  public String getInternalQualifiedName() {
    String internalName = getFullName();
    IScope as = getEnclosingScope();
    while (!(as instanceof IArtifactScope)) {
      as = as.getEnclosingScope();
    }
    String artifactName = ((IArtifactScope) as).getFullName();
    if (!artifactName.isEmpty()) {
      internalName = internalName.substring(artifactName.length() + 1);
    }
    return internalName;
  }

}
