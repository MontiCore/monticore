package de.monticore.symboltable.serialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.monticore.symboltable.serialization.json.JsonObject;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class JsonParserTest {
  
  @Test
  public void testSimpleObject() {
    JsonObject result = JsonParser.parseJsonObject("{\"foo\":false,\"bar\":3,\"bla\":\"yes\",\"blub\":3.4}");
    assertTrue(result.get("foo").isJsonBoolean());
    assertEquals(false, result.get("foo").getAsJsonBoolean().getValue());
    
    assertTrue(result.get("bar").isJsonNumber());
    assertEquals(3, result.get("bar").getAsJsonNumber().getNumberAsInt());

    assertTrue(result.get("bla").isJsonString());
    assertEquals("yes", result.get("bla").getAsJsonString().getValue());

    assertTrue(result.get("blub").isJsonNumber());
    assertEquals(3.4, result.get("blub").getAsJsonNumber().getNumberAsDouble(), 0.1);
  }
  
}
