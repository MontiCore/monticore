/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import de.monticore.completeness._ast.ASTCompleteness;
import de.monticore.runtime.junit.TestWithMCLanguage;
import de.monticore.testcompleteness.TestCompletenessMill;
import de.monticore.testcompleteness._parser.TestCompletenessParser;
import de.se_rwth.commons.logging.Log;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestWithMCLanguage(TestCompletenessMill.class)
public class CompletenessPrettyPrinterTest {

  @Test
  public void testCompleteness() throws IOException {
    TestCompletenessParser parser = TestCompletenessMill.parser();
    Optional<ASTCompleteness> result = parser.parseCompleteness(new StringReader("(c)"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    String output = TestCompletenessMill.prettyPrint(completeness, false);
    
    result = parser.parseCompleteness(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testIncompleteness() throws IOException {
    TestCompletenessParser parser = TestCompletenessMill.parser();
    Optional<ASTCompleteness> result = parser.parseCompleteness(new StringReader("(...)"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    String output = TestCompletenessMill.prettyPrint(completeness, false);
    
    result = parser.parseCompleteness(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testRightCompleteness() throws IOException {
    TestCompletenessParser parser = TestCompletenessMill.parser();
    Optional<ASTCompleteness> result = parser.parseCompleteness(new StringReader("(...,c)"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    String output = TestCompletenessMill.prettyPrint(completeness, false);
    
    result = parser.parseCompleteness(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testLeftCompleteness() throws IOException {
    TestCompletenessParser parser = TestCompletenessMill.parser();
    Optional<ASTCompleteness> result = parser.parseCompleteness(new StringReader("(c,...)"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    String output = TestCompletenessMill.prettyPrint(completeness, false);
    
    result = parser.parseCompleteness(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
