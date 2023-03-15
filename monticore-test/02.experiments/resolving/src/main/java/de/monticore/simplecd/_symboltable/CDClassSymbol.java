/* (c) https://github.com/MontiCore/monticore */
package de.monticore.simplecd._symboltable;

import com.google.common.collect.Lists;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.IArtifactScope;
import de.monticore.symboltable.IScope;

import java.util.List;

public class CDClassSymbol extends CDClassSymbolTOP {
  public CDClassSymbol(String name) {
    super(name);
  }


  /** search in the scope for methods with a specific name */
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

}
