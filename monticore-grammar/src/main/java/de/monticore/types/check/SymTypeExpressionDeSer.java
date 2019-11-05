/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.symboltable.serialization.IDeSer;
import de.monticore.symboltable.serialization.JsonParser;
import de.monticore.symboltable.serialization.JsonUtil;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.se_rwth.commons.logging.Log;

/**
 * This DeSer reailizes serialization and deserialization of SymTypeExpressions.
 */
public class SymTypeExpressionDeSer implements IDeSer<SymTypeExpression> {

  /**
   * The singleton that DeSerializes all SymTypeExpressions.
   * It is stateless and can be reused recursively.
   */
  protected static SymTypeExpressionDeSer instance = new SymTypeExpressionDeSer();
  // not realized as static delegator, but only as singleton

  protected SymTypeArrayDeSer symTypeArrayDeSer = new SymTypeArrayDeSer();

  protected SymTypeConstantDeSer symTypeConstantDeSer = new SymTypeConstantDeSer();

  protected SymTypeOfGenericsDeSer symTypeOfGenericsDeSer = new SymTypeOfGenericsDeSer();

  protected SymTypeOfObjectDeSer symTypeOfObjectDeSer = new SymTypeOfObjectDeSer();

  protected SymTypeVariableDeSer symTypeVariableDeSer = new SymTypeVariableDeSer();

  protected SymTypeExpressionDeSer() {
    //this is a singleton, do not use constructor
  }

  public static SymTypeExpressionDeSer getInstance() {
    if (null == instance) {
      instance = new SymTypeExpressionDeSer();
    }
    return instance;
  }

  /**
   * This method can be used to set the instance of the SymTypeExpressionDeSer to a custom suptype
   *
   * @param theInstance
   */
  public static void setInstance(SymTypeExpressionDeSer theInstance) {
    if (null == theInstance) {  //in this case, "reset" to default type
      instance = new SymTypeExpressionDeSer();
    }
    else {
      instance = theInstance;
    }
  }

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#getSerializedKind()
   */
  @Override
  public String getSerializedKind() {
    // Care: this String is never to occur, because all subclasses override this function
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
  public SymTypeExpression deserialize(String serialized) {
    return deserialize(JsonParser.parse(serialized));
  }

  /**
   * @see de.monticore.symboltable.serialization.IDeSer#deserialize(java.lang.String)
   */
  public SymTypeExpression deserialize(JsonElement serialized) {

    // void and null are stored as strings
    if (serialized.isJsonString()) {
      String value = serialized.getAsJsonString().getValue();

      if (value.equals(DefsTypeBasic._nullTypeString)) {
        return SymTypeExpressionFactory.createTypeOfNull();
      }
      else if (value.equals(DefsTypeBasic._voidTypeString)) {
        return SymTypeExpressionFactory.createTypeVoid();
      }
      else {
        Log.error(
            "0x823F3 Internal error: Loading ill-structured SymTab: Unknown serialization of SymTypeExpression: "
                + serialized);
        return null;
      }
    }

    // all other serialized SymTypeExrpressions are json objects with a kind
    if (JsonUtil.isCorrectDeSerForKind(symTypeArrayDeSer, serialized)) {
      return symTypeArrayDeSer.deserialize(serialized);
    }
    else if (JsonUtil.isCorrectDeSerForKind(symTypeConstantDeSer, serialized)) {
      return symTypeConstantDeSer.deserialize(serialized);
    }
    else if (JsonUtil.isCorrectDeSerForKind(symTypeOfGenericsDeSer, serialized)) {
      return symTypeOfGenericsDeSer.deserialize(serialized);
    }
    else if (JsonUtil.isCorrectDeSerForKind(symTypeOfObjectDeSer, serialized)) {
      return symTypeOfObjectDeSer.deserialize(serialized);
    }
    else if (JsonUtil.isCorrectDeSerForKind(symTypeVariableDeSer, serialized)) {
      return symTypeVariableDeSer.deserialize(serialized);
    }
    else {
      Log.error(
          "0x823FE Internal error: Loading ill-structured SymTab: Unknown serialization of SymTypeExpression: "
              + serialized);
      return null;
    }
  }

}
