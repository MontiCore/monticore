/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import java.util.Optional;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonConstants;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.se_rwth.commons.logging.Log;

/**
 * This DeSer reailizes serialization and deserialization of SymTypeExpressions.
 *
 */
public class SymTypeExpressionDeSer implements IDeSer<SymTypeExpression> {
  
  protected SymTypeArrayDeSer symTypeArrayDeSer = new SymTypeArrayDeSer();
  
  protected SymTypeConstantDeSer symTypeConstantDeSer = new SymTypeConstantDeSer();
  
  protected SymTypeOfGenericsDeSer symTypeOfGenericsDeSer = new SymTypeOfGenericsDeSer();
  
  protected SymTypeOfNullDeSer symTypeOfNullDeSer = new SymTypeOfNullDeSer();
  
  protected SymTypeOfObjectDeSer symTypeOfObjectDeSer = new SymTypeOfObjectDeSer();
  
  protected SymTypePackageDeSer symTypePackageDeSer = new SymTypePackageDeSer();
  
  protected SymTypeVariableDeSer symTypeVariableDeSer = new SymTypeVariableDeSer();
  
  protected SymTypeVoidDeSer symTypeVoidDeSer = new SymTypeVoidDeSer();
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    // TODO: anpassen, nachdem package umbenannt ist
    return "de.monticore.types.check.SymTypeExpression";
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(SymTypeExpression toSerialize) {
    return toSerialize.printAsJson();
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<SymTypeExpression> deserialize(String serialized) {
    return deserialize(JsonParser.parseJson(serialized));
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  public Optional<SymTypeExpression> deserialize(JsonElement serialized) {
    SymTypeExpression result = null;
    
    // void, package, and null have special serializations (they are no json objects and do not have
    // a "kind" member)
    Optional<SymTypeVoid> deserializedVoid = symTypeVoidDeSer.deserialize(serialized);
    if (deserializedVoid.isPresent()) {
      // wrap type of optional and return
      return Optional.of(deserializedVoid.get());
    }
    Optional<SymTypeOfNull> deserializedNull = symTypeOfNullDeSer.deserialize(serialized);
    if (deserializedNull.isPresent()) {
      // wrap type of optional and return
      return Optional.of(deserializedNull.get());
    }
    Optional<SymTypePackage> deserializedPackage = symTypePackageDeSer.deserialize(serialized);
    if (deserializedPackage.isPresent()) {
      // wrap type of optional and return
      return Optional.of(deserializedPackage.get());
    }
    
    // all other serialized SymTypeExrpressions are json objects with a kind
    String kind = JsonUtil.getOptStringMember(serialized, JsonConstants.KIND).orElse("");
    
    if (symTypeArrayDeSer.getSerializedKind().equals(kind)) {
      result = symTypeArrayDeSer.deserialize(serialized).orElse(null);
    }
    else if (symTypeConstantDeSer.getSerializedKind().equals(kind)) {
      result = symTypeConstantDeSer.deserialize(serialized).orElse(null);
    }
    else if (symTypeOfGenericsDeSer.getSerializedKind().equals(kind)) {
      result = symTypeOfGenericsDeSer.deserialize(serialized).orElse(null);
    }
    else if (symTypeOfObjectDeSer.getSerializedKind().equals(kind)) {
      result = symTypeOfObjectDeSer.deserialize(serialized).orElse(null);
    }
    else if (symTypePackageDeSer.getSerializedKind().equals(kind)) {
      result = symTypePackageDeSer.deserialize(serialized).orElse(null);
    }
    else if (symTypeVariableDeSer.getSerializedKind().equals(kind)) {
      result = symTypeVariableDeSer.deserialize(serialized).orElse(null);
    }
    else {
      Log.error("Unknown kind of SymTypeExpression in SymTypeExpressionDeSer: " + kind + " in "
          + serialized);
    }
    return Optional.ofNullable(result);
  }
  
}
