/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.prettyprint;

import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaExpression;
import de.monticore.expressions.lambdaexpressions._prettyprint.LambdaExpressionsFullPrettyPrinter;
import de.monticore.expressions.testlambdaexpressions.TestLambdaExpressionsMill;
import de.monticore.expressions.testlambdaexpressions._parser.TestLambdaExpressionsParser;
import de.monticore.prettyprint.IndentPrinter;
import de.se_rwth.commons.logging.Log;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LambdaExpressionsPrettyPrinterTest {

  protected TestLambdaExpressionsParser parser;
  protected LambdaExpressionsFullPrettyPrinter prettyPrinter;
  
  @BeforeEach
  public void init() {
    LogStub.init();
    Log.enableFailQuick(false);
    TestLambdaExpressionsMill.reset();
    TestLambdaExpressionsMill.init();
    parser = new TestLambdaExpressionsParser();
    prettyPrinter =
            new LambdaExpressionsFullPrettyPrinter(new IndentPrinter());
    prettyPrinter.getPrinter().clearBuffer();
  }

  @Test
  public void testLambdaWithoutParameter() throws IOException {
    testLambdaExpression("() -> a");
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLambdaWithoutTypeWithoutParenthesis() throws IOException {
    testLambdaExpression("a -> a");
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLambdaWithoutTypeWithParenthesis() throws IOException {
    testLambdaExpression("(a) -> a");
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
    Assertions.assertTrue(pattern.asPredicate().test(output));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  @Test
  public void testLambdaMultipeParametersWithoutType() throws IOException {
    testLambdaExpression("(a, b) -> a");
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
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
    Assertions.assertTrue(pattern.asPredicate().test(output));
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }
  
  public void testLambdaExpression(String exp) throws IOException {
    ASTLambdaExpression ast = parseLambdaExpression(exp);
    String output = prettyPrinter.prettyprint(ast);
    ASTLambdaExpression ast2 = parseLambdaExpression(output);
    Assertions.assertTrue(ast.deepEquals(ast2), "Parse equals: " + exp + " vs " + output);
  
    Assertions.assertTrue(Log.getFindings().isEmpty());
  }

  public ASTLambdaExpression parseLambdaExpression(String exp) throws IOException {
    Optional<ASTLambdaExpression> result = parser.parse_StringLambdaExpression(exp);
    Assertions.assertFalse(parser.hasErrors());
    Assertions.assertTrue(result.isPresent());
    return result.get();
  }

}
