/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import org.junit.Test;

import java.util.List;

import static de.monticore.symboltable.serialization.JsonTokenKind.*;
import static org.junit.Assert.*;

public class JsonLexerTest {

  @Test
  public void testSimpleJsonConstructs() {
    List<JsonToken> tokens;
    tokens = JsonLexer.read("\"foo\":\"bar\"");
    assertEquals(3, tokens.size());
    assertEquals(STRING, tokens.get(0).getKind());
    assertEquals("foo", tokens.get(0).getValue());
    assertEquals(COLON, tokens.get(1).getKind());
    assertEquals(STRING, tokens.get(2).getKind());
    assertEquals("bar", tokens.get(2).getValue());

    tokens = JsonLexer.read("\"foo\":true");

    tokens = JsonLexer.read("\"foo\":-0.2");

    tokens = JsonLexer.read("\"foo\":null");
  }

  @Test
  public void testSingleTokens() {
    checkSingleNonValueToken(":", COLON);
    checkSingleNonValueToken(",", COMMA);
    checkSingleNonValueToken("null", NULL);
    checkSingleNonValueToken("[", BEGIN_ARRAY);
    checkSingleNonValueToken("]", END_ARRAY);
    checkSingleNonValueToken("{", BEGIN_OBJECT);
    checkSingleNonValueToken("}", END_OBJECT);
    checkSingleNonValueToken(" ", WHITESPACE);
    checkSingleNonValueToken(" \t\n ", WHITESPACE);

    checkSingleValueToken("true", BOOLEAN);
    checkSingleValueToken("false", BOOLEAN);
    checkSingleValueToken("0", NUMBER);
    checkSingleValueToken("10.23", NUMBER);
    checkSingleValueToken("-10.2", NUMBER);
    checkSingleValueToken("-10e5", NUMBER);
    checkSingleValueToken("10E50", NUMBER);
    checkSingleValueToken("-10e-5", NUMBER);
    checkSingleValueToken("10E-50", NUMBER);
    checkSingleValueToken("-10e+5", NUMBER);
    checkSingleValueToken("10E+50", NUMBER);
    checkSingleValueToken("-10.2e+5", NUMBER);
    checkSingleValueToken("\"foo\"", STRING);
    checkSingleValueToken("\"true\"", STRING);
    checkSingleValueToken("\"foo foo \"", STRING);
    checkSingleValueToken("\"\\u1234\"", STRING);
    checkSingleValueToken("\"\\u12345\"", STRING);
    checkSingleValueToken("\"\\b\"", STRING);
    checkSingleValueToken("\"\\f\"", STRING);
    checkSingleValueToken("\"\\n\"", STRING);
    checkSingleValueToken("\"\\r\"", STRING);
    checkSingleValueToken("\"\\t\"", STRING);
    checkSingleValueToken("\"\\\"\"", STRING);
    checkSingleValueToken("\"foo \\b\\f\\r\\n\\t\\\" foo \"", STRING);
  }

  protected void checkSingleNonValueToken(String json, JsonTokenKind expectedTokenKind) {
    List<JsonToken> tokens = JsonLexer.read(json);
    assertEquals(1, tokens.size());
    assertEquals(expectedTokenKind, tokens.get(0).getKind());
    assertFalse(tokens.get(0).hasValue());
  }

  protected void checkSingleValueToken(String json, JsonTokenKind expectedTokenKind) {
    List<JsonToken> tokens = JsonLexer.read(json);
    assertEquals(1, tokens.size());
    assertEquals(expectedTokenKind, tokens.get(0).getKind());
    assertTrue(tokens.get(0).hasValue());
    assertEquals(json, tokens.get(0).getValue());
  }

}
