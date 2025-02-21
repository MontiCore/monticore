// (c) https://github.com/MontiCore/monticore
package de.monticore.types.check.types3wrapper;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.expressions.expressionsbasis.types3.util.ILValueRelations;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.TypeCheck3;
import de.monticore.visitor.ITraverser;
import de.se_rwth.commons.logging.Log;

/**
 * IDerive using the TypeCheck3.
 * Does not set isMethod of TypeCheckResult.
 * While the TypeCheck3 should be used directly,
 * this can be used to try the TypeCheck without major rewrites.
 */
public class TypeCheck3AsIDerive implements IDerive {

  @Deprecated
  protected Type4Ast type4Ast;

  @Deprecated
  protected ITraverser typeTraverser;

  protected ILValueRelations lValueRelations;

  /**
   * type4Ast should be filled by the typeTraverser,
   * thus, this and TypeCheck3AsISynthesize should have the same type4Ast.
   */
  @Deprecated
  public TypeCheck3AsIDerive(
      ITraverser typeTraverser,
      Type4Ast type4Ast,
      ILValueRelations lValueRelations
  ) {
    this.typeTraverser = typeTraverser;
    this.type4Ast = type4Ast;
    this.lValueRelations = lValueRelations;
  }

  public TypeCheck3AsIDerive(ILValueRelations lValueRelations) {
    this.lValueRelations = Log.errorIfNull(lValueRelations);
  }

  @Override
  public TypeCheckResult deriveType(ASTExpression expr) {
    TypeCheckResult result = new TypeCheckResult();
    if (type4Ast != null) {
      if (!type4Ast.hasTypeOfExpression(expr)) {
        expr.accept(typeTraverser);
      }
      result.setResult(type4Ast.getTypeOfExpression(expr));
    }
    else {
      result.setResult(TypeCheck3.typeOf(expr));
    }
    if (lValueRelations.isLValue(expr)) {
      result.setField();
    }
    return result;
  }

  @Override
  public TypeCheckResult deriveType(ASTLiteral lit) {
    TypeCheckResult result = new TypeCheckResult();
    if (type4Ast != null) {

      if (!type4Ast.hasTypeOfExpression(lit)) {
        lit.accept(typeTraverser);
      }
      result.setResult(type4Ast.getTypeOfExpression(lit));
    }
    else {
      result.setResult(TypeCheck3.typeOf(lit));
    }
    return result;
  }

}
