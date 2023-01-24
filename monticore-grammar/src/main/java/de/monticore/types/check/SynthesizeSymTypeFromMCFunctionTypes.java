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

  @Override
  public void handle(ASTMCFunctionType functionType) {
    SymTypeExpression symType;

    List<SymTypeExpression> arguments = new LinkedList<SymTypeExpression>();
    for (int i = 0; i<functionType.getMCFunctionParTypes().sizeMCTypes(); i++) {
      ASTMCType arg = functionType.getMCFunctionParTypes().getMCType(i);
      getTypeCheckResult().reset();
      if (null != arg) {
        arg.accept(getTraverser());
      }

      if (!getTypeCheckResult().isPresentResult()) {
        Log.error("0xE9BDC Type of argument number " + i+1 + " of the function type could not" +
          "be synthesized.", functionType.get_SourcePositionStart());
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
      Log.error("0xE9BDD The return type of the function type could not be synthesized.",
          functionType.get_SourcePositionStart());
      getTypeCheckResult().setResult(SymTypeExpressionFactory.createObscureType());
      return;
    }
    returnArgument = getTypeCheckResult().getResult();

    symType = SymTypeExpressionFactory.createFunction(
        returnArgument,
        arguments,
        functionType.getMCFunctionParTypes().isPresentIsElliptic()
    );
    getTypeCheckResult().setResult(symType);
    functionType.setDefiningSymbol(symType.getTypeInfo());
  }

}
