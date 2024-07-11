// (c) https://github.com/MontiCore/monticore
package de.monticore.expressions.lambdaexpressions._symboltable;

import de.monticore.expressions.lambdaexpressions._ast.ASTLambdaParameter;
import de.monticore.expressions.lambdaexpressions._visitor.LambdaExpressionsVisitor2;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types3.Type4Ast;
import de.monticore.visitor.ITraverser;

public class LambdaExpressionsSTCompleteTypes2 implements LambdaExpressionsVisitor2 {

  // to be moved into the mill after details are discussed
  @Deprecated
  private Type4Ast type4Ast = null;

  // the traverser filling the type map
  // not required, if the map is already filled
  protected ITraverser typeTraverser;

  protected Type4Ast getTypeMap() {
    return type4Ast;
  }

  protected ITraverser getTypeTraverser() {
    return typeTraverser;
  }

  // after moving the typemap, the other constructor ought to be used
  @Deprecated
  public LambdaExpressionsSTCompleteTypes2(ITraverser typeTraverser, Type4Ast type4Ast) {
    this(typeTraverser);
    this.type4Ast = type4Ast;
  }

  public LambdaExpressionsSTCompleteTypes2(ITraverser typeTraverser) {
    this.typeTraverser = typeTraverser;
  }

  @Override
  public void endVisit(ASTLambdaParameter ast) {
    if (ast.isPresentMCType()) {
      ast.getSymbol().setType(calculateType(ast.getMCType()));
    }
    // else: currently, the typecheck will set the type later.
    // Example:
    // int->int f = x->(y->y+2)(x);
    // Here, x and y have type int, however, this requires the calculations
    // found in the typecheck (especially for the Symbol y)
  }

  // Helper

  protected SymTypeExpression calculateType(ASTMCType mcType) {
    if (!getTypeMap().hasTypeOfTypeIdentifier(mcType)) {
      mcType.accept(getTypeTraverser());
    }
    return getTypeMap().getTypeOfTypeIdentifier(mcType);
  }

}
