// (c) https://github.com/MontiCore/monticore
package de.monticore.interpreter;

import de.monticore.expressions.assignmentexpressions.interpreter.AssignmentExpressionsInterpreter;
import de.monticore.expressions.bitexpressions.interpreter.BitExpressionsInterpreter;
import de.monticore.expressions.commonexpressions.interpreter.CommonExpressionsInterpreter;
import de.monticore.expressions.expressionsbasis.interpreter.ExpressionCalculationLogVisitor;
import de.monticore.expressions.expressionsbasis.interpreter.ExpressionsBasisInterpreter;
import de.monticore.expressions.lambdaexpressions.interpreter.LambdaExpressionsInterpreter;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.util.InterpreterAccess4Tests;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.literals.mccommonliterals.interpreter.MCCommonLiteralsInterpreter;
import de.monticore.ocl.optionaloperators.interpreter.OptionalOperatorsInterpreter;
import de.monticore.ocl.setexpressions.interpreter.SetExpressionsInterpreter;
import de.monticore.runtime.junit.AbstractMCTest;
import de.monticore.statements.mcassertstatements.interpreter.MCAssertStatementsInterpreter;
import de.monticore.statements.mccommonstatements.interpreter.MCCommonStatementsInterpreter;
import de.monticore.statements.mclowlevelstatements.interpreter.MCLowLevelStatementsInterpreter;
import de.monticore.statements.mcvardeclarationstatements.interpreter.MCVarDeclarationStatementsInterpreter;
import de.monticore.symbols.util.Class2MCTestUtil;
import de.monticore.tests.expressionsandstatements.ExpressionsAndStatementsMill;
import de.monticore.tests.expressionsandstatements.ExpressionsAndStatementsUtil;
import de.monticore.tests.expressionsandstatements._ast.ASTBehaviorInput;
import de.monticore.tests.expressionsandstatements._visitor.ExpressionsAndStatementsTraverser;
import de.monticore.tests.expressionsandstatements.interpreter.ExpressionsAndStatementsInterpreter;
import de.monticore.values.MCValue;
import de.se_rwth.commons.logging.LogStub;
import org.junit.jupiter.api.BeforeEach;

import static de.monticore.runtime.junit.MCAssertions.assertNoFindings;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public abstract class AbstractInterpreterTest extends AbstractMCTest {

  protected InterpreterAccess4Tests interpreter;

  @BeforeEach
  public void setup() {
    LogStub.initPlusLog();
    ExpressionsAndStatementsUtil.init();
    Class2MCTestUtil.initializeClass2MC4OOSymbols();
    interpreter = initializeInterpreter();
  }

  protected InterpreterAccess4Tests initializeInterpreter() {
    InterpreterDataForBasicSymbols iData = new InterpreterDataForBasicSymbols();
    ExpressionsAndStatementsTraverser traverser = ExpressionsAndStatementsMill.inheritanceTraverser();
    traverser.setExpressionsBasisHandler(new ExpressionsBasisInterpreter(iData));
    traverser.setCommonExpressionsHandler(new CommonExpressionsInterpreter(iData));
    traverser.setAssignmentExpressionsHandler(new AssignmentExpressionsInterpreter(iData));
    traverser.setMCCommonLiteralsHandler(new MCCommonLiteralsInterpreter(iData));
    traverser.setSetExpressionsHandler(new SetExpressionsInterpreter(iData));
    traverser.setBitExpressionsHandler(new BitExpressionsInterpreter(iData));
    traverser.setLambdaExpressionsHandler(new LambdaExpressionsInterpreter(iData));
    traverser.setOptionalOperatorsHandler(new OptionalOperatorsInterpreter(iData));
    traverser.setMCAssertStatementsHandler(new MCAssertStatementsInterpreter(iData));
    traverser.setMCCommonStatementsHandler(new MCCommonStatementsInterpreter(iData));
    traverser.setMCLowLevelStatementsHandler(new MCLowLevelStatementsInterpreter(iData));
    traverser.setMCVarDeclarationStatementsHandler(new MCVarDeclarationStatementsInterpreter(iData));
    traverser.setExpressionsAndStatementsHandler(new ExpressionsAndStatementsInterpreter(iData));
    InterpreterAccess4Tests access =
        new InterpreterAccess4Tests(traverser, iData);
    return access;
  }

  protected InterpreterAccess4Tests initializeInterpreterWithLog() {
    InterpreterAccess4Tests access = initializeInterpreter();
    ExpressionsAndStatementsTraverser traverser =
        (ExpressionsAndStatementsTraverser) access.getTraverser();
    traverser.add4ExpressionsBasis(
        new ExpressionCalculationLogVisitor(access.getInterpreterData())
    );
    return access;
  }

  /**
   * Executes the model and compares the result with the expected value.
   *
   * @param modelStr      the model to execute
   * @param expectedValue the expected value
   */
  protected void checkValue(String modelStr, Object expectedValue) {
    MCValue value = interpret(modelStr);
    // handle numbers, as they are represented differently in the interpreter
    try {
      if (expectedValue instanceof Byte) {
        assertEquals(expectedValue, value.asByte());
      }
      else if (expectedValue instanceof Short) {
        assertEquals(expectedValue, value.asShort());
      }
      else if (expectedValue instanceof Character) {
        assertEquals(expectedValue, value.asChar());
      }
      else if (expectedValue instanceof Integer) {
        assertEquals(expectedValue, value.asInt());
      }
      else if (expectedValue instanceof Long) {
        assertEquals(expectedValue, value.asLong());
      }
      else if (expectedValue instanceof Float) {
        assertEquals(expectedValue, value.asFloat());
      }
      else if (expectedValue instanceof Double) {
        assertEquals(expectedValue, value.asDouble());
      }
      else {
        assertEquals(expectedValue, value.asNativeObject());
      }
    }
    catch (RuntimeException e) {
      System.out.println(
          "Exception while comparing." + System.lineSeparator()
              + "Type: " + value.printType() + System.lineSeparator()
              + "Value: " + value.printValue() + System.lineSeparator()
              + "Expected: " + expectedValue + System.lineSeparator()
              + "Model:" + System.lineSeparator() + modelStr
      );
      fail(e);
    }
  }

  // helper

  MICalculation getCalculation(
      ASTBehaviorInput ast
  ) {
    MICalculation calculation = interpreter.getCalculation(ast);
    assertNoFindings();
    assertNotNull(calculation);
    InterpreterDataForBasicSymbols iData = interpreter.getInterpreterData();
    assertEquals(0, iData.getFrameLayoutStack().size());
    assertTrue(!iData.isPresentCalculation());
    return calculation;
  }

  protected MCValue interpret(String modelStr) {
    ASTBehaviorInput ast =
        ExpressionsAndStatementsUtil.getPreparedAST(modelStr);
    // explicitly get the calculation to check if there are errors
    getCalculation(ast);
    MCValue value = interpreter.interpretNode(ast);
    assertNoFindings();
    assertNotNull(value);
    return value;
  }

  protected <T> T interpretAndCast(String modelStr) {
    MCValue value = interpret(modelStr);
    Object valueObj = value.asNativeObject();
    @SuppressWarnings("unchecked")
    T casted = (T) valueObj;
    return casted;
  }

}
