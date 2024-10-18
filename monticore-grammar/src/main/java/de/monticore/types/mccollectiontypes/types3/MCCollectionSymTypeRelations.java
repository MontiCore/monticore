// (c) https://github.com/MontiCore/monticore
package de.monticore.types.mccollectiontypes.types3;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mccollectiontypes.types3.util.MCCollectionTypeRelations;
import de.monticore.types3.SymTypeRelations;
import de.se_rwth.commons.logging.Log;

/**
 * relations for built-in Collection SymTypes of MCCollectionTypes
 * these are List, Set, Optional, Map
 * Per default, this does NOT include types that inherit from collection types
 */
public class MCCollectionSymTypeRelations extends SymTypeRelations {

  protected static MCCollectionTypeRelations mcCollectionTypeRelations;

  public static void init() {
    Log.trace("init MCCollectionSymTypeRelations", "TypeCheck setup");
    SymTypeRelations.init();
    mcCollectionTypeRelations = new MCCollectionTypeRelations();
  }

  /**
   * whether the type is one of the four collection types
   * from MCCollectionTypes; List, Set, Optional, Map
   */
  public static boolean isMCCollection(SymTypeExpression type) {
    return getMcCollectionTypeRelations().isMCCollection(type);
  }

  public static boolean isList(SymTypeExpression type) {
    return getMcCollectionTypeRelations().isList(type);
  }

  public static boolean isSet(SymTypeExpression type) {
    return getMcCollectionTypeRelations().isSet(type);
  }

  public static boolean isOptional(SymTypeExpression type) {
    return getMcCollectionTypeRelations().isOptional(type);
  }

  public static boolean isMap(SymTypeExpression type) {
    return getMcCollectionTypeRelations().isMap(type);
  }

  /**
   * @return the Element type of a collection.
   *     In case of a Map, this is the value type.
   */
  public static SymTypeExpression getCollectionElementType(SymTypeExpression type) {
    return getMcCollectionTypeRelations().getCollectionElementType(type);
  }

  public static SymTypeExpression getMapKeyType(SymTypeExpression type) {
    return getMcCollectionTypeRelations().getMapKeyType(type);
  }

  // Helper

  protected static MCCollectionTypeRelations getMcCollectionTypeRelations() {
    if (mcCollectionTypeRelations == null) {
      Log.error("0xFD9CC internal error: "
          + "MCCollectionSymTypes was not init()-ialized."
      );
    }
    return mcCollectionTypeRelations;
  }
}
