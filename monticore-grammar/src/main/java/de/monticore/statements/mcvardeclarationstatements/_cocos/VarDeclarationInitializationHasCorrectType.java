/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mcvardeclarationstatements._cocos;

import de.monticore.expressions.expressionsbasis._ast.ASTExpression;
import de.monticore.statements.mcvardeclarationstatements.MCVarDeclarationStatementsMill;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTSimpleInit;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.types.check.*;
import de.monticore.types3.SymTypeRelations;
import de.monticore.types3.TypeCheck3;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that the initialization expressions of variable declaration statements are compatible to the types of the
 * variables.
 */
public class VarDeclarationInitializationHasCorrectType
  implements MCVarDeclarationStatementsASTVariableDeclaratorCoCo {

  /**
   * Used to derive the {@link SymTypeExpression} to which initialization expressions evaluate to.
   */
  @Deprecated
  protected IDerive typeDeriver;

  /**
   * Indicates that the type of the initialization expression is not compatible with the type of the assigned variable.
   */
  public static final String ERROR_CODE = "0xA0921";

  /**
   * Indicates that the initialization expression does not evaluate to a value, but to a type name. Example:
   * {@code String foo = String;}
   */
  public static final String TYPE_REF_ASSIGNMENT_ERROR_CODE = "0xA0922";

  public static final String ERROR_MSG_FORMAT = "Incompatible type '%s' of the initialization expression for variable '%s' " +
    "that is of type '%s'.";

  public static final String TYPE_REF_ASSIGNMENT_ERROR_MSG_FORMAT = "The initialization expression for variable " +
    "'%s' represents the type '%s'. As types do not evaluate to values, they can not be used in assignments / " +
    "initializations.";

  /**
   * @param typeDeriver Used to derive the {@link SymTypeExpression} to which initialization expressions evaluate to.
   * @deprecated use default constructor
   */
  public VarDeclarationInitializationHasCorrectType(IDerive typeDeriver) {
    this.typeDeriver = typeDeriver;
  }

  public VarDeclarationInitializationHasCorrectType() {
  }

  @Override
  public void check(ASTVariableDeclarator node) {
    if(!node.isPresentVariableInit() || !(node.getVariableInit() instanceof ASTSimpleInit)) {
      return; // We can only check initializations of the form of expressions (as defined in SimpleInit).

    } else if(!node.getDeclarator().isPresentSymbol()) {
      Log.error(String.format("Could not find a symbol for variable '%s', thus can not check coco '%s'. Check " +
        "whether you have run the symbol table creation before running this coco.",
        node.getDeclarator().getName(), this.getClass().getSimpleName()));

    } else { // Proceed with checking the coco
      SymTypeExpression varType = node.getDeclarator().getSymbol().getType();
      SymTypeExpression initType;
      if (typeDeriver != null) {
        // support deprecated behavior
        TypeCheckResult initResult = typeDeriver.deriveType(((ASTSimpleInit) node.getVariableInit()).getExpression());
        if (initResult.isPresentResult()) {
          if (initResult.isType()) {
            Log.error(TYPE_REF_ASSIGNMENT_ERROR_CODE + " " + String.format(TYPE_REF_ASSIGNMENT_ERROR_MSG_FORMAT,
                node.getDeclarator().getName(), initResult.getResult().print()));
          }
          initType = initResult.getResult();

        }
        else {
          initType = SymTypeExpressionFactory.createObscureType();
        }
      }
      else {
        ASTExpression initExpr = MCVarDeclarationStatementsMill.typeDispatcher()
            .asMCVarDeclarationStatementsASTSimpleInit(node.getVariableInit())
            .getExpression();
        initType = TypeCheck3.typeOf(initExpr, varType);
      }

      if (initType.isObscureType()) {
        // The error is already printed by the IDerive visitors, thus we would spam the log if we would log an error
        // again. Therefore, we only leave a note in the debug log.
        Log.debug(String.format("As the initialization expression for variable '%s' at %s is invalid, coco '%s' " +
          "will not be checked.",
          node.getDeclarator().getName(), node.get_SourcePositionStart(), this.getClass().getSimpleName()), "Cocos");

      } else if (!SymTypeRelations.isCompatible(varType, initType)) {
        Log.error(ERROR_CODE + " " + String.format(ERROR_MSG_FORMAT,
            initType.printFullName(), node.getDeclarator().getName(), varType.printFullName()));
      }
    }
  }
}
