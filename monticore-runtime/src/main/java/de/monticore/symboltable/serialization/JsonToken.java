/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serialization;

import de.se_rwth.commons.logging.Log;

/**
 * This class holds kind and, if present for this kind, also the value of a json token.
 * For tokens without a value, this class has constants that can be used to avoid creating unnecassary objects
 */
public class JsonToken {

  public static final JsonToken BEGIN_OBJECT = new JsonToken(JsonTokenKind.BEGIN_OBJECT);

  public static final JsonToken END_OBJECT = new JsonToken(JsonTokenKind.END_OBJECT);

  public static final JsonToken BEGIN_ARRAY = new JsonToken(JsonTokenKind.BEGIN_ARRAY);

  public static final JsonToken END_ARRAY = new JsonToken(JsonTokenKind.END_ARRAY);

  public static final JsonToken COLON = new JsonToken(JsonTokenKind.COLON);

  public static final JsonToken COMMA = new JsonToken(JsonTokenKind.COMMA);

  public static final JsonToken NULL = new JsonToken(JsonTokenKind.NULL);

  // for performance reasons, forget the exact whitespace characters after lexing
  public static final JsonToken WHITESPACE = new JsonToken(JsonTokenKind.WHITESPACE);

  public static final JsonToken BOOLEAN_TRUE = new JsonToken(JsonTokenKind.BOOLEAN, "true");

  public static final JsonToken BOOLEAN_FALSE = new JsonToken(JsonTokenKind.BOOLEAN, "false");

  protected JsonTokenKind kind;

  protected String value;

  /**
   * create a token with the passed kind and the passed value
   * @param kind
   * @param value
   */
  public JsonToken(JsonTokenKind kind, String value) {
    this.kind = kind;
    if (kind.hasValue()) {
      this.value = value;
    }
  }

  /**
   * returns a token with the passed kind
   * @param kind
   */
  public JsonToken(JsonTokenKind kind) {
    this.kind = kind;
  }

  /**
   * returns the kind of the token
   * @return
   */
  public JsonTokenKind getKind() {
    return kind;
  }

  /**
   * returns the value of the token if the token kind has a value. Throws an error otherwise
   * @return
   */
  public String getValue() {
    if (kind.hasValue()) {
      return this.value;
    }
    Log.error("0xA0596 Token " + kind + " cannot have a value!");
    return "";
  }

  @Override public String toString() {
    if (kind.hasValue()) {
      return "{" + kind.toString() + "=" + value + "}";
    }
    return "{" + kind.toString() + "}";
  }

}
