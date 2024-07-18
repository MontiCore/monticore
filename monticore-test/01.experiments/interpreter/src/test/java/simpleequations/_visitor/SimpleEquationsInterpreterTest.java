/* (c) https://github.com/MontiCore/monticore */
package simpleequations._visitor;

import de.monticore.interpreter.Value;
import org.junit.Test;
import simpleequations.SimpleEquationsMill;
import simpleequations._ast.ASTProgram;
import simpleequations._parser.SimpleEquationsParser;
import simpleequations._symboltable.SimpleEquationsScopesGenitorDelegator;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SimpleEquationsInterpreterTest {

  @Test
  public void test() throws IOException {
    SimpleEquationsMill.init();
    SimpleEquationsParser parser = SimpleEquationsMill.parser();
    SimpleEquationsInterpreter interpreter = new SimpleEquationsInterpreter();
    SimpleEquationsScopesGenitorDelegator delegator = SimpleEquationsMill.scopesGenitorDelegator();

    ASTProgram program = parser.parse_StringProgram("var a=3.5; var b=4; print(a); var c=a+b; c;").get();

    delegator.createFromAST(program);
    Value result = interpreter.interpret(program);

    assertTrue(result.isFloat());
    assertEquals(result.asFloat(), 7.5f, 0.0001f);

    SimpleEquationsMill.reset();
    SimpleEquationsMill.init();
    interpreter = new SimpleEquationsInterpreter();
    program = parser.parse_StringProgram(
        "var a = 40; " +
        "a = 45;" +
            "a;").get();

    delegator.createFromAST(program);
    result = interpreter.interpret(program);

    assertTrue(result.isInt());
    assertEquals(result.asInt(), 45);
  }

}
