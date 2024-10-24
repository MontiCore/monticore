/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import com.google.common.collect.Lists;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests the JsonPrinter
 */
public class JsonPrinterTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }

  @BeforeEach
  public void disableIndentation() {
    JsonPrinter.disableIndentation();
  }

  @Test
  public void testOmitEmptyArray() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.member("name", "anArtifactScopeName");
    printer.beginArray("kindHierarchy");
    printer.endArray();
    printer.beginArray("symbols");
    printer.value("foo");
    printer.endArray();
    printer.endObject();

    String serialized = printer.getContent();
    Assertions.assertTrue(null != JsonParser.parseJsonObject(serialized));
    Assertions.assertTrue(!serialized.contains("kindHierarchy"));
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEscapeSequences() {
    JsonPrinter printer = new JsonPrinter();
    printer.value("\"\t\\\n'");
    Assertions.assertEquals("\"\\\"\\t\\\\\\n'\"", printer.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEmptyObject() {
    JsonPrinter printer = new JsonPrinter(true);
    printer.beginObject();
    printer.endObject();
    Assertions.assertEquals("{}", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginObject();
    printer.endObject();
    Assertions.assertEquals("", printer.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefaultString() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.member("s", "");
    printer.endObject();
    Assertions.assertEquals("", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("s", "");
    printer.endObject();
    Assertions.assertEquals("{\"s\":\"\"}", printer.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefaultInt() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.member("i", 0);
    printer.endObject();
    Assertions.assertEquals("", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("i", 0);
    printer.endObject();
    Assertions.assertEquals("{\"i\":0}", printer.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testDefaultBoolean() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.member("b", false);
    printer.endObject();
    Assertions.assertEquals("", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("b", false);
    printer.endObject();
    Assertions.assertEquals("{\"b\":false}", printer.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testEmptyList() {
    JsonPrinter printer = new JsonPrinter(true);
    printer.beginArray();
    printer.endArray();
    Assertions.assertEquals("[]", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.beginArray("emptyList");
    printer.endArray();
    printer.endObject();
    Assertions.assertEquals("{\"emptyList\":[]}", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginArray();
    printer.endArray();
    Assertions.assertEquals("", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginObject();
    printer.beginArray("emptyList");
    printer.endArray();
    printer.endObject();
    Assertions.assertEquals("", printer.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testBasicTypeAttributes() {
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.member("booleanAttribute", true);
    printer.endObject();
    Assertions.assertEquals("{\"booleanAttribute\":true}", printer.toString());

    printer = new JsonPrinter();
    printer.beginObject();
    printer.member("intAttribute", -1);
    printer.endObject();
    Assertions.assertEquals("{\"intAttribute\":-1}", printer.toString());

    printer = new JsonPrinter();
    printer.beginObject();
    printer.member("floatAttribute", 47.11f);
    printer.endObject();
    Assertions.assertEquals("{\"floatAttribute\":47.11}", printer.toString());

    printer = new JsonPrinter();
    printer.beginObject();
    printer.member("doubleAttribute", 47.11);
    printer.endObject();
    Assertions.assertEquals("{\"doubleAttribute\":47.11}", printer.toString());

    printer = new JsonPrinter();
    printer.beginObject();
    printer.member("longAttribute", 123456789L);
    printer.endObject();
    Assertions.assertEquals("{\"longAttribute\":123456789}", printer.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testOptionalAndList() {
    JsonPrinter printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("optionalAttribute", Optional.of("presentOptional"));
    printer.endObject();
    Assertions.assertEquals("{\"optionalAttribute\":\"presentOptional\"}", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginObject();
    printer.member("optionalAttribute", Optional.of("presentOptional"));
    printer.endObject();
    Assertions.assertEquals("{\"optionalAttribute\":\"presentOptional\"}", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("optionalAttribute", Optional.empty());
    printer.endObject();
    Assertions.assertEquals("{\"optionalAttribute\":null}", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginObject();
    printer.member("optionalAttribute", Optional.empty());
    printer.endObject();
    Assertions.assertEquals("", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("listAttribute", new ArrayList<>());
    printer.endObject();
    Assertions.assertEquals("{\"listAttribute\":[]}", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginObject();
    printer.member("listAttribute", new ArrayList<>());
    printer.endObject();
    Assertions.assertEquals("", printer.toString());

    printer = new JsonPrinter(true);
    printer.beginObject();
    printer.member("listAttribute", Lists.newArrayList("first", "second"));
    printer.endObject();
    Assertions.assertEquals("{\"listAttribute\":[\"first\",\"second\"]}", printer.toString());

    printer = new JsonPrinter(false);
    printer.beginObject();
    printer.member("listAttribute", Lists.newArrayList("first", "second"));
    printer.endObject();
    Assertions.assertEquals("{\"listAttribute\":[\"first\",\"second\"]}", printer.toString());
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testInvalidNestings() {

    Log.clearFindings();
    JsonPrinter printer = new JsonPrinter();
    printer.beginObject();
    printer.beginObject();
    printer.endObject();
    printer.getContent();
    Assertions.assertEquals(1, Log.getFindings().size());

    Log.clearFindings();
    printer = new JsonPrinter();
    printer.beginObject();
    printer.endObject();
    printer.endObject();
    printer.getContent();
    Assertions.assertEquals(1, Log.getFindings().size());

    Log.clearFindings();
    printer = new JsonPrinter();
    printer.beginArray();
    printer.beginArray();
    printer.endArray();
    printer.getContent();
    Assertions.assertEquals(1, Log.getFindings().size());

    Log.clearFindings();
    printer = new JsonPrinter();
    printer.beginArray();
    printer.endArray();
    printer.endArray();
    printer.getContent();
    Assertions.assertEquals(1, Log.getFindings().size());

    Log.clearFindings();
    printer = new JsonPrinter();
    printer.beginObject();
    printer.beginArray();
    printer.endArray();
    printer.endArray();
    printer.endObject();
    printer.getContent();
    Assertions.assertEquals(3, Log.getFindings().size());
  }

}
