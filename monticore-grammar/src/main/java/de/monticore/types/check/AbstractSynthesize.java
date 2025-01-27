/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;
import de.monticore.types.mcbasictypes._ast.ASTMCReturnType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.visitor.ITraverser;

/**
 * @deprecated use {@link de.monticore.types3.TypeCheck3}
 */
@Deprecated(forRemoval = true)
public abstract class AbstractSynthesize implements ISynthesize {

  protected ITraverser traverser;
  protected TypeCheckResult typeCheckResult;

  protected ITraverser getTraverser() {
    return traverser;
  }

  protected TypeCheckResult getTypeCheckResult() {
    return typeCheckResult;
  }

  public AbstractSynthesize(ITraverser traverser) {
    this.typeCheckResult = new TypeCheckResult();
    this.traverser = traverser;
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCType type) {
    this.getTypeCheckResult().reset();
    type.accept(this.getTraverser());
    return this.getTypeCheckResult().copy();
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCReturnType type) {
    this.getTypeCheckResult().reset();
    type.accept(this.getTraverser());
    return this.getTypeCheckResult().copy();
  }

  @Override
  public TypeCheckResult synthesizeType(ASTMCQualifiedName qName) {
    this.getTypeCheckResult().reset();
    qName.accept(this.getTraverser());
    return this.getTypeCheckResult().copy();
  }


}
