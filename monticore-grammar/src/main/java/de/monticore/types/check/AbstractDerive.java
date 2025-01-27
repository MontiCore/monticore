/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.visitor.ITraverser;

/**
 * @deprecated use {@link de.monticore.types3.TypeCheck3}
 */
@Deprecated(forRemoval = true)
public abstract class AbstractDerive implements IDerive {

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

  @Override
  public TypeCheckResult deriveType(ASTExpression expr) {
    this.getTypeCheckResult().reset();
    expr.accept(this.getTraverser());
    return this.getTypeCheckResult().copy();
  }

  @Override
  public TypeCheckResult deriveType(ASTLiteral lit) {
    this.getTypeCheckResult().reset();
    lit.accept(this.getTraverser());
    return this.getTypeCheckResult().copy();
  }
}
