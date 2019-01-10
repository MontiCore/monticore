package de.monticore.expressions;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;

import de.monticore.expressions.testcommonexpressions._parser.TestCommonExpressionsParser;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class CommonExpressionsTest {

@Test
  public void parseTest() throws IOException {
  TestCommonExpressionsParser p = new TestCommonExpressionsParser();
  Optional<ASTExpression> o = p.parse_StringExpression("a.fun(a,b)");
  System.out.println("jj");

}
}
