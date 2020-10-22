// (c) https://github.com/MontiCore/monticore

package de.monticore.types.check;

import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesVisitor;
import de.se_rwth.commons.logging.Log;

/**
 * Visitor for Derivation of SymType from MCArrayTypes
 * i.e. for
 * types/MCArrayTypes.mc4
 */
public class SynthesizeSymTypeFromMCArrayTypes extends SynthesizeSymTypeFromMCBasicTypes
    implements MCArrayTypesVisitor, ISynthesize {

  /**
   * Using the visitor functionality to calculate the SymType Expression
   */

  public SynthesizeSymTypeFromMCArrayTypes(){
    super();
  }

  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  MCArrayTypesVisitor realThis = this;

  @Override
  public void setRealThis(MCArrayTypesVisitor realThis) {
    this.realThis = realThis;
    super.realThis = realThis;  // not necessarily needed, but to be safe ...
  }

  @Override
  public MCArrayTypesVisitor getRealThis() {
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

}
