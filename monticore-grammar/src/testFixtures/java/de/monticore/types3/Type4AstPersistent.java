// (c) https://github.com/MontiCore/monticore
package de.monticore.types3;

import java.util.LinkedHashMap;

/**
 * A version of {@link Type4Ast} where the types are persisted,
 * even if the AST does not exist anymore.
 * Additionally, its behavior is more deterministic,
 * as the order of elements in the maps are as well.
 */
public class Type4AstPersistent extends Type4Ast {

  @Override
  public void reset() {
    expr2type = new LinkedHashMap<>();
    typeID2type = new LinkedHashMap<>();
  }
}
