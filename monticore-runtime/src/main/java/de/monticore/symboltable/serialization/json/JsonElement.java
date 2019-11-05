/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization.json;

import de.se_rwth.commons.logging.Log;

/**
 * Common Interface for all Json metamodel Elements (e.g., JsonArray, JsonBoolean,...). By default,
 * all methods for checking the concrete type of a metamodel element (e.g., isJsonArray()) return
 * false. In each concrete type, the respective method must be overridden to return true. This
 * avoids instanceof expressions. Similarly, the getAs methods (e.g., getAsJsonArray()) return an
 * error if they are called. In each concrete type, the respective method must be overridden to
 * return "this". This avoids down casts to the concrete type.
 */
public interface JsonElement {
  
  /**
   * @return true iff this object is of type JsonObject and false otherwise.
   */
  default public boolean isJsonObject() {
    return false;
  }
  
  /**
   * @return true iff this object is of type JsonArray and false otherwise.
   */
  default public boolean isJsonArray() {
    return false;
  }
  
  /**
   * @return true iff this object is of type JsonBoolean and false otherwise.
   */
  default public boolean isJsonBoolean() {
    return false;
  }
  
  /**
   * @return true iff this object is of type JsonString and false otherwise.
   */
  default public boolean isJsonString() {
    return false;
  }
  
  /**
   * @return true iff this object is of type JsonNumber and false otherwise.
   */
  default public boolean isJsonNumber() {
    return false;
  }
  
  /**
   * @return true iff this object is of type JsonNull and false otherwise.
   */
  default public boolean isJsonNull() {
    return false;
  }
  
  
  /**
   * @return this object as JsonObject if it is of this type and throws an error otherwise.
   */
  default public JsonObject getAsJsonObject() {
    Log.error("0xA0605 "+ this + " is not a Json Object!");
    return null;
  }
  
  /**
   * @return this object as JsonArray if it is of this type and throws an error otherwise.
   */
  default public JsonArray getAsJsonArray() {
    Log.error("0xA0606 "+ this + " is not a Json Array!");
    return null;
  }
  
  /**
   * @return this object as JsonBoolean if it is of this type and throws an error otherwise.
   */
  default public JsonBoolean getAsJsonBoolean() {
    Log.error("0xA0607 "+ this + " is not a Json Boolean!");
    return null;
  }
  
  /**
   * @return this object as JsonString if it is of this type and throws an error otherwise.
   */
  default public JsonString getAsJsonString() {
    Log.error("0xA0608 "+ this + " is not a Json String!");
    return null;
  }
  
  /**
   * @return this object as JsonNumber if it is of this type and throws an error otherwise.
   */
  default public JsonNumber getAsJsonNumber() {
    Log.error("0xA0609 "+ this + " is not a Json Number!");
    return null;
  }
  
  /**
   * @return this object as JsonNull if it is of this type and throws an error otherwise.
   */
  default public JsonNull getAsJsonNull() {
    Log.error("0xA0610 "+ this + " is not a Json Null!");
    return null;
  }
  
}
