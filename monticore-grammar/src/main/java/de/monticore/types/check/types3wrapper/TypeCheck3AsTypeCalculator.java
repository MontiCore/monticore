// (c) https://github.com/MontiCore/monticore
package de.monticore.types.check.types3wrapper;

import de.monticore.expressions.expressionsbasis.types3.util.ILValueRelations;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types3.Type4Ast;
import de.monticore.types3.SymTypeRelations;
import de.monticore.visitor.ITraverser;

/**
 * the TypeCalculator class using the TypeCheck3.
 * While the TypeCheck3 should be used directly,
 * this can be used to try the TypeCheck without major rewrites.
 */
@Deprecated(forRemoval = true)
public class TypeCheck3AsTypeCalculator extends TypeCalculator {

  protected Type4Ast type4Ast;

  protected ITraverser typeTraverser;

  /**
   * @param typeTraverser    traverser filling type4Ast, language specific
   * @param type4Ast         a map of types to be filled
   * @param lValueRelations  is expression a variable?, language specific
   */
  public TypeCheck3AsTypeCalculator(
      ITraverser typeTraverser,
      Type4Ast type4Ast,
      ILValueRelations lValueRelations
  ) {
    super(
        new TypeCheck3AsISynthesize(typeTraverser, type4Ast),
        new TypeCheck3AsIDerive(typeTraverser, type4Ast, lValueRelations)
    );
    this.typeTraverser = typeTraverser;
    this.type4Ast = type4Ast;
  }

  @Override
  public boolean compatible(SymTypeExpression left, SymTypeExpression right) {
    return SymTypeRelations.isCompatible(left, right);
  }

  @Override
  public boolean isSubtypeOf(SymTypeExpression subType, SymTypeExpression superType) {
    return SymTypeRelations.isSubTypeOf(subType, superType);
  }
}
