/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serialization;

/**
 * Enumeration of all token kinds that occur in the JSON language. Tokens of some kinds (such as
 * a boolean that can be either 'true' or 'false') have a value,
 * while other kinds have a static value (such as begin_object,
 * which is always '{') that is not represented inside of the token.
 */
public enum JsonTokenKind {
  STRING,
  NUMBER,
  BOOLEAN,
  BEGIN_ARRAY,
  END_ARRAY,
  BEGIN_OBJECT,
  END_OBJECT,
  NULL,
  COMMA,
  COLON,
  WHITESPACE;

  /**
   * returns true, iff the token kind has a (variable) value.
   * @return
   */
  public boolean hasValue() {
    switch (this) {
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
}
