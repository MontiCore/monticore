/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.serialization;

import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static de.monticore.symboltable.serialization.JsonTokenKind.*;

public class JsonLexer {

  protected static JsonLexer instance = null;

  public static List<JsonToken> read(String json){
    return read(json, true);
  }

  public static List<JsonToken> read(String json, boolean filterWhiteSpaces) {
    if (null == instance) {
      instance = new JsonLexer();
    }
    List<JsonToken> tokens = instance.readJson(json);
    if (filterWhiteSpaces) {
      return tokens
          .stream()
          .filter(t -> t.getKind() != WHITESPACE)
          .collect(Collectors.toList());
    }
    return tokens;
  }

  public static void setInstance(JsonLexer instance) {
    JsonLexer.instance = instance;
  }

  public List<JsonToken> readJson(String json) {
    //find longest match
    List<JsonToken> result = new ArrayList<>();

    String current = json; //in this string, iteratively cut off the matched front
    while (!current.isEmpty()) {
      String[] matches = getMatches(current); //longest match for each token kind
      int indexOfLongestMatch = findLongestMatchIndex(matches); //longest match of all token kinds
      if (matches[indexOfLongestMatch].isEmpty()) {
        Log.error("0xTODO No match found during lexing of: '" + current + "'");
        return new ArrayList<>();
      }
      result.add(createToken(matches, indexOfLongestMatch));
      current = current.substring(matches[indexOfLongestMatch].length());
    }
    return result;
  }

  /**
   * String regex = ""
   * + "\""                   //start with '"'
   * + "("                    //begin iteration of characters
   * + "[^\\\"\\\\]"          //every character except '"' or "\"
   * + "|\\\\u[0-9A-Fa-f]{4}" //or unicode escape sequence
   * + "|\\\\[bfnrt\"/\\\\]"  // or other escape sequence
   * + ")*"                   //end iteration of characters
   * + "\"";                  //end with '"'
   *
   * @param json
   * @return
   */
  protected String matchString(String json) {

    return matchRegexPrefix(json, "\"([^\\\"\\\\]|\\\\u[0-9A-Fa-f]{4}|\\\\[bfnrt\"/\\\\])*\"");
  }

  protected String matchNumber(String json) {
    return matchRegexPrefix(json, "-?(0|[1-9]([0-9]))(.([0-9])+)?((e|E)(-|\\+)?[0-9]+)?");
  }

  protected String matchBoolean(String json) {
    return matchRegexPrefix(json, "true|false");
  }

  protected String matchBeginArray(String json) {
    return matchRegexPrefix(json, "\\[");
  }

  protected String matchEndArray(String json) {
    return matchRegexPrefix(json, "\\]");
  }

  protected String matchBeginObject(String json) {
    return matchRegexPrefix(json, "\\{");
  }

  protected String matchEndObject(String json) {
    return matchRegexPrefix(json, "\\}");
  }

  protected String matchNull(String json) {
    return matchRegexPrefix(json, "null");
  }

  protected String matchComma(String json) {
    return matchRegexPrefix(json, ",");
  }

  protected String matchColon(String json) {
    return matchRegexPrefix(json, ":");
  }

  protected String matchWhitespace(String json) {
    return matchRegexPrefix(json, "(\\s)*");
  }

  protected String[] getMatches(String current) {
    String[] matches = new String[values().length];
    matches[STRING.ordinal()] = matchString(current);
    matches[NUMBER.ordinal()] = matchNumber(current);
    matches[BOOLEAN.ordinal()] = matchBoolean(current);
    matches[BEGIN_ARRAY.ordinal()] = matchBeginArray(current);
    matches[END_ARRAY.ordinal()] = matchEndArray(current);
    matches[BEGIN_OBJECT.ordinal()] = matchBeginObject(current);
    matches[END_OBJECT.ordinal()] = matchEndObject(current);
    matches[COMMA.ordinal()] = matchComma(current);
    matches[COLON.ordinal()] = matchColon(current);
    matches[NULL.ordinal()] = matchNull(current);
    matches[WHITESPACE.ordinal()] = matchWhitespace(current);
    return matches;
  }

  protected int findLongestMatchIndex(String[] matches) {
    int indexOfLongestMatch = 0;
    for (int i = 0; i < matches.length; i++) {
      if (matches[i].length() > matches[indexOfLongestMatch].length()) {
        indexOfLongestMatch = i;
      }
    }
    //TODO check and warn ambiguous lexing here?
    return indexOfLongestMatch;
  }

  protected JsonToken createToken(String[] matches, int indexOfLongestMatch) {
    if (indexOfLongestMatch == STRING.ordinal()) {
      return new JsonToken(STRING, matches[indexOfLongestMatch]);
    }
    if (indexOfLongestMatch == NUMBER.ordinal()) {
      return new JsonToken(NUMBER, matches[indexOfLongestMatch]);
    }
    if (indexOfLongestMatch == BOOLEAN.ordinal()) {
      return new JsonToken(BOOLEAN, matches[indexOfLongestMatch]);
    }
    if (indexOfLongestMatch == BEGIN_ARRAY.ordinal()) {
      return new JsonToken(BEGIN_ARRAY);
    }
    if (indexOfLongestMatch == END_ARRAY.ordinal()) {
      return new JsonToken(END_ARRAY);
    }
    if (indexOfLongestMatch == BEGIN_OBJECT.ordinal()) {
      return new JsonToken(BEGIN_OBJECT);
    }
    if (indexOfLongestMatch == END_OBJECT.ordinal()) {
      return new JsonToken(END_OBJECT);
    }
    if (indexOfLongestMatch == COMMA.ordinal()) {
      return new JsonToken(COMMA);
    }
    if (indexOfLongestMatch == COLON.ordinal()) {
      return new JsonToken(COLON);
    }
    if (indexOfLongestMatch == NULL.ordinal()) {
      return new JsonToken(NULL);
    }
    if (indexOfLongestMatch == WHITESPACE.ordinal()) {
      return new JsonToken(WHITESPACE);
    }
    else {
      Log.error("0xTODO Unknown kind of token in '" + matches[indexOfLongestMatch] + "'");
      return null;
    }
  }

  /**
   * Returns the longest match for the passed regex in the passed input that is a prefix of the input.
   * Returns an empty String if not match is possible at all.
   *
   * @param input
   * @param regex
   * @return
   */
  protected String matchRegexPrefix(String input, String regex) {
    Matcher matcher = Pattern.compile(regex).matcher(input);
    if (matcher.find()) {
      if (0 == matcher.start()) {
        return matcher.group();
      }
    }
    return "";
  }

}
