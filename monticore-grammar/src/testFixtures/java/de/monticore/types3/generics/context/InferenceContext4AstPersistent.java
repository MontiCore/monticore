// (c) https://github.com/MontiCore/monticore
package de.monticore.types3.generics.context;

import java.util.LinkedHashMap;

/**
 * A version of {@link InferenceContext4Ast} where the contexts are persisted,
 * even if the AST does not exist anymore.
 * Additionally, its behavior is more deterministic,
 * as the order of elements in the maps are as well.
 */
public class InferenceContext4AstPersistent extends InferenceContext4Ast {

  @Override
  public void reset() {
    expr2ctx = new LinkedHashMap<>();
    expr2resolved = new LinkedHashMap<>();
  }

}
