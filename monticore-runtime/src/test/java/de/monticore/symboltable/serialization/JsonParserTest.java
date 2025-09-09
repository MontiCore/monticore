/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.monticore.symboltable.serialization.json.JsonObject;

import java.util.function.Function;

public class JsonParserTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

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
    Assertions.assertTrue(Log.getErrorCount()>0);
  }
  
  @Test
  public void testSimpleObject() {
    JsonObject result = JsonParser.parseJsonObject("{\"foo\":false,\"bar\":3,\"bla\":\"yes\",\"blub\":3.4}");
    Assertions.assertTrue(result.getMember("foo").isJsonBoolean());
    Assertions.assertEquals(false, result.getMember("foo").getAsJsonBoolean().getValue());
    
    Assertions.assertTrue(result.getMember("bar").isJsonNumber());
    Assertions.assertEquals(3, result.getMember("bar").getAsJsonNumber().getNumberAsInteger());

    Assertions.assertTrue(result.getMember("bla").isJsonString());
    Assertions.assertEquals("yes", result.getMember("bla").getAsJsonString().getValue());

    Assertions.assertTrue(result.getMember("blub").isJsonNumber());
    Assertions.assertEquals(3.4, result.getMember("blub").getAsJsonNumber().getNumberAsDouble(), 0.1);

    Assertions.assertTrue(JsonParser.parseJsonObject("{}").isJsonObject());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testStringEscapes(){
    JsonObject result = JsonParser.parseJsonObject("{\"foo\":\"\\n\"}");
    Assertions.assertTrue(result.getMember("foo").isJsonString());
    Assertions.assertEquals("\n", result.getMember("foo").getAsJsonString().getValue());
  }

}
