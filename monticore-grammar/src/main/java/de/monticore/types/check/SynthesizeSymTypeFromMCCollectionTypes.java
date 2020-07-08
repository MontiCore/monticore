/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.check;

import de.monticore.types.mcbasictypes.MCBasicTypesMill;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mccollectiontypes._ast.ASTMCListType;
import de.monticore.types.mccollectiontypes._ast.ASTMCMapType;
import de.monticore.types.mccollectiontypes._ast.ASTMCOptionalType;
import de.monticore.types.mccollectiontypes._ast.ASTMCSetType;
import de.monticore.types.mccollectiontypes._visitor.MCCollectionTypesVisitor;
import de.se_rwth.commons.logging.Log;

/**
 * Visitor for Derivation of SymType from MCBasicTypes
 * i.e. for
 * types/MCBasicTypes.mc4
 */
public class SynthesizeSymTypeFromMCCollectionTypes extends SynthesizeSymTypeFromMCBasicTypes
    implements MCCollectionTypesVisitor, ISynthesize {


  /**
   * Using the visitor functionality to calculate the SymType Expression
   */

  // ----------------------------------------------------------  realThis start
  // setRealThis, getRealThis are necessary to make the visitor compositional
  //
  // (the Vistors are then composed using theRealThis Pattern)
  //
  MCCollectionTypesVisitor realThis = this;

  public SynthesizeSymTypeFromMCCollectionTypes(){
    super();
  }

  @Override
  public void setRealThis(MCCollectionTypesVisitor realThis) {
    this.realThis = realThis;
    super.realThis = realThis;  // not necessarily needed, but to be safe ...
  }

  @Override
  public MCCollectionTypesVisitor getRealThis() {
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
      node.getKey().accept(getRealThis());
    }
    if (!typeCheckResult.isPresentCurrentResult()) {
      Log.error("0xE9FDA Internal Error: Missing SymType argument 1 for Map type. "
          + " Probably TypeCheck mis-configured.");
    }
    SymTypeExpression argument1 = typeCheckResult.getCurrentResult();

    // Argument 2:
    if (null != node.getValue()) {
      node.getValue().accept(getRealThis());
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

  /**
   * There are several forms of qualified Types possible:
   * ** Object-types
   * ** Boxed primitives, such as "java.lang.Boolean"
   * Primitives, like "int", void, null are not possible here.
   * This are the qualified Types that may occur.
   * <p>
   * To distinguish these kinds, we use the symbol that the ASTMCQualifiedType identifies
   *
   * @param qType
   */
  @Override
  public void endVisit(ASTMCQualifiedType qType) {
    typeCheckResult.setCurrentResult(SymTypeExpressionFactory.createTypeObject(qType.printType(MCBasicTypesMill.mcBasicTypesPrettyPrinter()), getScope(qType.getEnclosingScope())));
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
