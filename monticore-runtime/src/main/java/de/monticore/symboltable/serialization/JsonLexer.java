/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serialization;

import de.se_rwth.commons.logging.Log;

import static de.monticore.symboltable.serialization.JsonToken.*;
import static de.monticore.symboltable.serialization.JsonTokenKind.NUMBER;
import static de.monticore.symboltable.serialization.JsonTokenKind.STRING;

/**
 * This class is responsible to tokenize a String that encoded as JSON. The length of the input,
 * thus, is limited by the maximum length of a String.
 */
public class JsonLexer {

  protected String json;

  //current position within the input string
  protected int pos = 0;

  //currently peeked token, or null is not token has been peeked
  protected JsonToken peeked;

  protected NumberParser numbers;

  /**
   * The lexer tokenizes the passed input String.
   *
   * @param input
   */
  public JsonLexer(String input) {
    json = input;
    peeked = null;
    numbers = new NumberParser();
  }

  /**
   * returns truee, iff the end of the String lexed so far is not reached.
   *
   * @return
   */
  public boolean hasNext() {
    return peek() != null;
  }

  /**
   * Returns the part of the input that has not been lexed yet (e.g., for prin
   *
   * @return
   */
  public String getRemainder() {
    return json;
  }

  /**
   * reads the next token of the input, without consuming it. In Json, each kind of token can be
   * identified by its first character.
   *
   * @return
   */
  public JsonToken peek() {
    if (null != peeked) {
      return peeked;
    }
    if (pos >= json.length()) {
      return null;
    }
    char first = json.charAt(pos);
    if (first == '[') {
      pos++;
      peeked = BEGIN_ARRAY;
      return BEGIN_ARRAY;
    }
    if (first == ']') {
      pos++;
      peeked = END_ARRAY;
      return END_ARRAY;
    }
    if (first == '{') {
      pos++;
      peeked = BEGIN_OBJECT;
      return BEGIN_OBJECT;
    }
    if (first == '}') {
      pos++;
      peeked = END_OBJECT;
      return END_OBJECT;
    }
    if (first == ':') {
      pos++;
      peeked = COLON;
      return COLON;
    }
    if (first == ',') {
      pos++;
      peeked = COMMA;
      return COMMA;
    }
    if (first == 't' && la(1) == 'r' && la(2) == 'u' && la(3) == 'e') {
      pos += 4;
      peeked = BOOLEAN_TRUE;
      return BOOLEAN_TRUE;
    }
    if (first == 'f' && la(1) == 'a' && la(2) == 'l' && la(3) == 's'
        && la(4) == 'e') {
      pos += 5;
      peeked = BOOLEAN_FALSE;
      return BOOLEAN_FALSE;
    }
    if (first == 'n' && la(1) == 'u' && la(2) == 'l' && la(3) == 'l') {
      pos += 4;
      peeked = NULL;
      return NULL;
    }
    if (first == '"') {
      return checkString();
    }
    if (WHITESPACE_CHARACTERS.indexOf(first) > -1) {
      while (pos < json.length() && WHITESPACE_CHARACTERS.indexOf(json.charAt(pos)) > -1) {
        pos++;
      }
      peeked = WHITESPACE;
      return WHITESPACE;
    }
    if ("-0123456789".indexOf(first) > -1) {
      return checkNumber();
    }
    peeked = null;
    Log.error("0xA0591 Unexpected end during lexing!");
    return null;
  }

  /**
   * Returns the character at the i-th position from the current position of the input.
   *
   * @param i
   * @return
   */
  protected char la(int i) {
    return json.charAt(pos + i);
  }

  /**
   * returns the next token and removes it
   *
   * @return
   */
  public JsonToken poll() {
    JsonToken current = peek();
    if (null == current) {
      Log.error("0xA0592 unexpected end of model during lexing!");
      return null;
    }
    else {
      peeked = null;
      return current;
    }
  }

  /**
   * try to read a number starting at the current location
   *
   * @return
   */
  protected JsonToken checkNumber() {
    numbers.reset();
    while (pos < json.length()
        && "0123456789eE-+.".indexOf(json.charAt(pos)) > -1
        && !numbers.hasError()) {
      numbers.step(json.charAt(pos));
      pos++;
    }
    if (numbers.isInFinalState()) {
      peeked = new JsonToken(NUMBER, numbers.getResult());
    }
    else {
      Log.error("0xA0593 Invalid number!");
    }
    return peeked;
  }

  /**
   * checks, if the current input is a valid JSON String, i.e. if it is conform to the regex:
   * String regex = ""
   * + "\""                   //start with '"'
   * + "("                    //begin iteration of characters
   * + "[^\\\"\\\\]"          //every character except '"' or "\"
   * + "|\\\\u[0-9A-Fa-f]{4}" //or unicode escape sequence
   * + "|\\\\[bfnrt\"/\\\\]"  // or other escape sequence
   * + ")*"                   //end iteration of characters
   * + "\"";                  //end with '"'
   *
   * @return
   */
  protected JsonToken checkString() {
    int beginPos = pos;
    StringBuilder result = new StringBuilder();
    pos++; //skip first quotes
    while (pos < json.length()) {
      char c = json.charAt(pos);
      //check if valid escape sequence
      if (c == '\\') {
        result.append('\\');
        pos++;
        c = json.charAt(pos);
        result.append(c);
        // if backslash and u occurs, 4 digits must follow
        if (c == 'u') {
          if ((DIGITS.indexOf(la(1)) != -1 && DIGITS.indexOf(la(2)) != -1
              && DIGITS.indexOf(la(3)) != -1 && DIGITS.indexOf(la(4)) != -1)) {
            result.append(la(1));
            result.append(la(2));
            result.append(la(3));
            result.append(la(4));
          }
          else {
            Log.error(
                "0xA0594 Invalid escape sequence in String during lexing! 'u' must be followed by four digits");
          }
          pos = pos + 5;
        }
        // else, check whether other allowed escaped character
        else if ("\\\"bfnrt".indexOf(c) != -1) {
          pos++;
        }
        else {
          Log.error("0xA0595 Invalid escape sequence in String during lexing! An escaped '" + c
              + "' is not allowed");
        }
      }
      //else these are unescaped quotes, the string ends here and is valid
      else if (c == '\"') {
        pos++;
        peeked = new JsonToken(STRING, result.toString());
        return peeked;
      }
      else {
        //random valid character
        result.append(c);
        pos++;
      }
    }
    return null;
  }

  protected static final String WHITESPACE_CHARACTERS = " \t\n\f\r";

  protected static final String DIGITS = "0123456789";

}
