/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.combineexpressionswithliterals._visitor;

import de.monticore.expressions.combineexpressionswithliterals._ast.ASTFoo;
import de.monticore.interpreter.ModelInterpreter;
import de.monticore.interpreter.Value;

public class CombineExpressionsWithLiteralsInterpreter extends CombineExpressionsWithLiteralsInterpreterTOP {
  public  CombineExpressionsWithLiteralsInterpreter() {
    super();
  }

  public  CombineExpressionsWithLiteralsInterpreter(ICombineExpressionsWithLiteralsContext context, ModelInterpreter realThis) {
    super(context, realThis);
  }

  @Override
  public Value interpret(ASTFoo node) {
    return  node.getExpression().evaluate(getRealThis());
  }

}
