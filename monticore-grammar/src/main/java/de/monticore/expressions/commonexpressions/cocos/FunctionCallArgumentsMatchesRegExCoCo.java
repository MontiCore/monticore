/* (c) https://github.com/MontiCore/monticore */
package de.monticore.expressions.commonexpressions.cocos;

import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions.CommonExpressionsMill;
import de.monticore.expressions.commonexpressions._ast.ASTCallExpression;
import de.monticore.expressions.commonexpressions._cocos.CommonExpressionsASTCallExpressionCoCo;
import de.monticore.literals.mccommonliterals.MCCommonLiteralsMill;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfFunction;
import de.monticore.types.check.TypeCheckResult;
import de.monticore.types3.util.FunctionRelations;
import de.se_rwth.commons.logging.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FunctionCallArgumentsMatchesRegExCoCo implements
    CommonExpressionsASTCallExpressionCoCo {

  protected IDerive derive;

  public FunctionCallArgumentsMatchesRegExCoCo(IDerive derive) {
    this.derive = derive;
  }

  @Override
  public void check(ASTCallExpression node) {
    TypeCheckResult result = derive.deriveType(node);
    if (result.isPresentResult()) {

      SymTypeExpression expr = result.getResult();
      List<SymTypeOfFunction> functions = new ArrayList<>();

      if (expr.isFunctionType()) {
        functions.add(expr.asFunctionType());
      }
      else if (expr.isUnionType()) {
        functions.addAll(expr.asUnionType().getUnionizedTypeSet()
            .stream()
            .filter(SymTypeExpression::isFunctionType)
            .map(SymTypeExpression::asFunctionType)
            .collect(Collectors.toList()));
      }

      List<SymTypeExpression> arguments = node.getArguments().getExpressionList()
          .stream()
          .map(e -> derive.deriveType(e))
          .filter(TypeCheckResult::isPresentResult)
          .map(TypeCheckResult::getResult)
          .collect(Collectors.toList());

      List<SymTypeOfFunction> applicableFunctions = functions.stream()
          .filter(f -> FunctionRelations.canBeCalledWith(f, arguments))
          .map(f -> f.getWithFixedArity(arguments.size()))
          .collect(Collectors.toList());

      for (SymTypeOfFunction function : applicableFunctions) {
        for (int i = 0; i < arguments.size(); i++) {
          SymTypeExpression parameterType = function.getArgumentType(i);
          ASTNode argumentNode = node.getArguments().getExpression(i);
          if (parameterType.isRegExType() &&
              CommonExpressionsMill.typeDispatcher()
                  .isASTLiteralExpression(argumentNode)) {
            ASTLiteral literal = CommonExpressionsMill.typeDispatcher()
                .asASTLiteralExpression(argumentNode)
                .getLiteral();
            if (MCCommonLiteralsMill.typeDispatcher()
                .isASTStringLiteral(literal)) {
              String s = MCCommonLiteralsMill.typeDispatcher()
                  .asASTStringLiteral(literal).getSource();
              String regex = parameterType.asRegExType().getRegExString();
              if (!s.matches(regex)) {
                Log.error("0xFD725 incompatible String literal \""
                        + s + "\" is assigned to regex type "
                        + parameterType.printFullName()
                        + " as argument " + i,
                    argumentNode.get_SourcePositionStart(),
                    argumentNode.get_SourcePositionEnd()
                );
              }
            }
          }
        }
      }
    }
  }
}
