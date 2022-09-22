/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.check;

import de.monticore.types.mcfullgenerictypes._ast.ASTMCWildcardTypeArgument;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesHandler;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesTraverser;
import de.monticore.types.mcfullgenerictypes._visitor.MCFullGenericTypesVisitor2;
import de.se_rwth.commons.logging.Log;

/**
 * Visitor for Derivation of SymType from MCFullGenericTypes
 * i.e. for
 * types/MCFullGenericTypes.mc4
 */
public class SynthesizeSymTypeFromMCFullGenericTypes extends AbstractSynthesizeFromType
    implements MCFullGenericTypesVisitor2, MCFullGenericTypesHandler {

  protected MCFullGenericTypesTraverser traverser;

  @Override
  public MCFullGenericTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCFullGenericTypesTraverser traverser) {
    this.traverser = traverser;
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

  public void traverse(ASTMCWildcardTypeArgument wildcardType) {
    SymTypeOfWildcard tex;
    if(getTypeCheckResult().isPresentResult() && !getTypeCheckResult().getResult().isObscureType()) {
      if (wildcardType.isPresentLowerBound()) {
        wildcardType.getLowerBound().accept(getTraverser());
        if (!getTypeCheckResult().isPresentResult()) {
          Log.error("0xE9CDD Internal Error: SymType argument missing for generic type. "
            + " Probably TypeCheck mis-configured.");
          getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
          return;
        }
        tex = SymTypeExpressionFactory.createWildcard(false, getTypeCheckResult().getResult());
      } else if (wildcardType.isPresentUpperBound()) {
        wildcardType.getUpperBound().accept(getTraverser());
        if (!getTypeCheckResult().isPresentResult()) {
          Log.error("0xE9CDA Internal Error: SymType argument missing for generic type. "
            + " Probably TypeCheck mis-configured.");
          getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
          return;
        }
        tex = SymTypeExpressionFactory.createWildcard(true, getTypeCheckResult().getResult());
      } else {
        tex = SymTypeExpressionFactory.createWildcard();
      }
      getTypeCheckResult().setResult(tex);
    }
  }
}
