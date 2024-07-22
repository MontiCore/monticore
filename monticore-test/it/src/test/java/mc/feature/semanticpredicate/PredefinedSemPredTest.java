/* (c) https://github.com/MontiCore/monticore */

package mc.feature.semanticpredicate;

import de.se_rwth.commons.logging.LogStub;
import mc.GeneratorIntegrationsTest;

import mc.feature.semanticpredicate.predefinedsempred._parser.PredefinedSemPredParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import de.se_rwth.commons.logging.Log;

public class PredefinedSemPredTest extends GeneratorIntegrationsTest {
  
  @BeforeEach
  public void before() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Test
  public void testParse() throws IOException {
    PredefinedSemPredParser p = new PredefinedSemPredParser();

    // noSpace: "foo" "::" "foo"
    p.parse_StringA("foo :: foo");
    Assertions.assertFalse(p.hasErrors());
    p.parse_StringA("foo : : foo");
    Assertions.assertTrue(p.hasErrors());

    p.parse_StringB("foo :: foo");
    Assertions.assertFalse(p.hasErrors());
    p.parse_StringB("foo : : foo");
    Assertions.assertTrue(p.hasErrors());

    p.parse_StringC("foo :: foo");
    Assertions.assertFalse(p.hasErrors());
    p.parse_StringC("foo : : foo");
    Assertions.assertTrue(p.hasErrors());

    // is/next/cmpToken: "foo" ":";
    p.parse_StringD("foon :");
    Assertions.assertFalse(p.hasErrors());
    p.parse_StringD("FOO :");
    Assertions.assertTrue(p.hasErrors());

    p.parse_StringE("foon :");
    Assertions.assertFalse(p.hasErrors());
    p.parse_StringE("FOO :");
    Assertions.assertTrue(p.hasErrors());

    p.parse_StringF("foon :");
    Assertions.assertFalse(p.hasErrors());
    p.parse_StringF("FOO :");
    Assertions.assertTrue(p.hasErrors());

    p.parse_StringG("foon :");
    Assertions.assertFalse(p.hasErrors());
    p.parse_StringG("FOO :");
    Assertions.assertTrue(p.hasErrors());

    // cmpTokenRegEx: ("foo"|"FOO") ":";
    p.parse_StringH("foon :");
    Assertions.assertFalse(p.hasErrors());
    p.parse_StringH("FOO :");
    Assertions.assertFalse(p.hasErrors());
    p.parse_StringH("FOO1 :");
    Assertions.assertTrue(p.hasErrors());

    p.parse_StringI("foon :");
    Assertions.assertFalse(p.hasErrors());
    p.parse_StringI("FOO :");
    Assertions.assertFalse(p.hasErrors());
    p.parse_StringI("FOO1 :");
    Assertions.assertTrue(p.hasErrors());

    // cmpToken (list): "foo"|"FOO") ":";
    p.parse_StringJ("foon :");
    Assertions.assertFalse(p.hasErrors());
    p.parse_StringJ("FOO :");
    Assertions.assertFalse(p.hasErrors());
    p.parse_StringJ("FOO1 :");
    Assertions.assertTrue(p.hasErrors());
  }
  
}
