/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.completeness._ast.ASTCompleteness;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.testcompleteness.TestCompletenessMill;
import de.monticore.testcompleteness._parser.TestCompletenessParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestCompletenessMill.class)
public class CompletenessPrettyPrinterTest {

  @Test
  public void testCompleteness() throws IOException {
    TestCompletenessParser parser = TestCompletenessMill.parser();
    Optional<ASTCompleteness> result = parser.parse_StringCompleteness("(c)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    String output = TestCompletenessMill.prettyPrint(completeness, false);
    
    result = parser.parse_StringCompleteness(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  }
  
  @Test
  public void testIncompleteness() throws IOException {
    TestCompletenessParser parser = TestCompletenessMill.parser();
    Optional<ASTCompleteness> result = parser.parse_StringCompleteness("(...)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    String output = TestCompletenessMill.prettyPrint(completeness, false);
    
    result = parser.parse_StringCompleteness(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  }
  
  @Test
  public void testRightCompleteness() throws IOException {
    TestCompletenessParser parser = TestCompletenessMill.parser();
    Optional<ASTCompleteness> result = parser.parse_StringCompleteness("(...,c)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    String output = TestCompletenessMill.prettyPrint(completeness, false);
    
    result = parser.parse_StringCompleteness(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  }
  
  @Test
  public void testLeftCompleteness() throws IOException {
    TestCompletenessParser parser = TestCompletenessMill.parser();
    Optional<ASTCompleteness> result = parser.parse_StringCompleteness("(c,...)");
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    String output = TestCompletenessMill.prettyPrint(completeness, false);
    
    result = parser.parse_StringCompleteness(output);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  }
}
