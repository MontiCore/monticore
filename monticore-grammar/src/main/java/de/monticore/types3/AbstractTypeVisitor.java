// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.IScope;
import de.se_rwth.commons.logging.Log;

/**
 * extended by the main type system visitors
 * gives access to common functionality, e.g., the map from expression to type
 */
public abstract class AbstractTypeVisitor {

  //to be moved to the mill after specifics are discussed, DO NOT USE
  public static Type4Ast tmpMap = new Type4Ast();

  /**
   * the name to be used for Log.info, etc.
   */
  protected static final String LOG_NAME = "TypeVisitor";

  protected Type4Ast getType4Ast() {
    return tmpMap;
  }

  protected IBasicSymbolsScope getAsBasicSymbolsScope(IScope scope) {
    // is accepted only here, decided on 07.04.2020
    if (!(scope instanceof IBasicSymbolsScope)) {
      Log.error("0xA2307 the enclosing scope of the expression"
          + " does not implement the interface IBasicSymbolsScope");
    }
    // is accepted only here, decided on 07.04.2020
    return (IBasicSymbolsScope) scope;
  }

}
