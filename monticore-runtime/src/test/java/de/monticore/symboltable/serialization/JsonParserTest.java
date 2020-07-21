/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.se_rwth.commons.logging.Log;
import org.junit.Test;

import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.function.Function;

public class JsonParserTest {

  @Test
  public void testSimpleInvalidObject() {
    Log.enableFailQuick(false);
    testParseErroneousJson(JsonParser::parseJsonObject,"{\"foo\":false,,\"bar\":3}");
    testParseErroneousJson(JsonParser::parseJsonObject,"{\"foo\":false,\"bar\":3,}");
    testParseErroneousJson(JsonParser::parseJsonObject,"{\"foo\"        :false\"bar\":3}");
    testParseErroneousJson(JsonParser::parseJsonObject,"{\"foo\"false}");
    testParseErroneousJson(JsonParser::parseJsonObject,"{4711}");
    testParseErroneousJson(JsonParser::parseJsonObject,"{false");
    testParseErroneousJson(JsonParser::parseJsonObject,"{\"false\" : \"noending\"");
    Log.getFindings().clear();
  }

  @Test
  public void testSimpleInvalidArray() {
    Log.enableFailQuick(false);
    testParseErroneousJson(JsonParser::parseJsonArray, "[\"foo\",false,,\"bar\":3]");
    testParseErroneousJson(JsonParser::parseJsonArray,"[\"foo\",false,\"bar\":3,]");
    testParseErroneousJson(JsonParser::parseJsonArray,"[\"foo\" ,false \"bar\" 3]");
    testParseErroneousJson(JsonParser::parseJsonArray,"[\"foo\"false]");
    testParseErroneousJson(JsonParser::parseJsonArray,"[47 11]");
    testParseErroneousJson(JsonParser::parseJsonArray,"[,false");
    testParseErroneousJson(JsonParser::parseJsonArray,"[fal se");
    testParseErroneousJson(JsonParser::parseJsonArray,"[\"false\", \"noending\"");
    Log.getFindings().clear();
  }

  protected void testParseErroneousJson(Function<String, Object> f, String json){
    Log.getFindings().clear();
    try{
      f.apply(json);
    }
    catch (RuntimeException e){
      //Ignore exceptions that occur because fail quick is disabled
    }
    assertTrue(Log.getErrorCount()>0);
  }
  
  @Test
  public void testSimpleObject() {
    JsonObject result = JsonParser.parseJsonObject("{\"foo\":false,\"bar\":3,\"bla\":\"yes\",\"blub\":3.4}");
    assertTrue(result.getMember("foo").isJsonBoolean());
    assertEquals(false, result.getMember("foo").getAsJsonBoolean().getValue());
    
    assertTrue(result.getMember("bar").isJsonNumber());
    assertEquals(3, result.getMember("bar").getAsJsonNumber().getNumberAsInt());

    assertTrue(result.getMember("bla").isJsonString());
    assertEquals("yes", result.getMember("bla").getAsJsonString().getValue());

    assertTrue(result.getMember("blub").isJsonNumber());
    assertEquals(3.4, result.getMember("blub").getAsJsonNumber().getNumberAsDouble(), 0.1);

    assertTrue( JsonParser.parseJsonObject("{}").isJsonObject());
  }
  
}
