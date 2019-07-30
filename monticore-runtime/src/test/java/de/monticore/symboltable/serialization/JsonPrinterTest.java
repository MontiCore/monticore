/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serialization;

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class JsonPrinterTest {
  
  @Test
  public void testEscapeSequences() {
    JsonPrinter printer = new JsonPrinter();
    printer.attribute("\"\t\\\n\'");
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
    JsonPrinter printer = new JsonPrinter();
    printer.beginAttributeList();
    printer.endAttributeList();
    assertEquals("[]", printer.toString());
    
    printer = new JsonPrinter();
    printer.beginAttributeList("emptyList");
    printer.endAttributeList();
    assertEquals("\"emptyList\":[]", printer.toString());
  }
  
  @Test
  public void testBasicTypeAttributes() {
    JsonPrinter printer = new JsonPrinter();
    printer.attribute("booleanAttribute", true);
    assertEquals("\"booleanAttribute\":true", printer.toString());
    
    printer = new JsonPrinter();
    printer.attribute("intAttribute", -1);
    assertEquals("\"intAttribute\":-1", printer.toString());
    
    printer = new JsonPrinter();
    printer.attribute("floatAttribute", 47.11f);
    assertEquals("\"floatAttribute\":47.11", printer.toString());
    
    printer = new JsonPrinter();
    printer.attribute("doubleAttribute", 47.11);
    assertEquals("\"doubleAttribute\":47.11", printer.toString());
    
    printer = new JsonPrinter();
    printer.attribute("longAttribute", 123456789L);
    assertEquals("\"longAttribute\":123456789", printer.toString());
  }
  
  @Test
  public void testOptionalAndList() {
    JsonPrinter printer = new JsonPrinter();
    printer.attribute("optionalAttribute", Optional.of("presentOptional"));
    assertEquals("\"optionalAttribute\":\"presentOptional\"", printer.toString());
    
    printer = new JsonPrinter(true);
    printer.attribute("optionalAttribute", Optional.empty());
    assertEquals("\"optionalAttribute\":null", printer.toString());
    
    printer = new JsonPrinter();
    printer.attribute("optionalAttribute", Optional.empty());
    assertEquals("", printer.toString());
    
    printer = new JsonPrinter(true);
    printer.attribute("listAttribute", Lists.newArrayList());
    assertEquals("\"listAttribute\":[]", printer.toString());
    
    printer = new JsonPrinter();
    printer.attribute("listAttribute", Lists.newArrayList());
    assertEquals("", printer.toString());
    
    printer = new JsonPrinter();
    printer.attribute("listAttribute", Lists.newArrayList("first", "second"));
    assertEquals("\"listAttribute\":[\"first\",\"second\"]", printer.toString());
  }
  
  @Test
  public void testInvalidNestings() {
    //init Log and mute System.err temporarily
    LogStub.init();
    Log.enableFailQuick(false);
    PrintStream _err = System.err;
    System.setErr(new PrintStream(new OutputStream() {
      @Override
      public void write(int b) throws IOException {
      }}));
    
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
    printer.beginAttributeList();
    printer.beginAttributeList();
    printer.endAttributeList();
    printer.getContent();
    assertEquals(1, Log.getFindings().size());
  
    LogStub.init();
    Log.enableFailQuick(false);
    printer = new JsonPrinter();
    printer.beginAttributeList();
    printer.endAttributeList();
    printer.endAttributeList();
    printer.getContent();
    assertEquals(1, Log.getFindings().size());
  
    LogStub.init();
    Log.enableFailQuick(false);
    printer = new JsonPrinter();
    printer.beginObject();
    printer.beginAttributeList();
    printer.endAttributeList();
    printer.endAttributeList();
    printer.endObject();
    printer.getContent();
    assertEquals(1, Log.getFindings().size());
    
    //unmute Sytem.err
    System.setErr(_err);
    
  }
  
}
