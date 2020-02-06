/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import org.junit.Test;

import static de.monticore.symboltable.serialization.JsonTokenKind.*;
import static org.junit.Assert.assertEquals;

public class JsonLexerTest {

  @Test
  public void testNumberWhitespaces() {
    JsonLexer lexer = new JsonLexer("12.5 3e9");
    assertEquals("12.5", lexer.poll().getValue());
    assertEquals(WHITESPACE, lexer.poll().getKind());
    assertEquals("3e9", lexer.poll().getValue());
    assertEquals(false, lexer.hasNext());

    lexer = new JsonLexer("1\t2.5");
    assertEquals("1", lexer.poll().getValue());
    assertEquals(WHITESPACE, lexer.poll().getKind());
    assertEquals("2.5", lexer.poll().getValue());
    assertEquals(false, lexer.hasNext());
  }

  @Test
  public void testJsonObjects() {
    JsonLexer lexer = new JsonLexer("{\"foo\":\"b a r\"}");
    assertEquals(BEGIN_OBJECT, lexer.poll().getKind());
    assertEquals(STRING, lexer.poll().getKind());
    assertEquals(COLON, lexer.poll().getKind());
    assertEquals(STRING, lexer.poll().getKind());
    assertEquals(END_OBJECT, lexer.poll().getKind());
    assertEquals(false, lexer.hasNext());

    lexer = new JsonLexer("{\"a\":2,\"b\":false}");
    assertEquals(BEGIN_OBJECT, lexer.poll().getKind());
    assertEquals(STRING, lexer.poll().getKind());
    assertEquals(COLON, lexer.poll().getKind());
    assertEquals(NUMBER, lexer.poll().getKind());
    assertEquals(COMMA, lexer.poll().getKind());
    assertEquals(STRING, lexer.poll().getKind());
    assertEquals(COLON, lexer.poll().getKind());
    assertEquals(BOOLEAN, lexer.poll().getKind());
    assertEquals(END_OBJECT, lexer.poll().getKind());
    assertEquals(false, lexer.hasNext());
  }

  @Test
  public void testJsonArray() {
    JsonLexer lexer = new JsonLexer("[\"fo\\\"o\",\"\\b\\\\ar\"]");
    assertEquals(BEGIN_ARRAY, lexer.poll().getKind());
    assertEquals(STRING, lexer.poll().getKind());
    assertEquals(COMMA, lexer.poll().getKind());
    assertEquals(STRING, lexer.poll().getKind());
    assertEquals(END_ARRAY, lexer.poll().getKind());
    assertEquals(false, lexer.hasNext());

    lexer = new JsonLexer("[\"\\\"a\",-2.4e-5,\"b\",null]");
    assertEquals(BEGIN_ARRAY, lexer.poll().getKind());
    assertEquals(STRING, lexer.poll().getKind());
    assertEquals(COMMA, lexer.poll().getKind());
    assertEquals(NUMBER, lexer.poll().getKind());
    assertEquals(COMMA, lexer.poll().getKind());
    assertEquals(STRING, lexer.poll().getKind());
    assertEquals(COMMA, lexer.poll().getKind());
    assertEquals(NULL, lexer.poll().getKind());
    assertEquals(END_ARRAY, lexer.poll().getKind());
    assertEquals(false, lexer.hasNext());
  }

  @Test
  public void testSinglelexer() {
    checkSingleToken(":", COLON);
    checkSingleToken(",", COMMA);
    checkSingleToken("null", NULL);
    checkSingleToken("[", BEGIN_ARRAY);
    checkSingleToken("]", END_ARRAY);
    checkSingleToken("{", BEGIN_OBJECT);
    checkSingleToken("}", END_OBJECT);
    checkSingleToken(" ", WHITESPACE);
    checkSingleToken(" \t\n ", WHITESPACE);
    checkSingleToken("true", BOOLEAN);
    checkSingleToken("false", BOOLEAN);
    checkSingleToken("0", NUMBER);
    checkSingleToken("10.23", NUMBER);
    checkSingleToken("-1.2", NUMBER);
    checkSingleToken("-10e5", NUMBER);
    checkSingleToken("10E50", NUMBER);
    checkSingleToken("-10e-5", NUMBER);
    checkSingleToken("10E-50", NUMBER);
    checkSingleToken("-10e+5", NUMBER);
    checkSingleToken("123456789E+50", NUMBER);
    checkSingleToken("-10.2e+5", NUMBER);
    checkSingleToken("-2.4e-5", NUMBER);
    checkSingleToken("\"foo\"", STRING);
    checkSingleToken("\"foo\"", STRING);
    checkSingleToken("\"true\"", STRING);
    checkSingleToken("\"foo foo \"", STRING);
    checkSingleToken("\"\\u1234\"", STRING);
    checkSingleToken("\"\\u12345\"", STRING);
    checkSingleToken("\"\\b\"", STRING);
    checkSingleToken("\"\\f\"", STRING);
    checkSingleToken("\"\\n\"", STRING);
    checkSingleToken("\"\\r\"", STRING);
    checkSingleToken("\"\\t\"", STRING);
    checkSingleToken("\"\\\"\"", STRING);
    checkSingleToken("\"foo \\b\\f\\r\\n\\t\\\" foo \"", STRING);
  }

  protected void checkSingleToken(String json, JsonTokenKind expectedTokenKind) {
    JsonLexer lexer = new JsonLexer(json);
    JsonToken actual = lexer.poll();
    assertEquals(expectedTokenKind, actual.getKind());
    if (actual.getKind().hasValue()) {
      String value = actual.getValue();
      if (actual.getKind() == STRING) {
        value = "\"" + value + "\"";
      }
      assertEquals(json, value);
    }
    assertEquals(false, lexer.hasNext());
  }

}
