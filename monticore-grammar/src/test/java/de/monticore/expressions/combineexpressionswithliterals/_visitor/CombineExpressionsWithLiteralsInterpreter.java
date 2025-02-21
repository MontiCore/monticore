/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.combineexpressionswithliterals._visitor;

import de.monticore.expressions.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.MIValue;

public class CombineExpressionsWithLiteralsInterpreter extends CombineExpressionsWithLiteralsInterpreterTOP {
  public  CombineExpressionsWithLiteralsInterpreter() {
    super();
  }

  public  CombineExpressionsWithLiteralsInterpreter(ModelInterpreter realThis) {
    super(realThis);
  }

  @Override
  public MIValue interpret(ASTFoo node) {
    return node.getExpression().evaluate(getRealThis());
  }

}
