// (c) https://github.com/MontiCore/monticore
package de.monticore.types2;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfObject;
import de.monticore.types.check.SymTypePrimitive;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Boxes SymTypeExpressions,
 * including, but not limited to, Java primitive boxing
 * e.g., int -> java.lang.Integer
 * e.g., List -> java.util.List
 * Usage:
 * calculate(symType)
 */
public class SymTypeBoxingVisitor extends SymTypeDeepCloneVisitor {

  /**
   * Map for boxing primitive types (e.g. "int" -> "java.lang.Integer")
   * Results are fully qualified.
   */
  protected static final Map<String, String> primitiveBoxMap;

  /**
   * Map for boxing object types (e.g. "String" -> "java.lang.String")
   * Results are fully qualified.
   */
  protected static final Map<String, String> objectBoxMap;

  /**
   * Map for boxing generic types (e.g. "List" -> "java.util.List")
   * Results are fully qualified.
   */
  protected static final Map<String, String> genericBoxMap;

  public Map<String, String> getPrimitiveBoxMap() {
    return primitiveBoxMap;
  }

  public Map<String, String> getObjectBoxMap() {
    return objectBoxMap;
  }

  public Map<String, String> getGenericBoxMap() {
    return genericBoxMap;
  }

  /**
   * initializing the maps
   */
  static {
    Map<String, String> primitiveBoxMap_temp = new HashMap<>();
    primitiveBoxMap_temp.put("boolean", "java.lang.Boolean");
    primitiveBoxMap_temp.put("byte", "java.lang.Byte");
    primitiveBoxMap_temp.put("char", "java.lang.Character");
    primitiveBoxMap_temp.put("double", "java.lang.Double");
    primitiveBoxMap_temp.put("float", "java.lang.Float");
    primitiveBoxMap_temp.put("int", "java.lang.Integer");
    primitiveBoxMap_temp.put("long", "java.lang.Long");
    primitiveBoxMap_temp.put("short", "java.lang.Short");
    primitiveBoxMap = Collections.unmodifiableMap(primitiveBoxMap_temp);

    Map<String, String> objectBoxMap_temp = new HashMap<>();
    objectBoxMap_temp.put("String", "java.lang.String");
    objectBoxMap = Collections.unmodifiableMap(objectBoxMap_temp);

    Map<String, String> genericBoxMap_temp = new HashMap<>();
    genericBoxMap_temp.put("Optional", "java.util.Optional");
    genericBoxMap_temp.put("Set", "java.util.Set");
    genericBoxMap_temp.put("List", "java.util.List");
    genericBoxMap_temp.put("Map", "java.util.Map");
    genericBoxMap = Collections.unmodifiableMap(genericBoxMap_temp);
  }

  @Override
  public void visit(SymTypeOfGenerics symType) {
    final String name = symType.getTypeConstructorFullName();
    Optional<TypeSymbol> typeSymbolOpt =
        resolveBoxedSymType(name, getGenericBoxMap());
    TypeSymbol typeSymbol = typeSymbolOpt.orElse(symType.getTypeInfo());
    pushTransformedSymType(SymTypeExpressionFactory.createGenerics(
        typeSymbol,
        applyToCollection(symType.getArgumentList())
    ));
  }

  @Override
  public void visit(SymTypeOfObject symType) {
    final String name = symType.printFullName();
    Optional<TypeSymbol> typeSymbolOpt =
        resolveBoxedSymType(name, getObjectBoxMap());
    if (typeSymbolOpt.isPresent()) {
      pushTransformedSymType(
          SymTypeExpressionFactory.createTypeObject(typeSymbolOpt.get())
      );
    }
    else {
      pushTransformedSymType(symType);
    }
  }

  @Override
  public void visit(SymTypePrimitive symType) {
    final String name = symType.getPrimitiveName();
    Optional<TypeSymbol> typeSymbolOpt =
        resolveBoxedSymType(name, getPrimitiveBoxMap());
    if (typeSymbolOpt.isPresent()) {
      pushTransformedSymType(
          SymTypeExpressionFactory.createTypeObject(typeSymbolOpt.get())
      );
    }
    else {
      pushTransformedSymType(symType);
    }
  }

  // Helpers

  /**
   * iff there is a boxed variant of the given symtype,
   * this tries to resolve it
   */
  protected Optional<TypeSymbol> resolveBoxedSymType(String name, Map<String, String> boxMap) {
    // getting the correct name to use of a SymTypeExpression is inconsistent,
    // as such we pass it as a parameter along with the map
    if (boxMap.containsKey(name)) {
      final String boxedName = boxMap.get(name);
      Optional<TypeSymbol> boxedTypeSymbolOpt =
          BasicSymbolsMill.globalScope().resolveType(boxedName);
      if (!boxedTypeSymbolOpt.isPresent()) {
        Log.info("symbol for boxed type "
                + boxedName
                + " is not found for type "
                + name
                + " and is thus not used",
            "Typing");
      }
      return boxedTypeSymbolOpt;
    }
    else {
      return Optional.empty();
    }
  }

}
