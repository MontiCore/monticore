/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static de.monticore.symboltable.serialization.JsonTokenKind.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonLexerTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @Test
  public void testNumberWhitespaces() {
    JsonLexer lexer = new JsonLexer("12.5 3e9");
    Assertions.assertEquals("12.5", lexer.poll().getValue());
    Assertions.assertEquals(WHITESPACE, lexer.poll().getKind());
    Assertions.assertEquals("3e9", lexer.poll().getValue());
    Assertions.assertEquals(false, lexer.hasNext());

    lexer = new JsonLexer("1\t2.5");
    Assertions.assertEquals("1", lexer.poll().getValue());
    Assertions.assertEquals(WHITESPACE, lexer.poll().getKind());
    Assertions.assertEquals("2.5", lexer.poll().getValue());
    Assertions.assertEquals(false, lexer.hasNext());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testJsonObjects() {
    JsonLexer lexer = new JsonLexer("{\"foo\":\"b a r\"}");
    Assertions.assertEquals(BEGIN_OBJECT, lexer.poll().getKind());
    Assertions.assertEquals(STRING, lexer.poll().getKind());
    Assertions.assertEquals(COLON, lexer.poll().getKind());
    Assertions.assertEquals(STRING, lexer.poll().getKind());
    Assertions.assertEquals(END_OBJECT, lexer.poll().getKind());
    Assertions.assertEquals(false, lexer.hasNext());

    lexer = new JsonLexer("{\"a\":2,\"b\":false}");
    Assertions.assertEquals(BEGIN_OBJECT, lexer.poll().getKind());
    Assertions.assertEquals(STRING, lexer.poll().getKind());
    Assertions.assertEquals(COLON, lexer.poll().getKind());
    Assertions.assertEquals(NUMBER, lexer.poll().getKind());
    Assertions.assertEquals(COMMA, lexer.poll().getKind());
    Assertions.assertEquals(STRING, lexer.poll().getKind());
    Assertions.assertEquals(COLON, lexer.poll().getKind());
    Assertions.assertEquals(BOOLEAN, lexer.poll().getKind());
    Assertions.assertEquals(END_OBJECT, lexer.poll().getKind());
    Assertions.assertEquals(false, lexer.hasNext());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testJsonArray() {
    JsonLexer lexer = new JsonLexer("[\"fo\\\"o\",\"\\b\\\\ar\"]");
    Assertions.assertEquals(BEGIN_ARRAY, lexer.poll().getKind());
    Assertions.assertEquals(STRING, lexer.poll().getKind());
    Assertions.assertEquals(COMMA, lexer.poll().getKind());
    Assertions.assertEquals(STRING, lexer.poll().getKind());
    Assertions.assertEquals(END_ARRAY, lexer.poll().getKind());
    Assertions.assertEquals(false, lexer.hasNext());

    lexer = new JsonLexer("[\"\\\"a\",-2.4e-5,\"b\",null]");
    Assertions.assertEquals(BEGIN_ARRAY, lexer.poll().getKind());
    Assertions.assertEquals(STRING, lexer.poll().getKind());
    Assertions.assertEquals(COMMA, lexer.poll().getKind());
    Assertions.assertEquals(NUMBER, lexer.poll().getKind());
    Assertions.assertEquals(COMMA, lexer.poll().getKind());
    Assertions.assertEquals(STRING, lexer.poll().getKind());
    Assertions.assertEquals(COMMA, lexer.poll().getKind());
    Assertions.assertEquals(NULL, lexer.poll().getKind());
    Assertions.assertEquals(END_ARRAY, lexer.poll().getKind());
    Assertions.assertEquals(false, lexer.hasNext());
    
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
    checkSingleToken("\"\\u1234\"", "\u1234", STRING);
    checkSingleToken("\"\\u12345\"", "\u12345", STRING);
    checkSingleToken("\"\\b\"", "\b", STRING);
    checkSingleToken("\"\\f\"", "\f",STRING);
    checkSingleToken("\"\\n\"", "\n", STRING);
    checkSingleToken("\"\\r\"", "\r", STRING);
    checkSingleToken("\"\\t\"", "\t",STRING);
    checkSingleToken("\"\\\"\"", "\"",STRING);
    checkSingleToken("\"foo \\b\\f\\r\\n\\t\\\" foo \"", "foo \b\f\r\n\t\" foo ", STRING);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  protected void checkSingleToken(String json, String expectedContent, JsonTokenKind expectedTokenKind){
    JsonLexer lexer = new JsonLexer(json);
    JsonToken actual = lexer.poll();
    Assertions.assertEquals(expectedTokenKind, actual.getKind());
    if (actual.getKind().hasValue()) {
      String value = actual.getValue();
      Assertions.assertEquals(expectedContent, value);
    }
    Assertions.assertEquals(false, lexer.hasNext());
  }

  protected void checkSingleToken(String json, JsonTokenKind expectedTokenKind) {
    // Strip starting and ending " from String
    checkSingleToken(json, expectedTokenKind == STRING ? json.replaceAll("^\"", "").replaceAll("\"$", "") : json, expectedTokenKind);
  }

}
