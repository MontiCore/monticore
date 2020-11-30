/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesHandler;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesTraverser;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor2;
import de.se_rwth.commons.logging.Log;

/**
 * Visitor for Derivation of SymType from MCBasicTypes
 * i.e. for
 * types/MCBasicTypes.mc4
 */
public class SynthesizeSymTypeFromMCCollectionTypes extends AbstractSynthesizeFromType implements MCCollectionTypesVisitor2, MCCollectionTypesHandler, ISynthesize {


  protected MCCollectionTypesTraverser traverser;

  @Override
  public void setTraverser(MCCollectionTypesTraverser traverser) {
    this.traverser = traverser;
  }

  @Override
  public MCCollectionTypesTraverser getTraverser() {
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

  public void endVisit(ASTMCListType t) {
    // argument Type has been processed and stored in result:
    SymTypeExpression tex =
        SymTypeExpressionFactory.createGenerics("List", getScope(t.getEnclosingScope()), typeCheckResult.getCurrentResult());
    if (!typeCheckResult.isPresentCurrentResult()) {
      Log.error("0xE9FD6 Internal Error: No SymType argument for List type. "
          + " Probably TypeCheck mis-configured.");
    }
    typeCheckResult.setCurrentResult(tex);
  }

  public void endVisit(ASTMCSetType t) {
    // argument Type has been processed and stored in result:
    SymTypeExpression tex =
        SymTypeExpressionFactory.createGenerics("Set", getScope(t.getEnclosingScope()), typeCheckResult.getCurrentResult());
    if (!typeCheckResult.isPresentCurrentResult()) {
      Log.error("0xE9FD7 Internal Error: No SymType argument for Set type. "
          + " Probably TypeCheck mis-configured.");
    }
    typeCheckResult.setCurrentResult(tex);
  }

  public void endVisit(ASTMCOptionalType t) {
    // argument Type has been processed and stored in result:
    SymTypeExpression tex =
        SymTypeExpressionFactory.createGenerics("Optional", getScope(t.getEnclosingScope()), typeCheckResult.getCurrentResult());
    if (!typeCheckResult.isPresentCurrentResult()) {
      Log.error("0xE9FD8 Internal Error: No SymType argument for Optional type. "
          + " Probably TypeCheck mis-configured.");
    }
    typeCheckResult.setCurrentResult(tex);
  }

  /**
   * Map has two arguments: to retrieve them properly, the traverse method
   * is adapted (looking deeper into the visitor), instead of the endVisit method:
   */
  public void traverse(ASTMCMapType node) {
    // Argument 1:
    if (null != node.getKey()) {
      node.getKey().accept(getTraverser());
    }
    if (!typeCheckResult.isPresentCurrentResult()) {
      Log.error("0xE9FDA Internal Error: Missing SymType argument 1 for Map type. "
          + " Probably TypeCheck mis-configured.");
    }
    SymTypeExpression argument1 = typeCheckResult.getCurrentResult();

    // Argument 2:
    if (null != node.getValue()) {
      node.getValue().accept(getTraverser());
    }
    if (!typeCheckResult.isPresentCurrentResult()) {
      Log.error("0xE9FDB Internal Error: Missing SymType argument 1 for Map type. "
          + " Probably TypeCheck mis-configured.");
    }
    SymTypeExpression argument2 = typeCheckResult.getCurrentResult();
    // Construct new TypeExpression:
    SymTypeExpression tex =
        SymTypeExpressionFactory.createGenerics(
            "Map", getScope(node.getEnclosingScope()), argument1, argument2);
    typeCheckResult.setCurrentResult(tex);
  }

  // ASTMCTypeArgument, ASTMCBasicTypeArgument and  MCPrimitiveTypeArgument:
  // Do nothing, because result already contains the argument
  // (because: MCG contains:
  // interface MCTypeArgument;
  // MCBasicTypeArgument implements MCTypeArgument <200> =
  //       MCQualifiedType;
  //
  // MCPrimitiveTypeArgument implements MCTypeArgument <190> =
  //       MCPrimitiveType;


}
