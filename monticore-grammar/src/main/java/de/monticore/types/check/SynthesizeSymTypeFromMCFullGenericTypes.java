// (c) https://github.com/MontiCore/monticore

package de.monticore.types.check;

import de.monticore.types.mcfullgenerictypes._ast.ASTMCArrayType;
import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesVisitor;
import de.se_rwth.commons.logging.Log;

/**
 * Visitor for Derivation of SymType from MCSimpleGenericTypes
 * i.e. for
 * types/MCSimpleGenericTypes.mc4
 */
public class SynthesizeSymTypeFromMCFullGenericTypes extends SynthesizeSymTypeFromMCSimpleGenericTypes
    implements MCFullGenericTypesVisitor, ISynthesize {

  /**
   * Using the visitor functionality to calculate the SymType Expression
   */

  public SynthesizeSymTypeFromMCFullGenericTypes(){
    super();
  }

  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  MCFullGenericTypesVisitor realThis = this;

  @Override
  public void setRealThis(MCFullGenericTypesVisitor realThis) {
    this.realThis = realThis;
    super.realThis = realThis;  // not necessarily needed, but to be safe ...
  }

  @Override
  public MCFullGenericTypesVisitor getRealThis() {
    return realThis;
  }
  // ---------------------------------------------------------- realThis end

  /**
   * Storage in the Visitor: result of the last endVisit
   * is inherited
   * This attribute is synthesized upward.
   */

  /**
   * We use mainly endVisit, because the result is synthesized along the
   * tree, when walking upwards
   */

  public void traverse(ASTMCArrayType arrayType) {
    arrayType.getMCType().accept(getRealThis());
    if (!typeCheckResult.isPresentCurrentResult()) {
      Log.error("0xE9CDA Internal Error: SymType argument missing for generic type. "
              + " Probably TypeCheck mis-configured.");
    }
    SymTypeExpression tex = SymTypeExpressionFactory.createTypeArray(
            arrayType.printTypeWithoutBrackets(), getScope(arrayType.getEnclosingScope()),
            arrayType.getDimensions(),
            typeCheckResult.getCurrentResult());
    typeCheckResult.setCurrentResult(tex);
  }

  public void traverse(ASTMCWildcardTypeArgument wildcardType) {
    SymTypeOfWildcard tex;
    if (wildcardType.isPresentLowerBound()) {
      wildcardType.getLowerBound().accept(getRealThis());
      if (!typeCheckResult.isPresentCurrentResult()) {
        Log.error("0xE9CDA Internal Error: SymType argument missing for generic type. "
                + " Probably TypeCheck mis-configured.");
      }
      tex = SymTypeExpressionFactory.createWildcard(false, typeCheckResult.getCurrentResult());
    } else if (wildcardType.isPresentUpperBound()) {
      wildcardType.getUpperBound().accept(getRealThis());
      if (!typeCheckResult.isPresentCurrentResult()) {
        Log.error("0xE9CDA Internal Error: SymType argument missing for generic type. "
                + " Probably TypeCheck mis-configured.");
      }
      tex = SymTypeExpressionFactory.createWildcard(true, typeCheckResult.getCurrentResult());
    } else {
      tex = SymTypeExpressionFactory.createWildcard();
    }
    typeCheckResult.setCurrentResult(tex);
  }
}
