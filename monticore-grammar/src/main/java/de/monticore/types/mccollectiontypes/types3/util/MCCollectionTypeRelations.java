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
    if (!type.isGenericType()) {
      return false;
    }
    SymTypeOfGenerics generic = (SymTypeOfGenerics) type;
    String name = generic.getTypeConstructorFullName();
    if (!name.equals("List") && !name.equals("java.util.List")) {
      return false;
    }
    if (generic.sizeArguments() != 1) {
      Log.warn("0xFD1C1 encountered generic called "
          + generic.getTypeConstructorFullName() + " with "
          + generic.sizeArguments() + " type arguments.");
      return false;
    }
    return true;
  }

  public boolean isSet(SymTypeExpression type) {
    if (!type.isGenericType()) {
      return false;
    }
    SymTypeOfGenerics generic = (SymTypeOfGenerics) type;
    String name = generic.getTypeConstructorFullName();
    if (!name.equals("Set") && !name.equals("java.util.Set")) {
      return false;
    }
    if (generic.sizeArguments() != 1) {
      Log.warn("0xFD1C2 encountered generic called "
          + generic.getTypeConstructorFullName() + " with "
          + generic.sizeArguments() + " type arguments.");
      return false;
    }
    return true;
  }

  public boolean isOptional(SymTypeExpression type) {
    if (!type.isGenericType()) {
      return false;
    }
    SymTypeOfGenerics generic = (SymTypeOfGenerics) type;
    String name = generic.getTypeConstructorFullName();
    if (!name.equals("Optional") && !name.equals("java.util.Optional")) {
      return false;
    }
    if (generic.sizeArguments() != 1) {
      Log.warn("0xFD1C3 encountered generic called "
          + generic.getTypeConstructorFullName() + " with "
          + generic.sizeArguments() + " type arguments.");
      return false;
    }
    return true;
  }

  public boolean isMap(SymTypeExpression type) {
    if (!type.isGenericType()) {
      return false;
    }
    SymTypeOfGenerics generic = (SymTypeOfGenerics) type;
    String name = generic.getTypeConstructorFullName();
    if (!name.equals("Map") && !name.equals("java.util.Map")) {
      return false;
    }
    if (generic.sizeArguments() != 2) {
      Log.warn("0xFD1C4 encountered generic called "
          + generic.getTypeConstructorFullName() + " with "
          + generic.sizeArguments() + " type arguments.");
      return false;
    }
    return true;
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

}
