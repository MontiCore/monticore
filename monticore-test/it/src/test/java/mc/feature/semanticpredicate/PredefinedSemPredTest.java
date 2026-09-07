/* (c) https://github.com/MontiCore/monticore */

package mc.feature.semanticpredicate;

import de.monticore.runtime.junit.MCAssertions;
import de.monticore.runtime.junit.TestWithMCLanguage;

import mc.feature.semanticpredicate.predefinedsempred.PredefinedSemPredMill;
import mc.feature.semanticpredicate.predefinedsempred._parser.PredefinedSemPredParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(PredefinedSemPredMill.class)
public class PredefinedSemPredTest {
  
  @Test
  public void testParse() throws IOException {
    PredefinedSemPredParser p = PredefinedSemPredMill.parser();

    // noSpace: "foo" "::" "foo"
    p.parse_StringA("foo :: foo");
    assertFalse(p.hasErrors());
    p.parse_StringA("foo : : foo");
    assertTrue(p.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule a failed predicate: {noSpace()}?");

    p.parse_StringB("foo :: foo");
    assertFalse(p.hasErrors());
    p.parse_StringB("foo : : foo");
    assertTrue(p.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule b failed predicate: {noSpace(-2)}?");
    p.parse_StringC("foo :: foo");
    assertFalse(p.hasErrors());
    p.parse_StringC("foo : : foo");
    assertTrue(p.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule c failed predicate: {noSpace(3)}?");

    // is/next/cmpToken: "foo" ":";
    p.parse_StringD("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringD("FOO :");
    assertTrue(p.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule d failed predicate: {next(\"foon\")}?");

    p.parse_StringE("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringE("FOO :");
    assertTrue(p.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule e failed predicate: {is(\"foon\")}?");
    
    p.parse_StringF("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringF("FOO :");
    assertTrue(p.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule f failed predicate: {cmpToken(1,\"foon\")}?");
    
    p.parse_StringG("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringG("FOO :");
    assertTrue(p.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule g failed predicate: {cmpToken(-2,\"foon\")}?");
    
    // cmpTokenRegEx: ("foo"|"FOO") ":";
    p.parse_StringH("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringH("FOO :");
    assertFalse(p.hasErrors());
    p.parse_StringH("FOO1 :");
    assertTrue(p.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule h failed predicate: {cmpTokenRegEx(1,\"foon|FOO\")}?");
    
    p.parse_StringI("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringI("FOO :");
    assertFalse(p.hasErrors());
    p.parse_StringI("FOO1 :");
    assertTrue(p.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule i failed predicate: {cmpTokenRegEx(-2,\"foon|FOO\")}?");
    
    // cmpToken (list): "foo"|"FOO") ":";
    p.parse_StringJ("foon :");
    assertFalse(p.hasErrors());
    p.parse_StringJ("FOO :");
    assertFalse(p.hasErrors());
    p.parse_StringJ("FOO1 :");
    assertTrue(p.hasErrors());
    MCAssertions.assertHasFindingStartingWith("rule j failed predicate: {cmpToken(-2,\"foon\",\"FOO\")}?");
  }
  
}
