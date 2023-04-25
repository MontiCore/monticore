/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpression;
import de.monticore.expressions.lambdaexpressions._prettyprint.LambdaExpressionsFullPrettyPrinter;
import de.monticore.expressions.testlambdaexpressions._parser.TestLambdaExpressionsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LambdaExpressionsPrettyPrinterTest {

  protected TestLambdaExpressionsParser parser = new TestLambdaExpressionsParser();

  protected LambdaExpressionsFullPrettyPrinter prettyPrinter =
      new LambdaExpressionsFullPrettyPrinter(new IndentPrinter());
  
  @Before
  public void initLog() {
    LogStub.init();
    Log.enableFailQuick(false);
  }
  
  @Before
  public void init() {
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testLambdaWithoutParameter() throws IOException {
    testLambdaExpression("() -> a");
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLambdaWithoutTypeWithoutParenthesis() throws IOException {
    testLambdaExpression("a -> a");
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLambdaWithoutTypeWithParenthesis() throws IOException {
    testLambdaExpression("(a) -> a");
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLambdaWithType() throws IOException {
    ASTLambdaExpression ast = parseLambdaExpression("(int a) -> a");
    String output = prettyPrinter.prettyprint(ast);
    // does not print 'int' because functionality for type printing has to be added
    // over delegation from prettyprinter of language that fills the external
    // pattern matches e.g. "(name a) -> a" and "( a)->a"
    Pattern pattern = Pattern.compile(""
        // "("
        + "\\(\\p{Space}*"
        // "name " or " "
        + "\\p{Alpha}*\\p{Space}+"
        // "a"
        + "a\\p{Space}*"
        // ")"
        + "\\)\\p{Space}*"
        // "->"
        + "->\\p{Space}*"
        // "a"
        + "a"
    );
    assertTrue(pattern.asPredicate().test(output));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLambdaMultipeParametersWithoutType() throws IOException {
    testLambdaExpression("(a, b) -> a");
  
    assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLambdaMultipeParametersWithType() throws IOException {
    ASTLambdaExpression ast = parseLambdaExpression("(int a, int b) -> a");
    String output = prettyPrinter.prettyprint(ast);
    // does not print 'int' because functionality for type printing has to be added
    // over delegation from prettyprinter of language that fills the external
    // pattern matches e.g. "(name a, name2 b) -> a" and "( a, b)->a"
    Pattern pattern = Pattern.compile(""
        // "("
        + "\\(\\p{Space}*"
        // "name " or " "
        + "\\p{Alpha}*\\p{Space}+"
        // "a"
        + "a\\p{Space}*"
        // ","
        + ",\\p{Space}*"
        // "name2 " or " "
        + "\\p{Alpha}*\\p{Space}+"
        // "b"
        + "b\\p{Space}*"
        // ")"
        + "\\)\\p{Space}*"
        // "->"
        + "->\\p{Space}*"
        // "a"
        + "a"
    );
    assertTrue(pattern.asPredicate().test(output));
  
    assertTrue(Log.getFindings().isEmpty());
  }
  
  public void testLambdaExpression(String exp) throws IOException {
    ASTLambdaExpression ast = parseLambdaExpression(exp);
    String output = prettyPrinter.prettyprint(ast);
    ASTLambdaExpression ast2 = parseLambdaExpression(output);
    assertTrue("Parse equals: " + exp + " vs " + output, ast.deepEquals(ast2));
  
    assertTrue(Log.getFindings().isEmpty());
  }

  public ASTLambdaExpression parseLambdaExpression(String exp) throws IOException {
    Optional<ASTLambdaExpression> result = parser.parse_StringLambdaExpression(exp);
    assertFalse(parser.hasErrors());
    assertTrue(result.isPresent());
    return result.get();
  }

}
