/* (c) https://github.com/MontiCore/monticore */
package simpleequations._visitor;

import de.monticore.interpreter.MIValue;
import org.junit.jupiter.api.Test;
import simpleequations.SimpleEquationsMill;
import simpleequations._ast.ASTProgram;
import simpleequations._parser.SimpleEquationsParser;
import simpleequations._symboltable.SimpleEquationsScopesGenitorDelegator;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleEquationsInterpreterTest {

  @Test
  public void test() throws IOException {
    SimpleEquationsMill.init();
    SimpleEquationsParser parser = SimpleEquationsMill.parser();
    SimpleEquationsInterpreter interpreter = new SimpleEquationsInterpreter();
    SimpleEquationsScopesGenitorDelegator delegator = SimpleEquationsMill.scopesGenitorDelegator();

    ASTProgram function = parser.parse_StringProgram("" +
        "var a = 3; " +
        "var b = 3; " +
        "func func1(a, b){ " +
        " var c = a; " +
        " if( b > 0 ){ " +
        "   b = b - 1; " +
        "   c = a * 3; " +
        "   var d = func1(c, b);" +
        "   return d;" +
        " } else { " +
        "   return a;" +
        " };" +
        "}"+
        "var result = func1(a, b);" +
        "print(result);").get();
    delegator.createFromAST(function);
    MIValue functionResult = interpreter.interpret(function);
    assertTrue(functionResult.isInt());
    assertEquals(functionResult.asInt(), 81);


    ASTProgram program = parser.parse_StringProgram("var a=3.5; var b=4; print(a); var c=a+b; c;").get();

    delegator.createFromAST(program);
    MIValue result = interpreter.interpret(program);

    assertTrue(result.isFloat());
    assertEquals(7.5f, result.asFloat(), 0.0001f);

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
