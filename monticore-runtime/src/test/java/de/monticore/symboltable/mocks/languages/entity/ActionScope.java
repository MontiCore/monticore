/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import java.util.Optional;

import de.monticore.symboltable.CommonScope;
import de.monticore.symboltable.MutableScope;

public class ActionScope extends CommonScope {

  public ActionScope(ActionSymbol spanningSymbol, Optional<MutableScope> enclosingScope) {
    super(enclosingScope, true);
    setSpanningSymbol(spanningSymbol);
  }
  
  public ActionScope(ActionSymbol spanningSymbol) {
    this(spanningSymbol, Optional.empty());
  }
  
}
