/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

import static org.junit.Assert.*;

/**
 * Tests the JsonPrinter
 */
public class JsonPrinterTest {
  
  @Before
  public void disableIndentation() {
    JsonPrinter.disableIndentation();
  }
  
  @Test
  public void testEscapeSequences() {
    JsonPrinter printer = new JsonPrinter();
    printer.value("\"\t\\\n\'");
    assertEquals("\"\\\"\\t\\\\\\n\\'\"", printer.toString());
  }
  
  @Test
  public void testEmptyObject() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.endObject();
    assertEquals("{}", printer.toString());
  }
  
  @Test
  public void testEmptyList() {
    JsonPrinter printer = new JsonPrinter(true);
    printer.beginArray();
    printer.endArray();
    assertEquals("[]", printer.toString());
    
    printer = new JsonPrinter(true);
    printer.beginArray("emptyList");
    printer.endArray();
    assertEquals("\"emptyList\":[]", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginArray();
    printer.endArray();
    assertEquals("", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginArray("emptyList");
    printer.endArray();
    assertEquals("", printer.toString());
  }
  
  @Test
  public void testBasicTypeAttributes() {
    JsonPrinter printer = new JsonPrinter();
    printer.member("booleanAttribute", true);
    assertEquals("\"booleanAttribute\":true", printer.toString());
    
    printer = new JsonPrinter();
    printer.member("intAttribute", -1);
    assertEquals("\"intAttribute\":-1", printer.toString());
    
    printer = new JsonPrinter();
    printer.member("floatAttribute", 47.11f);
    assertEquals("\"floatAttribute\":47.11", printer.toString());
    
    printer = new JsonPrinter();
    printer.member("doubleAttribute", 47.11);
    assertEquals("\"doubleAttribute\":47.11", printer.toString());
    
    printer = new JsonPrinter();
    printer.member("longAttribute", 123456789L);
    assertEquals("\"longAttribute\":123456789", printer.toString());
  }
  
  @Test
  public void testOptionalAndList() {
    JsonPrinter printer = new JsonPrinter(true);
    printer.member("optionalAttribute", Optional.of("presentOptional"));
    assertEquals("\"optionalAttribute\":\"presentOptional\"", printer.toString());

    printer = new JsonPrinter(false);
    printer.member("optionalAttribute", Optional.of("presentOptional"));
    assertEquals("\"optionalAttribute\":\"presentOptional\"", printer.toString());

    printer = new JsonPrinter(true);
    printer.member("optionalAttribute", Optional.empty());
    assertEquals("\"optionalAttribute\":null", printer.toString());

    printer = new JsonPrinter(false);
    printer.member("optionalAttribute", Optional.empty());
    assertEquals("", printer.toString());

    printer = new JsonPrinter(true);
    printer.member("listAttribute", new ArrayList<>());
    assertEquals("\"listAttribute\":[]", printer.toString());

    printer = new JsonPrinter(false);
    printer.member("listAttribute", new ArrayList<>());
    assertEquals("", printer.toString());

    printer = new JsonPrinter(true);
    printer.member("listAttribute", Lists.newArrayList("first", "second"));
    assertEquals("\"listAttribute\":[\"first\",\"second\"]", printer.toString());

    printer = new JsonPrinter(false);
    printer.member("listAttribute", Lists.newArrayList("first", "second"));
    assertEquals("\"listAttribute\":[\"first\",\"second\"]", printer.toString());
  }
  
  @Test
  public void testInvalidNestings() {
    // init Log and mute System.err temporarily
    LogStub.init();
    Log.enableFailQuick(false);
    PrintStream _err = System.err;
    System.setErr(new PrintStream(new OutputStream() {
      @Override
      public void write(int b) throws IOException {
      }
    }));
    
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.beginObject();
    printer.endObject();
    printer.getContent();
    assertEquals(1, Log.getFindings().size());
    
    LogStub.init();
    Log.enableFailQuick(false);
    printer = new JsonPrinter();
    printer.beginObject();
    printer.endObject();
    printer.endObject();
    printer.getContent();
    assertEquals(1, Log.getFindings().size());
    
    LogStub.init();
    Log.enableFailQuick(false);
    printer = new JsonPrinter();
    printer.beginArray();
    printer.beginArray();
    printer.endArray();
    printer.getContent();
    assertEquals(1, Log.getFindings().size());
    
    LogStub.init();
    Log.enableFailQuick(false);
    printer = new JsonPrinter();
    printer.beginArray();
    printer.endArray();
    printer.endArray();
    printer.getContent();
    assertEquals(1, Log.getFindings().size());
    
    LogStub.init();
    Log.enableFailQuick(false);
    printer = new JsonPrinter();
    printer.beginObject();
    printer.beginArray();
    printer.endArray();
    printer.endArray();
    printer.endObject();
    printer.getContent();
    assertEquals(1, Log.getFindings().size());
    
    // unmute Sytem.err
    System.setErr(_err);
    
  }

  @Test
  public void testIsSerializingEmptyLists() {
    JsonPrinter serializeEmpty = new JsonPrinter(true);
    JsonPrinter serializeEmptyNot = new JsonPrinter();
    assertTrue(serializeEmpty.isSerializingEmptyLists());
    assertFalse(serializeEmptyNot.isSerializingEmptyLists());
  }
  
}
