/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import de.monticore.symboltable.serialization.json.*;
import de.se_rwth.commons.logging.Log;

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
    return parseJson(new JsonLexer(s));
  }

  /**
   * Parses a given String encoded in JSON to a {@link JsonObject}. Parsing of the String fails with
   * an error if the encoded String cannot be parsed into a Json object.
   *
   * @param json
   * @return
   */
  public static JsonObject parseJsonObject(String json) {
    JsonLexer lexer = new JsonLexer(json);
    JsonToken t = lexer.poll();
    if (null == t || JsonTokenKind.BEGIN_OBJECT != t.getKind()) {
      Log.error("0xA0589 Json objects must begin with a '{'!");
      return null;
    }
    return parseJsonObject(lexer);
  }

  /**
   * Parses a given String encoded in JSON to a {@link JsonArray}. Parsing of the String fails with
   * an error if the encoded String cannot be parsed into a Json array.
   *
   * @param json
   * @return
   */
  public static JsonArray parseJsonArray(String json) {
    JsonLexer lexer = new JsonLexer(json);
    JsonToken t = lexer.poll();
    if (null == t || JsonTokenKind.BEGIN_ARRAY != t.getKind()) {
      Log.error("0xA0590 Json arrays must begin with a '['!");
      return null;
    }
    return parseJsonArray(lexer);
  }

  /**
   * parses any json using the passed lexer.
   *
   * @param lexer
   * @return
   */
  public static JsonElement parseJson(JsonLexer lexer) {
    while (lexer.hasNext()) {
      JsonToken t = lexer.poll();
      switch (t.getKind()) {
        case WHITESPACE: //ignore whitespaces
          break;
        case BEGIN_ARRAY:
          return parseJsonArray(lexer);
        case BEGIN_OBJECT:
          return parseJsonObject(lexer);
        case BOOLEAN:
          return JsonElementFactory.createJsonBoolean(Boolean.parseBoolean(t.getValue()));
        case NULL:
          return JsonElementFactory.createJsonNull();
        case NUMBER:
          return JsonElementFactory.createJsonNumber(t.getValue());
        case STRING:
          return JsonElementFactory.createJsonString(t.getValue());
        case END_ARRAY:
        case END_OBJECT:
        case COLON:
        case COMMA:
        default:
          Log.error(
              "0xA0564 Invalid JSON token \"" + t
                  + "\". The serialized object is not well-formed! Discarding :" + lexer
                  .getRemainder());
          return null;
      }
    }
    //this only occurs if string is only whitespace
    return null;
  }

  /**
   * Parses a Json Object with the passed lexer
   *
   * @param lexer
   * @return
   */
  protected static JsonObject parseJsonObject(JsonLexer lexer) {
    JsonObject result = JsonElementFactory.createJsonObject();
    while (lexer.hasNext()) {
      JsonToken t = lexer.poll();
      switch (t.getKind()) {
        case WHITESPACE: //ignore whitespaces
          break;
        case STRING: //look for key:value pairs
          String memberName = t.getValue(); //first get the key
          JsonToken colon = pollNextNonWhiteSpace(lexer); //that *must* be followed by colon
          if (null == colon || JsonTokenKind.COLON != colon.getKind()) {
            Log.error(
                " 0xA0566 Invalid JSON token \"" + t
                    + "\". The serialized object is not well-formed! A member name must be followed by a colon");
          }
          JsonElement memberValue = parseJson(lexer);  //then parse any value
          result.putMember(memberName, memberValue); // and add the member to result
          JsonToken next = pollNextNonWhiteSpace(
              lexer); //either object end is reached or a comma must follow
          if (null == next) {
            Log.error(
                " 0xA0580 Invalid JSON structure. Unexpected end of object!");
          }
          if (JsonTokenKind.END_OBJECT == next.getKind()) {
            return result;
          }
          if (JsonTokenKind.COMMA != next.getKind()) {
            Log.error(" 0xA0581 Invalid JSON structure. Missing comma in object!");
          }
          JsonToken next2 = peekNextNonWhiteSpace(
              lexer); //peek if next token is end of object after comma.
          if (null == next2 || JsonTokenKind.END_OBJECT == next2.getKind()) {
            Log.error(
                " 0xA0582 Invalid JSON structure. Unexpected end of object after comma!");
          }
          break;
        case END_OBJECT:
          return result; //only if empty object
        case COMMA:
        case BEGIN_ARRAY:
        case BEGIN_OBJECT:
        case BOOLEAN:
        case NULL:
        case NUMBER:
        case COLON:
        case END_ARRAY:
        default:
          Log.error(
              " 0xA0583 Invalid JSON structure \"" + t
                  + "\". The serialized object is not well-formed! Ignoring remainder: " + lexer
                  .getRemainder());
          return null;
      }
    }
    Log.error(
        " 0xA0584 Invalid JSON structure. Unexpected end of object!");
    return null;
  }

  /**
   * Parses a Json array by using the passed lexer
   *
   * @param lexer
   * @return
   */
  protected static JsonArray parseJsonArray(JsonLexer lexer) {
    JsonArray result = JsonElementFactory.createJsonArray();
    while (lexer.hasNext()) {
      JsonToken t = lexer.peek();
      switch (t.getKind()) {
        case WHITESPACE: //ignore whitespaces
          lexer.poll();
          break;
        case BEGIN_ARRAY:
        case BEGIN_OBJECT:
        case BOOLEAN:
        case NUMBER:
        case STRING:
        case NULL:
          JsonElement e = parseJson(lexer);
          result.add(e);
          JsonToken next = pollNextNonWhiteSpace(lexer);
          if (null == next) {
            Log.error(
                " 0xA0585 Invalid JSON structure. Unexpected end of array!");
          }
          if (JsonTokenKind.END_ARRAY == next.getKind()) {
            return result;
          }
          if (JsonTokenKind.COMMA != next.getKind()) {
            Log.error(
                " 0xA0586 Invalid JSON token. Missing comma in array!");
          }
          //else it is a comma that has been consumed already
          JsonToken next2 = peekNextNonWhiteSpace(
              lexer); //peek if next token is end of array after comma.
          if (null == next2 || JsonTokenKind.END_ARRAY == next2.getKind()) {
            Log.error(
                " 0xA0587 Invalid JSON token. Unexpected end of array after comma!");
          }
          break;
        case END_ARRAY:
          lexer.poll();
          return result;
        case COMMA:
        case COLON:
        case END_OBJECT:
        default:
          Log.error(
              "0xA0568 Invalid JSON token \"" + t
                  + "\". The serialized array is not well-formed!");
          return null;
      }
    }
    Log.error(
        " 0xA0588 Invalid JSON token. Unexpected end of array!");
    return null;
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
   *
   * @see JsonParser#enableObjectMemberTracing()
   */
  public static void disableObjectMemberTracing() {
    JsonElementFactory.setInstance(new JsonElementFactory());
  }

  static {
    //by default, enableObjectMemberTracing
    enableObjectMemberTracing();
  }

  /**
   * reads all whitespace tokens until the next non-whitespace token.
   * This is returned, but not consumed. If the method is called when the lexer has already reached
   * the end of the document, it returns "null". If the end of the document occurs during
   * consumption of whitespaces, a whitespace token is returned.
   *
   * @param lexer
   * @return
   */
  protected static JsonToken peekNextNonWhiteSpace(JsonLexer lexer) {
    if (!lexer.hasNext()) {
      return null;
    }
    JsonToken t = lexer.peek();
    while (lexer.hasNext() && lexer.peek().getKind() == JsonTokenKind.WHITESPACE) {
      t = lexer.poll();
    }
    return t;
  }

  /**
   * reads all whitespace tokens until the next non-whitespace token.
   * This is returned and consumed. If the method is called when the lexer has already reached the
   * end of the document, it returns "null". If the end of the document occurs during consumption
   * of whitespaces, a whitespace token is returned.
   *
   * @param lexer
   * @return
   */
  protected static JsonToken pollNextNonWhiteSpace(JsonLexer lexer) {
    if (!lexer.hasNext()) {
      return null;
    }
    JsonToken t = lexer.poll();
    while (lexer.hasNext() && t.getKind() == JsonTokenKind.WHITESPACE) {
      t = lexer.poll();
    }
    return t;
  }

}
