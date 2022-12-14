/* (c) https://github.com/MontiCore/monticore */

package de.monticore.types.check;

import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfunctiontypes._ast.ASTMCFunctionType;
import de.monticore.types.mcfunctiontypes._visitor.MCFunctionTypesHandler;
import de.monticore.types.mcfunctiontypes._visitor.MCFunctionTypesTraverser;
import de.monticore.types.mcfunctiontypes._visitor.MCFunctionTypesVisitor2;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedList;
import java.util.List;

/**
 * Visitor for Derivation of SymType from MCFunctionTypes
 * i.e. for
 * types/MCFunctionTypes.mc4
 */
public class SynthesizeSymTypeFromMCFunctionTypes extends AbstractSynthesizeFromType
    implements MCFunctionTypesVisitor2, MCFunctionTypesHandler {

  protected MCFunctionTypesTraverser traverser;

  @Override
  public MCFunctionTypesTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(MCFunctionTypesTraverser traverser) {
    this.traverser = traverser;
  }

  public void handle(ASTMCFunctionType functionType) {
    SymTypeExpression symType = null;

    List<SymTypeExpression> arguments = new LinkedList<SymTypeExpression>();
    for (ASTMCType arg : functionType.getMCFunctionParameters().getMCTypeList()) {
      getTypeCheckResult().reset();
      if (null != arg) {
        arg.accept(getTraverser());
      }

      if (!getTypeCheckResult().isPresentResult()) {
        Log.error("0xE9BDC Internal Error: SymType argument missing for function type. "
            + " Probably TypeCheck mis-configured.");
        getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
        return;
      }
      arguments.add(getTypeCheckResult().getResult());
    }

    SymTypeExpression returnArgument;
    getTypeCheckResult().reset();
    if (null != functionType.getMCReturnType()) {
      functionType.getMCReturnType().accept(getTraverser());
    }

    if (!getTypeCheckResult().isPresentResult()) {
      Log.error("0xE9BDD Internal Error: SymType return argument missing for function type. "
          + " Probably TypeCheck mis-configured.");
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      return;
    }
    returnArgument = getTypeCheckResult().getResult();

    symType = SymTypeExpressionFactory.createFunction(
        returnArgument,
        arguments,
        functionType.getMCFunctionParameters().isPresentIsElliptic()
    );
    getTypeCheckResult().setResult(symType);
    functionType.setDefiningSymbol(symType.getTypeInfo());
  }

}
