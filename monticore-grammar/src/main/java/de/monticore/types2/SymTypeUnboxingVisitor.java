// (c) https://github.com/MontiCore/monticore
package de.monticore.types2;

import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.check.SymTypeOfGenerics;
import de.monticore.types.check.SymTypeOfObject;
import de.se_rwth.commons.logging.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Unboxes SymTypeExpressions,
 * including, but not limited to, Java primitive unboxing
 * e.g., java.lang.Integer -> int
 * e.g., java.util.List -> List
 * Usage:
 * calculate(symType)
 */
public class SymTypeUnboxingVisitor extends SymTypeDeepCloneVisitor {

  /**
   * Map for unboxing to primitive types (e.g. "java.lang.Integer" -> "int")
   * Results are fully qualified.
   */
  protected static final Map<String, String> primitiveUnboxMap;

  /**
   * Map for unboxing to object types (e.g. "java.lang.String" -> "String")
   * Results are fully qualified.
   */
  protected static final Map<String, String> objectUnboxMap;

  /**
   * Map for unboxing to generic types (e.g. "java.util.List" -> "List")
   * Results are fully qualified.
   */
  protected static final Map<String, String> genericUnboxMap;

  public Map<String, String> getPrimitiveUnboxMap() {
    return primitiveUnboxMap;
  }

  public Map<String, String> getObjectUnboxMap() {
    return objectUnboxMap;
  }

  public Map<String, String> getGenericUnboxMap() {
    return genericUnboxMap;
  }

  /**
   * initializing the maps
   */
  static {
    Map<String, String> primitiveUnboxMap_temp = new HashMap<>();
    primitiveUnboxMap_temp.put("java.lang.Boolean", "boolean");
    primitiveUnboxMap_temp.put("java.lang.Byte", "byte");
    primitiveUnboxMap_temp.put("java.lang.Character", "char");
    primitiveUnboxMap_temp.put("java.lang.Double", "double");
    primitiveUnboxMap_temp.put("java.lang.Float", "float");
    primitiveUnboxMap_temp.put("java.lang.Integer", "int");
    primitiveUnboxMap_temp.put("java.lang.Long", "long");
    primitiveUnboxMap_temp.put("java.lang.Short", "short");
    primitiveUnboxMap = Collections.unmodifiableMap(primitiveUnboxMap_temp);

    Map<String, String> objectUnboxMap_temp = new HashMap<>();
    objectUnboxMap_temp.put("java.lang.String", "String");
    objectUnboxMap = Collections.unmodifiableMap(objectUnboxMap_temp);

    Map<String, String> genericUnboxMap_temp = new HashMap<>();
    genericUnboxMap_temp.put("java.util.Optional", "Optional");
    genericUnboxMap_temp.put("java.util.Set", "Set");
    genericUnboxMap_temp.put("java.util.List", "List");
    genericUnboxMap_temp.put("java.util.Map", "Map");
    genericUnboxMap = Collections.unmodifiableMap(genericUnboxMap_temp);
  }

  @Override
  public void visit(SymTypeOfGenerics symType) {
    final String name = symType.getTypeConstructorFullName();
    Optional<TypeSymbol> typeSymbolOpt =
        resolveUnboxedSymType(name, getGenericUnboxMap());
    TypeSymbol typeSymbol = typeSymbolOpt.orElse(symType.getTypeInfo());
    pushTransformedSymType(SymTypeExpressionFactory.createGenerics(
        typeSymbol,
        applyToCollection(symType.getArgumentList())
    ));
  }

  @Override
  public void visit(SymTypeOfObject symType) {
    final String name = symType.printFullName();
    // try primitives first
    Optional<TypeSymbol> typeSymbolOpt =
        resolveUnboxedSymType(name, getPrimitiveUnboxMap());
    if (typeSymbolOpt.isPresent()) {
      pushTransformedSymType(
          SymTypeExpressionFactory.createPrimitive(typeSymbolOpt.get())
      );
    }
    else {
      // if it was no primitive, it might be an object
      typeSymbolOpt = resolveUnboxedSymType(name, getObjectUnboxMap());
      pushTransformedSymType(SymTypeExpressionFactory.createTypeObject(
          typeSymbolOpt.orElse(symType.getTypeInfo())
      ));
    }
  }

  // Helpers

  /**
   * iff there is a unboxed variant of the given symtype,
   * this tries to resolve it
   */
  protected Optional<TypeSymbol> resolveUnboxedSymType(String name, Map<String, String> unboxMap) {
    // getting the correct name to use of a SymTypeExpression is inconsistent,
    // as such we pass it as a parameter along with the map
    if (unboxMap.containsKey(name)) {
      final String unboxedName = unboxMap.get(name);
      Optional<TypeSymbol> unboxedTypeSymbolOpt =
          BasicSymbolsMill.globalScope().resolveType(unboxedName);
      if (!unboxedTypeSymbolOpt.isPresent()) {
        Log.info("symbol for unboxed type "
                + unboxedName
                + " is not found for type "
                + name
                + " and is thus not used",
            "Typing");
      }
      return unboxedTypeSymbolOpt;
    }
    else {
      return Optional.empty();
    }
  }

}
