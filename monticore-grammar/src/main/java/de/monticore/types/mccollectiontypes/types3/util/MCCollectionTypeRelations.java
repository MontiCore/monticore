// (c) https://github.com/MontiCore/monticore
package de.monticore.types.mccollectiontypes.types3.util;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.se_rwth.commons.logging.Log;

/**
 * relations for built-in Collection SymTypes of MCCollectionTypes
 * these are List, Set, Optional, Map
 * This does NOT include types that inherit from collection types
 */
public class MCCollectionTypeRelations {

  /**
   * whether the type is one of the four collection types
   * from MCCollectionTypes; List, Set, Optional, Map
   */
  public boolean isCollection(SymTypeExpression type) {
    return isList(type) || isSet(type) || isOptional(type) || isMap(type);
  }

  public boolean isList(SymTypeExpression type) {
    return isSpecificCollection(type, "List", "java.util.List", 1);
  }

  public boolean isSet(SymTypeExpression type) {
    return isSpecificCollection(type, "Set", "java.util.Set", 1);
  }

  public boolean isOptional(SymTypeExpression type) {
    return isSpecificCollection(type, "Optional", "java.util.Optional", 1);
  }

  public boolean isMap(SymTypeExpression type) {
    return isSpecificCollection(type, "Map", "java.util.Map", 2);
  }

  /**
   * @return the Element type of a collection.
   * In case of a Map, this is the value type.
   */
  public SymTypeExpression getCollectionElementType(SymTypeExpression type) {
    if (!isCollection(type)) {
      Log.error("0xFD1C7 internal error: tried to get the type "
          + "of an collection's element of a non collection type");
      return SymTypeExpressionFactory.createObscureType();
    }
    // in case of List, Set, Optional we only have one type parameter,
    // in case of Map the second type parameter is the value type.
    SymTypeOfGenerics collectionType = (SymTypeOfGenerics) type;
    return collectionType.getArgument(collectionType.sizeArguments() - 1);
  }

  public SymTypeExpression getMapKeyType(SymTypeExpression type) {
    if (!isMap(type)) {
      Log.error("0xFD1C8 internal error: tried to get a map's key type "
          + "of a non map type");
      return SymTypeExpressionFactory.createObscureType();
    }
    return ((SymTypeOfGenerics) type).getArgument(0);
  }

  // Helper

  protected boolean isSpecificCollection(
      SymTypeExpression type,
      String unboxedName,
      String boxedName,
      int numberOfArgs
  ) {
    if (!type.isGenericType()) {
      return false;
    }
    SymTypeOfGenerics generic = (SymTypeOfGenerics) type;
    String name = generic.getTypeConstructorFullName();
    if (!name.equals(unboxedName) && !name.equals(boxedName)) {
      return false;
    }
    if (generic.sizeArguments() != numberOfArgs) {
      Log.warn("0xFD1C4 encountered generic called "
          + generic.getTypeConstructorFullName() + " with "
          + generic.sizeArguments() + " type arguments, "
          + "but expected " + numberOfArgs);
      return false;
    }
    return true;
  }

}
