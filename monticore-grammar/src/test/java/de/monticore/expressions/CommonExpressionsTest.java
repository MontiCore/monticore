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
  String[] tests = { "fun(a,b)" , "a.fun(j,b)", "A.fun(b,b)","fun2(g).fun(h)","j.fun2(g).fun(h)","A.k.fun2(g).fun(h)"};
  Optional<ASTExpression> o = p.parse_StringExpression("fun(a,b)");
  System.out.println("jj");
}






}
