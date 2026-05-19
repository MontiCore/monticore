// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mcvardeclarationstatements.interpreter;

import com.google.common.base.Preconditions;
import de.monticore.interpreter.calculations.MICalculation;
import de.monticore.interpreter.calculations.MICalculationVoid;
import de.monticore.interpreter.util.InterpreterData;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._visitor.MCVarDeclarationStatementsInheritanceHandler;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;

/**
 * Interpreter Visitor for MCVarDeclarationStatements
 */
public class MCVarDeclarationStatementsInterpreter
    extends MCVarDeclarationStatementsInheritanceHandler {

  protected InterpreterData iData;

  public MCVarDeclarationStatementsInterpreter(InterpreterData iData) {
    this.iData = Preconditions.checkNotNull(iData);
  }

  @Override
  public void traverse(ASTVariableDeclarator node) {
    FieldSymbol fieldSym = node.getDeclarator().getSymbol();
    iData.getFrameLayoutStack().peek().declareVariable(fieldSym);
    MICalculationVoid initCalc;
    if (node.isPresentVariableInit()) {
      node.getVariableInit().accept(getTraverser());
      MICalculation valueCalc = iData.popCalculation();
      initCalc = iData.getFrameLayoutStack().peek()
          .getCalcAndStore(fieldSym, valueCalc);
    }
    else {
      initCalc = MICalculationVoid.NOOP_CALC;
    }
    iData.putCalculation(initCalc);
  }

  @Override
  public void traverse(ASTLocalVariableDeclaration node) {
    MICalculationVoid chainedCalc = MICalculationVoid.NOOP_CALC;
    for (ASTVariableDeclarator declarator : node.getVariableDeclaratorList()) {
      declarator.accept(getTraverser());
      MICalculationVoid declCalc = iData.popCalculation()
          .asCalculationVoid();
      chainedCalc = chainedCalc.getChainedBefore(declCalc);
    }
    iData.putCalculation(chainedCalc);
  }

}

