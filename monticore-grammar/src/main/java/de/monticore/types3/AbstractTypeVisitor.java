// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import de.monticore.symbols.basicsymbols._symboltable.IBasicSymbolsScope;
import de.monticore.symboltable.IScope;
import de.monticore.types3.generics.context.InferenceContext4Ast;
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
   * However, if a AstNode can have multiple Types
   * (e.g., variants within the same model),
   * one may set a different map to be filled.
   */
  protected Type4Ast type4Ast = tmpMap;

  /**
   * the map to be filled with inference context information,
   * s. {@link InferenceContext4Ast}.
   */
  protected InferenceContext4Ast ctx4Ast;

  public void setType4Ast(Type4Ast type4Ast) {
    this.type4Ast = type4Ast;
  }

  public void setContext4Ast(InferenceContext4Ast ctx4Ast) {
    this.ctx4Ast = ctx4Ast;
  }

  /**
   * the map to be filled with type information.
   * Deriving classes should always use this method to get the map.
   */
  protected Type4Ast getType4Ast() {
    if (type4Ast == null) {
      Log.error("0xFD335 internal error: type4Ast not set()."
          + " Check the type traverser setup."
      );
    }
    return type4Ast;
  }

  /**
   * the map to be filled with type context information.
   * Deriving classes should always use this method to get the map.
   * Only dedicated visitors for target types usage
   * (mostly called CTTIVisitor) should make use of this map.
   * It is set to null for all over visitors!
   */
  protected InferenceContext4Ast getInferenceContext4Ast() {
    if (ctx4Ast == null) {
      Log.error("0xFD336 internal error: InferenceContext4Ast not set()."
          + " Check the type traverser setup."
      );
    }
    return ctx4Ast;
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
