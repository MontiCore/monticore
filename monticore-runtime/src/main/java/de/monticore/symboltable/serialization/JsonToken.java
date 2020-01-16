/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serialization;

import de.se_rwth.commons.logging.Log;

import static de.monticore.symboltable.serialization.JsonTokenKind.STRING;

/**
 * This class holds kind and, if present for this kind, also the value of a json token.
 */
public class JsonToken {

  protected JsonTokenKind kind;

  protected String value;

  public JsonToken(JsonTokenKind kind, String value) {
    this.kind = kind;
    this.value = value;
  }

  public JsonToken(JsonTokenKind kind) {
    this.kind = kind;
  }

  public JsonTokenKind getKind() {
    return kind;
  }

  public String getValue() {
    if (hasValue()) {
      if (STRING == getKind()) {
        return this.value.substring(1, this.value.length() - 1);
      }
      return this.value;
    }
    Log.error("0xTODO Token " + kind + " cannot have a value!");
    return "";
  }

  public boolean hasValue() {
    switch (kind) {
      case STRING:
      case NUMBER:
      case BOOLEAN:
        return true;
      case BEGIN_ARRAY:
      case END_ARRAY:
      case BEGIN_OBJECT:
      case END_OBJECT:
      case NULL:
      case COMMA:
      case COLON:
      case WHITESPACE:
      default:
        return false;
    }
  }

  @Override public String toString() {
    if (hasValue()) {
      return "{" + kind.toString() + "=" + value + "}";
    }
    return "{" + kind.toString() + "}";
  }
}
