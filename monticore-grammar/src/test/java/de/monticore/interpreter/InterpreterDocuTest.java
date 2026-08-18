// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import de.monticore.interpreter.util.InterpreterAccess4Tests;
import de.monticore.statements.interpreter.StatementsInterpreter;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCBlockStatement;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.monticore.tests.expressionsandstatements.TestExpressionsAndStatementsTool;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.values.MCValueInt;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests the example in the interpreter docu.
 * This is a direct copy-paste to ensure correctness.
 */
public class InterpreterDocuTest extends AbstractInterpreterTest {

  protected TestExpressionsAndStatementsTool tool =
      new TestExpressionsAndStatementsTool();

  @Test
  public void testDocu1() {
    ////////////////////// START COPY-PASTE //////////////////////
    // add Symbol "j" to the symbol table, e.g., in the global scope
    VariableSymbol jSymbol = BasicSymbolsMill.variableSymbolBuilder()
        .setName("j")
        .setType(SymTypeExpressionFactory.createPrimitive("int"))
        .build();
    BasicSymbolsMill.globalScope().add(jSymbol);

    // initialize an interpreter
    ASTMCBlockStatement stmt =
        parseAndCreateSymTabAndRunCoCos("int i = j++;");
    StatementsInterpreter interpreter = getMyLangInterpreter();

    // add the variable to the interpreters current scope
    interpreter.addVariable(jSymbol, new MCValueInt(2));
    interpreter.interpret(stmt);

    // resolve for "i" in the model's scope
    VariableSymbol iSymbol = BasicSymbolsMill.globalScope()
        .resolveVariable("i").get();
    int i = interpreter.getVariable(iSymbol).asInt(); // i == 2
    int j = interpreter.getVariable(jSymbol).asInt(); // j == 3, due to ++

    // j is already in the interpreter's scope, use 'set' to set a new value
    interpreter.setVariable(jSymbol, new MCValueInt(4));
    ////////////////////// END COPY-PASTE //////////////////////

    assertEquals(2, i);
    assertEquals(3, j);
    j = interpreter.getVariable(jSymbol).asInt();
    assertEquals(4, j);
  }

  protected ASTMCBlockStatement parseAndCreateSymTabAndRunCoCos(String code) {
    return tool.getASTWithSymbolTable(code).getMCBlockStatement(0);
  }

  protected StatementsInterpreter getMyLangInterpreter() {
    InterpreterAccess4Tests interpreter = tool.initializeInterpreter();
    return new StatementsInterpreter(
        interpreter.getTraverser(),
        interpreter.getInterpreterData()
    );
  }

}
