/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serialization;

import java.io.IOException;
import java.io.StringReader;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import de.monticore.symboltable.serialization.json.JsonArray;
import de.monticore.symboltable.serialization.json.JsonBoolean;
import de.monticore.symboltable.serialization.json.JsonElement;
import de.monticore.symboltable.serialization.json.JsonNull;
import de.monticore.symboltable.serialization.json.JsonNumber;
import de.monticore.symboltable.serialization.json.JsonObject;
import de.monticore.symboltable.serialization.json.JsonString;
import de.se_rwth.commons.logging.Log;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class JsonParser {
  
  public static JsonElement deserializeJson(String s) {
    JsonReader reader = new JsonReader(new StringReader(s));
    return deserializeJson(reader);
  }
  
  public static JsonObject deserializeJsonObject(String s) {
    JsonReader reader = new JsonReader(new StringReader(s));
    return deserializeJsonObject(reader);
  }
  
  public static JsonArray deserializeJsonArray(String s) {
    JsonReader reader = new JsonReader(new StringReader(s));
    return deserializeJsonArray(reader);
  }
  
  protected static JsonElement deserializeJson(JsonReader reader) {
    try {
      while (reader.hasNext()) {
        JsonToken token = reader.peek();
        switch (token) {
          case BEGIN_ARRAY:
            return deserializeJsonArray(reader);
          case BEGIN_OBJECT:
            return deserializeJsonObject(reader);
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
  
  protected static JsonObject deserializeJsonObject(JsonReader reader) {
    JsonObject result = new JsonObject();
    try {
      reader.beginObject();
      while (reader.hasNext()) {
        JsonToken token = reader.peek();
        switch (token) {
          case NAME:
            String name = reader.nextName();
            JsonElement value = deserializeJson(reader);
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
  
  protected static JsonArray deserializeJsonArray(JsonReader reader) {
    JsonArray result = new JsonArray();
    try {
      reader.beginArray();
      while (reader.hasNext()) {
        JsonToken token = reader.peek();
        switch (token) {
          case BEGIN_ARRAY:
            JsonArray array = deserializeJsonArray(reader);
            result.add(array);
            break;
          case BEGIN_OBJECT:
            JsonObject object = deserializeJsonObject(reader);
            result.add(object);
            break;
          case BOOLEAN:
            JsonBoolean bool = new JsonBoolean(reader.nextBoolean());
            result.add(bool);
            break;
          case NUMBER:
            JsonNumber number = new JsonNumber(reader.nextDouble() + "");
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
