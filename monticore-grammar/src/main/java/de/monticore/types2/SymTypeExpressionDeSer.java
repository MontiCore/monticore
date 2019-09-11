/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.types2;

import java.util.Optional;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonConstants;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.se_rwth.commons.logging.Log;

/**
 * This DeSer reailizes serialization and deserialization of 
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
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
    return "de.monticore.types2.SymTypeExpression";
  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#serialize(java.lang.Object)
   */
  @Override
  public String serialize(SymTypeExpression toSerialize) {
    return toSerialize.printAsJson();
  }
  
//  public Optional<SymTypeExpression> deserialize(JsonElement json) {
//    
//  }
  
  /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  @Override
  public Optional<SymTypeExpression> deserialize(String serialized) {
    SymTypeExpression result = null;
    
    JsonElement json = JsonParser.parseJson(serialized);
    String kind = JsonUtil.getOptStringMember(json, JsonConstants.KIND).orElse("");
    
    if (symTypeArrayDeSer.getSerializedKind().equals(kind)) {
      result = symTypeArrayDeSer.deserialize(serialized).orElse(null);
    }
    else if (symTypeConstantDeSer.getSerializedKind().equals(kind)) {
      result = symTypeConstantDeSer.deserialize(serialized).orElse(null);
    }
    else if (symTypeOfGenericsDeSer.getSerializedKind().equals(kind)) {
      result = symTypeOfGenericsDeSer.deserialize(serialized).orElse(null);
    }
    else if (symTypeOfNullDeSer.getSerializedKind().equals(kind)) {
      result = symTypeOfNullDeSer.deserialize(serialized).orElse(null);
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
    else if (symTypeVoidDeSer.getSerializedKind().equals(kind)) {
      result = symTypeVoidDeSer.deserialize(serialized).orElse(null);
    }
    else {
      Log.error("Something went wrong");
    }
    return Optional.ofNullable(result);
  }
  
}
