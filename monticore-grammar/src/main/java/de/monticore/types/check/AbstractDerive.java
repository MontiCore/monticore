/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.visitor.ITraverser;

public abstract class AbstractDerive {

  protected ITraverser traverser;
  protected TypeCheckResult typeCheckResult;

  protected ITraverser getTraverser() {
    return traverser;
  }

  protected TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }

  public AbstractDerive(ITraverser traverser) {
    this.typeCheckResult = new TypeCheckResult();
    this.traverser = traverser;
  }

  public TypeCheckResult deriveType(ASTExpression expr) {
    this.getTypeCheckResult().reset();
    expr.accept(this.getTraverser());
    return this.getTypeCheckResult().copy();
  }

  public TypeCheckResult deriveType(ASTLiteral lit) {
    this.getTypeCheckResult().reset();
    lit.accept(this.getTraverser());
    return this.getTypeCheckResult().copy();
  }
}
