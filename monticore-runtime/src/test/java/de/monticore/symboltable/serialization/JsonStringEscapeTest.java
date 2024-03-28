package de.monticore.symboltable.serialization;

import de.monticore.symboltable.serialization.json.JsonElement;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JsonStringEscapeTest {

  @Test
  public void testSingleQuoteNotEscaped(){
    JsonPrinter printer = new JsonPrinter();
    printer.value("'");

    String content = printer.getContent();
    assertEquals("\"'\"", content);
  }

  @Test
  public void testUtf8Escaped(){
    JsonPrinter printer = new JsonPrinter();
    printer.value("✔");

    String content = printer.getContent();
    assertEquals("\"\\u2714\"", content);
  }

  @Test
  public void testQuotesRoundtrip(){
    testRoundtrip("\"'");
  }

  @Test
  public void testUtf8Roundtrip(){
    testRoundtrip("✔");
  }

  @Test
  public void testDoubleQuoteRoundtrip() {
    testRoundtrip("\"");
  }

  @Test
  public void testBackslashRoundtrip() {
    testRoundtrip("\\");
  }

  @Test
  public void testSlashRoundtrip() {
    testRoundtrip("/");
  }

  @Test
  public void testBackspaceRoundtrip() {
    testRoundtrip("\b");
  }

  @Test
  public void testFormFeedRoundtrip() {
    testRoundtrip("\f");
  }

  @Test
  public void testNewlineRoundtrip() {
    testRoundtrip("\n");
  }

  @Test
  public void testCarriageReturnRoundtrip() {
    testRoundtrip("\r");
  }

  @Test
  public void testTabRoundtrip() {
    testRoundtrip("\t");
  }


  @Test
  public void testStringWithoutEscapeRoundtrip(){
    testRoundtrip("Hello World");
  }

  protected void testRoundtrip(String input) {
    JsonPrinter printer = new JsonPrinter();
    printer.value(input);

    String content = printer.getContent();
    JsonElement e = JsonParser.parse(content);
    assertTrue(e.isJsonString());

    assertEquals(input, e.getAsJsonString().getValue());
  }

}
