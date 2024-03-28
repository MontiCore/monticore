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

  /**
   * the name to be used for Log.info, etc.
   */
  protected static final String LOG_NAME = "TypeVisitor";

  //to be moved to the mill after specifics are discussed, DO NOT USE
  public static Type4Ast tmpMap = new Type4Ast();

  /**
   * the map to be filled with type information
   * In most cases, this is the global map in the mill.
   * However, if a AstNode kann have multiple Types
   * (e.g., variants within the same model),
   * one may set a different map to be filled.
   */
  protected Type4Ast type4Ast = tmpMap;

  public void setType4Ast(Type4Ast type4Ast) {
    this.type4Ast = type4Ast;
  }

  /**
   * the map to be filled with type information.
   * Deriving classes should always use this method to get the map.
   */
  protected Type4Ast getType4Ast() {
    return type4Ast;
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
