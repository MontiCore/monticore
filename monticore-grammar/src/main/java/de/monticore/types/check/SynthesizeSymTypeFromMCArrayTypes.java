/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.check;

import de.monticore.types.mcarraytypes._ast.ASTMCArrayType;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesHandler;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesTraverser;
import de.monticore.types.mcarraytypes._visitor.MCArrayTypesVisitor2;
import de.se_rwth.commons.logging.Log;

/**
 * Visitor for Derivation of SymType from MCArrayTypes
 * i.e. for
 * types/MCArrayTypes.mc4
 */
public class SynthesizeSymTypeFromMCArrayTypes extends AbstractSynthesizeFromType
    implements MCArrayTypesVisitor2, MCArrayTypesHandler {

  protected MCArrayTypesTraverser traverser;

  @Override
  public void setTraverser(MCArrayTypesTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public MCArrayTypesTraverser getTraverser() {
    return traverser;
  }

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
    arrayType.getMCType().accept(getTraverser());
    if (!typeCheckResult.isPresentCurrentResult()) {
      Log.error("0xE9CDC Internal Error: SymType argument missing for generic type. "
              + " Probably TypeCheck mis-configured.");
    }
    SymTypeExpression tex = SymTypeExpressionFactory.createTypeArray(
            arrayType.printTypeWithoutBrackets(), getScope(arrayType.getEnclosingScope()),
            arrayType.getDimensions(),
            typeCheckResult.getCurrentResult());
    typeCheckResult.setCurrentResult(tex);
  }

}
