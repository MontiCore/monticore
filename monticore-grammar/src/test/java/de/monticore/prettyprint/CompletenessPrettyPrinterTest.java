/* (c) https://github.com/MontiCore/monticore */

package de.monticore.prettyprint;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.util.Optional;

import de.monticore.testcompleteness.TestCompletenessMill;
import org.junit.Before;
import org.junit.Test;
import de.monticore.completeness._ast.ASTCompleteness;
import de.monticore.completeness._prettyprint.CompletenessFullPrettyPrinter;
import de.monticore.testcompleteness._parser.TestCompletenessParser;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;

public class CompletenessPrettyPrinterTest {
  
  @Before
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestCompletenessMill.reset();
    TestCompletenessMill.init();
  }
  
  @Test
  public void testCompleteness() throws IOException {
    TestCompletenessParser parser = new TestCompletenessParser();
    Optional<ASTCompleteness> result = parser.parseCompleteness(new StringReader("(c)"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    CompletenessFullPrettyPrinter prettyPrinter = new CompletenessFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(completeness);
    
    result = parser.parseCompleteness(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testIncompleteness() throws IOException {
    TestCompletenessParser parser = new TestCompletenessParser();
    Optional<ASTCompleteness> result = parser.parseCompleteness(new StringReader("(...)"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    CompletenessFullPrettyPrinter prettyPrinter = new CompletenessFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(completeness);
    
    result = parser.parseCompleteness(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testRightCompleteness() throws IOException {
    TestCompletenessParser parser = new TestCompletenessParser();
    Optional<ASTCompleteness> result = parser.parseCompleteness(new StringReader("(...,c)"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    CompletenessFullPrettyPrinter prettyPrinter = new CompletenessFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(completeness);
    
    result = parser.parseCompleteness(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  @Test
  public void testLeftCompleteness() throws IOException {
    TestCompletenessParser parser = new TestCompletenessParser();
    Optional<ASTCompleteness> result = parser.parseCompleteness(new StringReader("(c,...)"));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    ASTCompleteness completeness = result.get();
    
    CompletenessFullPrettyPrinter prettyPrinter = new CompletenessFullPrettyPrinter(new IndentPrinter());
    String output = prettyPrinter.prettyprint(completeness);
    
    result = parser.parseCompleteness(new StringReader(output));
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    
    assertTrue(completeness.deepEquals(result.get()));
  
    assertTrue(Log.getFindings().isEmpty());
  }
}
