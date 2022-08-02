// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.lambdaexpressions._symboltable;

import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaParameter;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsVisitor2;
import de.monticore.types.check.ISynthesize;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types.mcbasictypes._ast.ASTMCType;

public class LambdaExpressionsSTCompleteTypes implements LambdaExpressionsVisitor2 {

  ISynthesize synthesize;

  public LambdaExpressionsSTCompleteTypes(ISynthesize synthesize) {
    this.synthesize = synthesize;
  }

  protected ISynthesize getSynthesize() {
    return synthesize;
  }

  @Override
  public void endVisit(ASTLambdaParameter ast) {
    if (ast.isPresentMCType()) {
      ast.getSymbol().setType(createTypeLoader(ast.getMCType()));
    }
  }

  protected SymTypeExpression createTypeLoader(ASTMCType ast) {
    TypeCheckResult typeCheckResult = getSynthesize().synthesizeType(ast);
    if (typeCheckResult.isPresentResult()) {
      return typeCheckResult.getResult();
    }
    return new SymTypeOfNull();
  }

}
