/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import de.monticore.symboltable.serialization.json.*;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.StringReader;

/**
 * Parses serialized JSON Strings into an intermediate JSON data structure. This data structure can
 * then be used, e.g., to build Java objects with their builders.
 */
public class JsonParser {
  
  public static JsonElement parseJson(String s) {
    JsonReader reader = new JsonReader(new StringReader(s));
    return parseJson(reader);
  }
  
  public static JsonObject parseJsonObject(String s) {
    JsonReader reader = new JsonReader(new StringReader(s));
    return parseJsonObject(reader);
  }
  
  public static JsonArray parseJsonArray(String s) {
    JsonReader reader = new JsonReader(new StringReader(s));
    return parseJsonArray(reader);
  }
  
  @Deprecated
  public static JsonElement deserializeJson(String s) {
    return parseJson(s);
  }
  
  @Deprecated
  public static JsonObject deserializeJsonObject(String s) {
    return parseJsonObject(s);
  }
  
  @Deprecated
  public static JsonArray deserializeJsonArray(String s) {
    return parseJsonArray(s);
  }
  
  protected static JsonElement parseJson(JsonReader reader) {
    try {
      while (reader.hasNext()) {
        JsonToken token = reader.peek();
        switch (token) {
          case BEGIN_ARRAY:
            return parseJsonArray(reader);
          case BEGIN_OBJECT:
            return parseJsonObject(reader);
          case BOOLEAN:
            return new JsonBoolean(reader.nextBoolean());
          case END_DOCUMENT:
          case NULL:
            reader.nextNull();
            return new JsonNull();
          case NUMBER:
            return new JsonNumber(reader.nextString());
          case STRING:
            return new JsonString(reader.nextString());
          case END_ARRAY:
          case END_OBJECT:
          case NAME:
          default:
            Log.error(
                "Invalid JSON token \"" + token + "\". The serialized object is not well-formed!");
        }
      }
      // reader.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  protected static JsonObject parseJsonObject(JsonReader reader) {
    JsonObject result = new JsonObject();
    try {
      reader.beginObject();
      while (reader.hasNext()) {
        JsonToken token = reader.peek();
        switch (token) {
          case NAME:
            String name = reader.nextName();
            JsonElement value = parseJson(reader);
            result.put(name, value);
            break;
          default:
            Log.error(
                "Invalid JSON token \"" + token + "\". The serialized object is not well-formed!");
        }
      }
      reader.endObject();
      // reader.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }
  
  protected static JsonArray parseJsonArray(JsonReader reader) {
    JsonArray result = new JsonArray();
    try {
      reader.beginArray();
      while (reader.hasNext()) {
        JsonToken token = reader.peek();
        switch (token) {
          case BEGIN_ARRAY:
            JsonArray array = parseJsonArray(reader);
            result.add(array);
            break;
          case BEGIN_OBJECT:
            JsonObject object = parseJsonObject(reader);
            result.add(object);
            break;
          case BOOLEAN:
            JsonBoolean bool = new JsonBoolean(reader.nextBoolean());
            result.add(bool);
            break;
          case NUMBER:
            JsonNumber number = new JsonNumber(reader.nextString());
            result.add(number);
            break;
          case STRING:
            JsonString string = new JsonString(reader.nextString());
            result.add(string);
            break;
          case NULL:
            reader.nextNull();
            result.add(new JsonNull());
            break;
          case END_DOCUMENT:
          case NAME:
          default:
            Log.error(
                "Invalid JSON token \"" + token + "\". The serialized object is not well-formed!");
        }
      }
      reader.endArray();
      // reader.close();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }
  
}
