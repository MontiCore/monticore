/* (c) https://github.com/MontiCore/monticore */
package de.monticore.types.mcfunctiontypes.types3;

import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcfunctiontypes._ast.ASTMCFunctionType;
import de.monticore.types.mcfunctiontypes._ast.ASTMCFunctionTypeNoParentheses;
import de.monticore.types.mcfunctiontypes._visitor.MCFunctionTypesVisitor2;
import de.monticore.types3.AbstractTypeVisitor;
import de.se_rwth.commons.logging.Log;

import java.util.LinkedList;
import java.util.List;

public class MCFunctionTypesTypeVisitor extends AbstractTypeVisitor
    implements MCFunctionTypesVisitor2 {

  @Override
  public void endVisit(ASTMCFunctionType functionType) {
    SymTypeExpression symType;

    List<SymTypeExpression> arguments = new LinkedList<SymTypeExpression>();
    for (int i = 0; i < functionType.getMCFunctionParTypes().sizeMCTypes(); i++) {
      ASTMCType par = functionType.getMCFunctionParTypes().getMCType(i);

      if (getType4Ast().getPartialTypeOfTypeId(par).isObscureType()) {
        getType4Ast().setTypeOfTypeIdentifier(functionType,
            SymTypeExpressionFactory.createObscureType());
        return;
      }
      arguments.add(getType4Ast().getPartialTypeOfTypeId(par));
    }

    if (!getType4Ast().hasTypeOfTypeIdentifier(functionType.getMCReturnType())) {
      getType4Ast().setTypeOfTypeIdentifier(functionType,
          SymTypeExpressionFactory.createObscureType());
      return;
    }
    SymTypeExpression returnArgument = getType4Ast()
        .getPartialTypeOfTypeId(functionType.getMCReturnType());

    symType = SymTypeExpressionFactory.createFunction(
        returnArgument,
        arguments,
        functionType.getMCFunctionParTypes().isPresentIsElliptic()
    );
    getType4Ast().setTypeOfTypeIdentifier(functionType, symType);
  }

  @Override
  public void endVisit(ASTMCFunctionTypeNoParentheses functionType) {
    SymTypeExpression symType;

    if (getType4Ast().getPartialTypeOfTypeId(functionType.getParameter())
        .isObscureType()) {
      getType4Ast().setTypeOfTypeIdentifier(functionType,
          SymTypeExpressionFactory.createObscureType());
      return;
    }
    SymTypeExpression parType =
        getType4Ast().getPartialTypeOfTypeId(functionType.getParameter());

    if (!getType4Ast().hasTypeOfTypeIdentifier(functionType.getMCReturnType())) {
      Log.error("0xE9BDD The return type of the function type"
              + " could not be synthesized.",
          functionType.getMCReturnType().get_SourcePositionStart(),
          functionType.getMCReturnType().get_SourcePositionEnd()
      );
      getType4Ast().setTypeOfTypeIdentifier(functionType,
          SymTypeExpressionFactory.createObscureType());
      return;
    }
    SymTypeExpression returnArgument = getType4Ast()
        .getPartialTypeOfTypeId(functionType.getMCReturnType());

    symType = SymTypeExpressionFactory.createFunction(
        returnArgument, List.of(parType), false
    );
    getType4Ast().setTypeOfTypeIdentifier(functionType, symType);
  }

}
