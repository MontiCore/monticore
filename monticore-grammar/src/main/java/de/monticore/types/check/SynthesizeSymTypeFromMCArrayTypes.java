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
    if (!getTypeCheckResult().isPresentResult()) {
      Log.error("0xE9CDC Internal Error: SymType argument missing for generic type. "
              + " Probably TypeCheck mis-configured.");
      getTypeCheckResult().reset();
      return;
    }
    if(!getTypeCheckResult().getResult().isObscureType()){
      SymTypeExpression tex = SymTypeExpressionFactory.createTypeArray(
        arrayType.printTypeWithoutBrackets(), getScope(arrayType.getEnclosingScope()),
        arrayType.getDimensions(),
        getTypeCheckResult().getResult());
      getTypeCheckResult().setResult(tex);
      arrayType.setDefiningSymbol(tex.getTypeInfo());
    }
  }

}
