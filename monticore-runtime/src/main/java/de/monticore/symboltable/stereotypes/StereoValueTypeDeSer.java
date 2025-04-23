/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.stereotypes;

import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.UserJsonString;
import de.se_rwth.commons.logging.Log;

public class StereoValueTypeDeSer {

  public static final String NONE = "none";
  public static final String OBJECT = "object";
  public static final String BOOLEAN = "boolean";
  public static final String INT = "int";
  public static final String LONG = "long";
  public static final String FLOAT = "float";
  public static final String DOUBLE = "double";
  public static final String CHAR = "char";
  public static final String STRING = "String";

  protected static StereoValueTypeDeSer instance;

  public static StereoValueTypeDeSer getInstance() {
    if (instance == null) {
      instance = new StereoValueTypeDeSer();
    }

    return instance;
  }

  public static JsonElement serializeStereoValueType(StereoValueType type) {
    return getInstance().doSerializeStereoValueType(type);
  }

  protected JsonElement doSerializeStereoValueType(StereoValueType type) {
    switch (type) {
      case NONE: return new UserJsonString(NONE);
      case OBJECT: return new UserJsonString(OBJECT);
      case BOOLEAN: return new UserJsonString(BOOLEAN);
      case INT: return new UserJsonString(INT);
      case LONG: return new UserJsonString(LONG);
      case FLOAT: return new UserJsonString(FLOAT);
      case DOUBLE: return new UserJsonString(DOUBLE);
      case CHAR: return new UserJsonString(CHAR);
      case STRING: return new UserJsonString(STRING);
      default: throw new IllegalStateException();
    }
  }

  public static StereoValueType deserializeStereoValueType(JsonElement json) {
    return getInstance().doDeserializeStereoValueType(json);
  }

  protected StereoValueType doDeserializeStereoValueType(JsonElement json) {
    if (json.isJsonString()) {
      switch(json.getAsJsonString().getValue()) {
        case NONE: return StereoValueType.NONE;
        case OBJECT: return StereoValueType.OBJECT;
        case BOOLEAN: return StereoValueType.BOOLEAN;
        case INT: return StereoValueType.INT;
        case LONG: return StereoValueType.LONG;
        case FLOAT: return StereoValueType.FLOAT;
        case DOUBLE: return StereoValueType.DOUBLE;
        case CHAR: return StereoValueType.CHAR;
        case STRING: return StereoValueType.STRING;
        default:  // continue to error below
      }
    }

    Log.error(
      "0x823FF Internal error: Loading ill-structured SymTab: Unknown serialization of" +
        "StereoValueType: " + json);
    return null;
  }
}
