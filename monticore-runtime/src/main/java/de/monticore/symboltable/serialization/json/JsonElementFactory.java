/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

/**
 * This factory creates instances of JsonElements. 
 * It can be used to instantiate subtypes of the Json 
 * classes instead of the default ones.
 */
public class JsonElementFactory {
  
  public JsonElementFactory() {
  }
  
  protected static JsonElementFactory instance;
  
  public static void setInstance(JsonElementFactory instance) {
    JsonElementFactory.instance = instance;
  }
  
  protected JsonArray doCreateJsonArray() {
    return new JsonArray();
  }
  
  protected JsonBoolean doCreateJsonBoolean(boolean value) {
    return new JsonBoolean(value);
  }
  
  protected JsonNull doCreateJsonNull() {
    return new JsonNull();
  }
  
  protected JsonNumber doCreateJsonNumber(String value) {
    return new JsonNumber(value);
  }
  
  protected JsonObject doCreateJsonObject() {
    return new JsonObject();
  }
  
  protected JsonString doCreateJsonString(String value) {
    return new JsonString(value);
  }
  
  public static JsonArray createJsonArray() {
    return instance.doCreateJsonArray();
  }
  
  public static JsonBoolean createJsonBoolean(boolean value) {
    return instance.doCreateJsonBoolean(value);
  }
  
  public static JsonNull createJsonNull() {
    return instance.doCreateJsonNull();
  }
  
  public static JsonNumber createJsonNumber(String value) {
    return instance.doCreateJsonNumber(value);
  }
  
  public static JsonObject createJsonObject() {
    return instance.doCreateJsonObject();
  }
  
  public static JsonString createJsonString(String value) {
    return instance.doCreateJsonString(value);
  }
  
}
