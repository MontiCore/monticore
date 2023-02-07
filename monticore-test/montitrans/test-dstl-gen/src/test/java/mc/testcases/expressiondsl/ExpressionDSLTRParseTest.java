/* (c) https://github.com/MontiCore/monticore */
package mc.testcases.expressiondsl;

import de.monticore.expressions.tr.expressionsbasistr._ast.ASTITFExpression;
import de.monticore.expressions.tr.expressionsbasistr._ast.ASTITFLiteralExpression;
import de.se_rwth.commons.logging.Log;
import mc.testcases.tr.expressiondsltr.ExpressionDSLTRMill;
import mc.testcases.tr.expressiondsltr._parser.ExpressionDSLTRParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

/**
 * Test for literal support in left recursive grammars (aka expressions)
 */
public class ExpressionDSLTRParseTest {
  @Before
  public void beforeEach() {
    Log.clearFindings();
  }

  @BeforeClass
  public static void beforeClass() {
    Log.init();
    Log.enableFailQuick(false);
    ExpressionDSLTRMill.init();
  }

  @Test
  public void testITFExpressions() throws IOException {
    // Test if various literals can be parsed
    testITFExpression("a=b");
    testITFExpression("a=\"string\"");
    testITFExpression("a=\'c\'");
    testITFExpression("a=false");
    testITFExpression("a=true");
    testITFExpression("a=null");
    testITFExpression("a=12");
  }

  protected void testITFExpression(String exp) throws IOException {
    ExpressionDSLTRParser parser = ExpressionDSLTRMill.parser();
    Optional<ASTITFExpression> expressionOptional = parser.parse_StringITFExpression(exp);
    Assert.assertFalse("Parser error while parsing: " + exp, parser.hasErrors());
    Assert.assertTrue("Failed to parse: " + exp, expressionOptional.isPresent());
  }
}
