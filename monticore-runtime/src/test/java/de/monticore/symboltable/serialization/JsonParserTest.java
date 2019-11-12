/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.monticore.symboltable.serialization.json.JsonObject;

public class JsonParserTest {
  
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
  }
  
}
