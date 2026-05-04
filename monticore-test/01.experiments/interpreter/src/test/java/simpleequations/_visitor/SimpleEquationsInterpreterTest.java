/* (c) https://github.com/MontiCore/monticore */
package simpleequations._visitor;

import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.MIValueFactory;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpressionFactory;
import numerals.NumeralsMill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import simpleequations.SimpleEquationsMill;
import simpleequations._ast.ASTSimpleEquationCompilationUnit;
import simpleequations._parser.SimpleEquationsParser;
import simpleequations._symboltable.SimpleEquationsScopesGenitorDelegator;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimpleEquationsInterpreterTest {

  protected SimpleEquationsParser parser = SimpleEquationsMill.parser();
  protected SimpleEquationsScopesGenitorDelegator delegator = SimpleEquationsMill.scopesGenitorDelegator();
  protected SimpleEquationsInterpreter interpreter = new SimpleEquationsInterpreter();


  @BeforeEach
  public void setUp() {
    NumeralsMill.reset();
    NumeralsMill.init();
    BasicSymbolsMill.reset();
    BasicSymbolsMill.init();
    SimpleEquationsMill.reset();
    SimpleEquationsMill.init();
    BasicSymbolsMill.initializePrimitives();

    parser = SimpleEquationsMill.parser();
    delegator = SimpleEquationsMill.scopesGenitorDelegator();
    interpreter = new SimpleEquationsInterpreter();
  }

  @Test
  public void test() throws IOException {
    ASTSimpleEquationCompilationUnit program = parser.parse_StringSimpleEquationCompilationUnit("" +
        "Program {" +
        "  int a = 3; " +
        "  int b = 3; " +
        "  int func func1(int a, int b){ " +
        "    int c = a; " +
        "    if( b > 0 ){ " +
        "      b = b - 1; " +
        "      a = a;" +
        "      c = a * 3; " +
        "      int d = func1(c, b);" +
        "      return d;" +
        "    } else { " +
        "      return a;" +
        "    };" +
        "  };" +
        "  int result = func1(a, b);" +
        "  print(result);" +
        "}").get();
    delegator.createFromAST(program);
    MIValue functionResult = interpreter.interpret(program);

    assertTrue(functionResult.isInt());
    assertEquals(81, functionResult.asInt());
  }
  @Test
  public void testRecursive() throws IOException {
    //test recursive method definition
    ASTSimpleEquationCompilationUnit program = parser.parse_StringSimpleEquationCompilationUnit("" +
        "Program {" +
        "  int a = 3;" +
        "  int b = 3;" +
        "  int func func1(int a, int  b){" +
        "    int func func1(int a, int b) {" +
        "      return a + b;" +
        "    };" +
        "    a = a * a;" +
        "    b = b * b;" +
        "    return func1(a,b);" +
        "  };" +
        "  int result = func1(a,b); " +
        "  print(result);" +
        "}").get();
    delegator.createFromAST(program);
    MIValue recursiveResult = interpreter.interpret(program);

    assertTrue(recursiveResult.isInt());
    assertEquals(18, recursiveResult.asInt());
  }

  @Test
  public void testDefaultIsFloat() throws IOException {
    ASTSimpleEquationCompilationUnit program = parser.parse_StringSimpleEquationCompilationUnit("" +
        "Program {" +
        "  var a=3.5; " +
        "  var b=4; " +
        "  print(a); " +
        "  var c=a+b; " +
        "  c;" +
        "}").get();

    delegator.createFromAST(program);
    MIValue result = interpreter.interpret(program);

    assertTrue(result.isFloat());
    assertEquals(7.5f, result.asFloat(), 0.0001f);
  }

  @Test
  public void testAssignment() throws IOException {
    ASTSimpleEquationCompilationUnit program = parser.parse_StringSimpleEquationCompilationUnit(
        "Program {" +
        "  var a = 40; " +
        "  a = 45;" +
        "  a;" +
        "}").get();

    delegator.createFromAST(program);
    MIValue result = interpreter.interpret(program);
    assertTrue(result.isInt());
    assertEquals(45, result.asInt());
  }
}
