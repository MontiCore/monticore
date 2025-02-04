// (c) https://github.com/MontiCore/monticore
package de.monticore.types.mccollectiontypes.types3;

import de.monticore.types.check.SymTypeExpression;

/**
 * @deprecated use MCCollectionSymTypeRelations
 */
@Deprecated
public interface IMCCollectionTypeRelations {

  /**
   * whether the type is one of the four collection types
   * from MCCollectionTypes; List, Set, Optional, Map.
   * Can be extended (e.g., in OCL)
   */
  default boolean isCollection(SymTypeExpression type) {
    return isList(type) || isSet(type) || isOptional(type) || isMap(type);
  }

  boolean isList(SymTypeExpression type);

  boolean isSet(SymTypeExpression type);

  boolean isOptional(SymTypeExpression type);

  boolean isMap(SymTypeExpression type);

  /**
   * @return the Element type of a collection.
   * In case of a Map, this is the value type.
   */
  SymTypeExpression getCollectionElementType(SymTypeExpression type);

  SymTypeExpression getMapKeyType(SymTypeExpression type);

}
