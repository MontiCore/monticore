package de.monticore.ocl.oclexpressions._visitor;

import de.monticore.interpreter.IModelInterpreter;
import de.monticore.interpreter.MIValue;
import de.monticore.interpreter.values.ErrorMIValue;
import de.monticore.ocl.oclexpressions._ast.ASTIfThenElseExpression;
import de.monticore.symbols.basicsymbols.BasicSymbolsMill;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

public class OCLExpressionsInterpreter extends OCLExpressionsInterpreterTOP {
  
  public OCLExpressionsInterpreter(IModelInterpreter realThis) {
    super(realThis);
  }
  
  public OCLExpressionsInterpreter() {
    super();
  }
  
  @Override
  public MIValue interpret(ASTIfThenElseExpression expr) {
    SymTypeExpression condType = TypeCheck3.typeOf(expr.getCondition());
    if (!(condType.isPrimitive() && condType.asPrimitive().getPrimitiveName().equals(BasicSymbolsMill.BOOLEAN))) {
      String errorMsg = "0x57074 The condition of the IfThenElseExpression was expected to be a boolean but was a " + condType.print() + ".";
      Log.error(errorMsg);
      return new ErrorMIValue(errorMsg);
    }
    
    MIValue conditionValue = expr.getCondition().evaluate(getRealThis());
    if (conditionValue.isFlowControlSignal()) return conditionValue;
    
    if (conditionValue.asBoolean()) {
      return expr.getThenExpression().evaluate(getRealThis());
    } else {
      return expr.getElseExpression().evaluate(getRealThis());
    }
  }

}
