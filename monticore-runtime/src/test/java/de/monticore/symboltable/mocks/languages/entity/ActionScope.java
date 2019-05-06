/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.Scope;

import java.util.Optional;

public class ActionScope extends CommonScope {

  public ActionScope(ActionSymbol spanningSymbol, Optional<Scope> enclosingScope) {
    super(enclosingScope, true);
    setSpanningSymbol(spanningSymbol);
  }
  
  public ActionScope(ActionSymbol spanningSymbol) {
    this(spanningSymbol, Optional.empty());
  }
  
}
