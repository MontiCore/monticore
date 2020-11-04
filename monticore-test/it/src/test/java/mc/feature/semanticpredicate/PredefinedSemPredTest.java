/* (c) https://github.com/MontiCore/monticore */

package mc.feature.semanticpredicate;

import mc.GeneratorIntegrationsTest;

import mc.feature.semanticpredicate.predefinedsempred._parser.PredefinedSemPredParser;
import org.junit.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

public class PredefinedSemPredTest extends GeneratorIntegrationsTest {
  
  @Test
  public void testParse() throws IOException {
    PredefinedSemPredParser p = new PredefinedSemPredParser();

    // noSpace: "foo" "::" "foo"
    p.parse_StringA("foo :: foo");
    assertFalse(p.hasErrors());
    p.parse_StringA("foo : : foo");
    assertTrue(p.hasErrors());

    p.parse_StringB("foo :: foo");
    assertFalse(p.hasErrors());
    p.parse_StringB("foo : : foo");
    assertTrue(p.hasErrors());

    p.parse_StringC("foo :: foo");
    assertFalse(p.hasErrors());
    p.parse_StringC("foo : : foo");
    assertTrue(p.hasErrors());

    // is/next/cmpToken: "foo" ":";
    p.parse_StringD("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringD("FOO :");
    assertTrue(p.hasErrors());

    p.parse_StringE("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringE("FOO :");
    assertTrue(p.hasErrors());

    p.parse_StringF("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringF("FOO :");
    assertTrue(p.hasErrors());

    p.parse_StringG("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringG("FOO :");
    assertTrue(p.hasErrors());

    // cmpTokenRegEx: ("foo"|"FOO") ":";
    p.parse_StringH("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringH("FOO :");
    assertFalse(p.hasErrors());
    p.parse_StringH("FOO1 :");
    assertTrue(p.hasErrors());

    p.parse_StringI("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringI("FOO :");
    assertFalse(p.hasErrors());
    p.parse_StringI("FOO1 :");
    assertTrue(p.hasErrors());

    // cmpToken (list): "foo"|"FOO") ":";
    p.parse_StringJ("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringJ("FOO :");
    assertFalse(p.hasErrors());
    p.parse_StringJ("FOO1 :");
    assertTrue(p.hasErrors());

  }
  
}
