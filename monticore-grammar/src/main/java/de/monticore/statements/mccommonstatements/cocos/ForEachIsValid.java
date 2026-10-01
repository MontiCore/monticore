/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mccommonstatements.cocos;

import com.google.common.base.Preconditions;
import de.monticore.statements.mccommonstatements._ast.ASTEnhancedForControl;
import de.monticore.statements.mccommonstatements._cocos.MCCommonStatementsASTEnhancedForControlCoCo;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCalculator;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

import static de.monticore.types3.SymTypeRelations.isCompatible;
import static de.monticore.types3.SymTypeRelationsOfIterables.getIterationType;
import static java.lang.String.format;

public class ForEachIsValid implements MCCommonStatementsASTEnhancedForControlCoCo {

  public static final String FOR_EACH_EXPR_NOT_ITERABLE_ERROR_CODE = "0xA0907";

  public static final String FOR_EACH_EXPR_NOT_ITERABLE_ERROR_MSG =
    "For-each loop expression must be iterable. Instead, the type is '%s'";

  public static final String FOR_EACH_TYPE_MISMATCH_ERROR_CODE = "0xA0908";

  public static final String FOR_EACH_TYPE_MISMATCH_ERROR_MSG =
    "Type mismatch, expected '%s' but provided '%s'";

  @Deprecated
  TypeCalculator typeCheck;

  @Deprecated
  public static final String ERROR_CODE = FOR_EACH_EXPR_NOT_ITERABLE_ERROR_CODE;

  @Deprecated
  public static final String ERROR_MSG_FORMAT = FOR_EACH_EXPR_NOT_ITERABLE_ERROR_MSG;

  /**
   * @deprecated use default constructor
   */
  @Deprecated
  public ForEachIsValid(TypeCalculator typeCheck) {
    this.typeCheck = typeCheck;
  }

  public ForEachIsValid() {
  }

  @Override
  public void check(ASTEnhancedForControl node) {
    Preconditions.checkNotNull(node);

    SymTypeExpression typeOfVariable = TypeCheck3.symTypeFromAST(node.getFormalParameter().getMCType());
    SymTypeExpression typeOfExpression = TypeCheck3.typeOf(node.getExpression());

    if (typeOfVariable.isObscureType() || typeOfExpression.isObscureType()) {
      return;
    }

    Optional<SymTypeExpression> symTypeOfIteration = getIterationType(typeOfExpression);

    if (symTypeOfIteration.isEmpty()) {
      Log.error(FOR_EACH_EXPR_NOT_ITERABLE_ERROR_CODE + " "
          + format(FOR_EACH_EXPR_NOT_ITERABLE_ERROR_MSG, typeOfExpression.printFullName()),
        node.getExpression().get_SourcePositionStart(),
        node.getExpression().get_SourcePositionEnd()
      );
      return;
    }

    if (symTypeOfIteration.get().isObscureType()) {
      return;
    }

    if (!isCompatible(typeOfVariable, symTypeOfIteration.get())) {
      Log.error(FOR_EACH_TYPE_MISMATCH_ERROR_CODE + " " +
          format(FOR_EACH_TYPE_MISMATCH_ERROR_MSG,
            symTypeOfIteration.get().printFullName(),
            typeOfVariable.printFullName()
          ),
        node.getFormalParameter().get_SourcePositionStart(),
        node.getFormalParameter().get_SourcePositionEnd()
      );
    }
  }
}
