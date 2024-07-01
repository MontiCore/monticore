/* (c) https://github.com/MontiCore/monticore */
package simpleequations._visitor;

import de.monticore.interpreter.Value;
import org.junit.Test;
import simpleequations.SimpleEquationsMill;
import simpleequations._ast.ASTExpression;
import simpleequations._parser.SimpleEquationsParser;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleEquationsInterpreterTest {

  @Test
  public void test() throws IOException {
    SimpleEquationsMill.init();
    SimpleEquationsParser parser = SimpleEquationsMill.parser();
    SimpleEquationsInterpreter interpreter = new SimpleEquationsInterpreter();

    final Optional<ASTExpression> variableAss = parser.parse_String("a=3.5");
    assertTrue(variableAss.isPresent());
    final ASTExpression variable = variableAss.get();

    SimpleEquationsMill.scopesGenitorDelegator().createFromAST(variable);

    interpreter.interpret(variable);

    final Optional<ASTExpression> optPlusEq = parser.parse_String("a+2.4");
    assertTrue(optPlusEq.isPresent());
    final ASTExpression plusEq = optPlusEq.get();

    Value result = interpreter.interpret(plusEq);
    assertTrue(result.isFloat());
    assertEquals(result.asFloat(), 5.9f);

    final Optional<ASTExpression> optPlusEq1 = parser.parse_String("5+5");
    assertTrue(optPlusEq1.isPresent());
    final ASTExpression plusEq1 = optPlusEq1.get();

    result = interpreter.interpret(plusEq1);

    assertTrue(result.isInt());
    assertEquals(result.asInt(), 10);
  }

}
