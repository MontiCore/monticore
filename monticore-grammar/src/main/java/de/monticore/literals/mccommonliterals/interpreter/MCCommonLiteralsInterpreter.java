// (c) https://github.com/MontiCore/monticore
package de.monticore.literals.mccommonliterals.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculationBoolean;
import de.monticore.interpreter.calculations.MICalculationDouble;
import de.monticore.interpreter.calculations.MICalculationInt;
import de.monticore.interpreter.calculations.MICalculationValue;
import de.monticore.interpreter.util.InterpreterDataForBasicSymbols;
import de.monticore.values.MCValueObject;
import de.monticore.literals.mccommonliterals._ast.ASTBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBasicLongLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTBooleanLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTCharLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicDoubleLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicFloatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedBasicLongLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.literals.mccommonliterals._visitor.MCCommonLiteralsInheritanceHandler;
import de.se_rwth.commons.logging.Log;

/**
 * Interpreter Visitor for MCCommonLiterals
 */
public class MCCommonLiteralsInterpreter
    extends MCCommonLiteralsInheritanceHandler {

  protected InterpreterDataForBasicSymbols iData;

  public MCCommonLiteralsInterpreter(InterpreterDataForBasicSymbols iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTBooleanLiteral node) {
    final boolean value = node.getValue();
    iData.putCalculation((MICalculationBoolean) frame -> value);
  }

  @Override
  public void traverse(ASTCharLiteral node) {
    final char value = node.getValue();
    iData.putCalculation((MICalculationInt) frame -> value);
  }

  @Override
  public void traverse(ASTStringLiteral node) {
    final String value = node.getValue();
    // reuse value, as Strings are final
    final MCValueObject miValue = new MCValueObject(value);
    iData.putCalculation((MICalculationValue) frame -> miValue);
  }

  @Override
  public void traverse(ASTNatLiteral node) {
    final int value = node.getValue();
    iData.putCalculation((MICalculationInt) frame -> value);
  }

  @Override
  public void traverse(ASTSignedNatLiteral node) {
    final int value = node.getValue();
    iData.putCalculation((MICalculationInt) frame -> value);
  }

  @Override
  public void traverse(ASTBasicLongLiteral node) {
    final long value = node.getValue();
    if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
      Log.warn("0xF1003 value is currently not supported: " + value
              + System.lineSeparator() + " Currently, only values in the range of "
              + Integer.MIN_VALUE + " to " + Integer.MAX_VALUE + " are supported.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
    final int intValue = (int) value;
    iData.putCalculation((MICalculationInt) frame -> intValue);
  }

  @Override
  public void traverse(ASTSignedBasicLongLiteral node) {
    final long value = node.getValue();
    if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
      Log.warn("0xF1004 value is currently not supported: " + value
              + System.lineSeparator() + " Currently, only values in the range of "
              + Integer.MIN_VALUE + " to " + Integer.MAX_VALUE + " are supported.",
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
    final int intValue = (int) value;
    iData.putCalculation((MICalculationInt) frame -> intValue);
  }

  @Override
  public void traverse(ASTBasicFloatLiteral node) {
    final float value = node.getValue();
    iData.putCalculation((MICalculationDouble) frame -> value);
  }

  @Override
  public void traverse(ASTSignedBasicFloatLiteral node) {
    final float value = node.getValue();
    iData.putCalculation((MICalculationDouble) frame -> value);
  }

  @Override
  public void traverse(ASTBasicDoubleLiteral node) {
    final double value = node.getValue();
    iData.putCalculation((MICalculationDouble) frame -> value);
  }

  @Override
  public void traverse(ASTSignedBasicDoubleLiteral node) {
    final double value = node.getValue();
    iData.putCalculation((MICalculationDouble) frame -> value);
  }

}
