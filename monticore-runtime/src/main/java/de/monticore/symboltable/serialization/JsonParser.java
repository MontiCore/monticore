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

  /**
   * Parses a given String encoded in JSON to a {@link JsonElement}. This method should be used if
   * the Json type (i.e., Object, Array,...) of the encoded JSON is unclear.
   *
   * @param s
   * @return
   */
  public static JsonElement parse(String s) {
    JsonReader reader = new JsonReader(new StringReader(s));
    return parseJson(reader);
  }

  /**
   * Parses a given String encoded in JSON to a {@link JsonObject}. Parsing of the String fails with
   * an error if the encoded String cannot be parsed into a Json object.
   *
   * @param s
   * @return
   */
  public static JsonObject parseJsonObject(String s) {
    JsonReader reader = new JsonReader(new StringReader(s));
    return parseJsonObject(reader);
  }

  /**
   * Parses a given String encoded in JSON to a {@link JsonArray}. Parsing of the String fails with
   * an error if the encoded String cannot be parsed into a Json array.
   *
   * @param s
   * @return
   */
  public static JsonArray parseJsonArray(String s) {
    JsonReader reader = new JsonReader(new StringReader(s));
    return parseJsonArray(reader);
  }

  /**
   * Parses any JsonElement with the passed JsonReader
   * @param reader
   * @return
   */
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
            return JsonElementFactory.createJsonBoolean(reader.nextBoolean());
          case END_DOCUMENT:
          case NULL:
            reader.nextNull();
            return JsonElementFactory.createJsonNull();
          case NUMBER:
            return JsonElementFactory.createJsonNumber(reader.nextString());
          case STRING:
            return JsonElementFactory.createJsonString(reader.nextString());
          case END_ARRAY:
          case END_OBJECT:
          case NAME:
          default:
            Log.error(
                "0xTODO Invalid JSON token \"" + token + "\". The serialized object is not well-formed!");
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * Parses a Json Object with the passed JsonReader
   * @param reader
   * @return
   */
  protected static JsonObject parseJsonObject(JsonReader reader) {
    JsonObject result = JsonElementFactory.createJsonObject();
    try {
      reader.beginObject();
      while (reader.hasNext()) {
        JsonToken token = reader.peek();
        switch (token) {
          case NAME:
            String name = reader.nextName();
            JsonElement value = parseJson(reader);
            result.putMember(name, value);
            break;
          case BEGIN_ARRAY:
          case BEGIN_OBJECT:
          case BOOLEAN:
          case END_DOCUMENT:
          case NULL:
          case NUMBER:
          case STRING:
          case END_ARRAY:
          case END_OBJECT:
          default:
            Log.error(
                " 0xTODO Invalid JSON token \"" + token + "\". The serialized object is not well-formed!");
        }
      }
      reader.endObject();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * Parses a Json array by using the passed JsonReader
   * @param reader
   * @return
   */
  protected static JsonArray parseJsonArray(JsonReader reader) {
    JsonArray result = JsonElementFactory.createJsonArray();
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
            JsonBoolean bool = JsonElementFactory.createJsonBoolean(reader.nextBoolean());
            result.add(bool);
            break;
          case NUMBER:
            JsonNumber number = JsonElementFactory.createJsonNumber(reader.nextString());
            result.add(number);
            break;
          case STRING:
            JsonString string = JsonElementFactory.createJsonString(reader.nextString());
            result.add(string);
            break;
          case NULL:
            reader.nextNull();
            result.add(JsonElementFactory.createJsonNull());
            break;
          case END_DOCUMENT:
          case NAME:
          default:
            Log.error(
                "0xTODO Invalid JSON token \"" + token + "\". The serialized object is not well-formed!");
        }
      }
      reader.endArray();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * If object member tracing is enabled, getter-methods of Json objects are tracked to identify,
   * if any members have been forgotten during deserialization
   */
  public static void enableObjectMemberTracing() {
    JsonElementFactory.setInstance(new JsonElementFactory() {
      @Override
      protected JsonObject doCreateJsonObject() {
        return new TraceableJsonObject();
      }
    });
  }

  /**
   * Disables object member tracing.
   * @see JsonParser#enableObjectMemberTracing()
   */
  public static void disableObjectMemberTracing() {
    JsonElementFactory.setInstance(new JsonElementFactory());
  }

  static {
    //by default, enableObjectMemberTracing
    enableObjectMemberTracing();
  }

}
