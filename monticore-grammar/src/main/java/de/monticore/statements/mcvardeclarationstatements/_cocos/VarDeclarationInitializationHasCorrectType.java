/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mcvardeclarationstatements._cocos;

import de.monticore.statements.mcvardeclarationstatements._ast.ASTSimpleInit;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.types.check.IDerive;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.TypeCheckResult;
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
  protected final IDerive typeDeriver;

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
   */
  public VarDeclarationInitializationHasCorrectType(IDerive typeDeriver) {
    this.typeDeriver = typeDeriver;
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
      TypeCheckResult initType = typeDeriver.deriveType(((ASTSimpleInit) node.getVariableInit()).getExpression());

      if(!initType.isPresentCurrentResult()) {
        // The error is already printed by the IDerive visitors, thus we would spam the log if we would log an error
        // again. Therefore, we only leave a note in the debug log.
        Log.debug(String.format("As the initialization expression for variable '%s' at %s is invalid, coco '%s' " +
          "will not be checked.",
          node.getDeclarator().getName(), node.get_SourcePositionStart(), this.getClass().getSimpleName()), "Cocos");

      } else if(initType.isType()) {
        Log.error(TYPE_REF_ASSIGNMENT_ERROR_CODE + " " + String.format(TYPE_REF_ASSIGNMENT_ERROR_MSG_FORMAT,
          node.getDeclarator().getName(), initType.getCurrentResult().print()));

      } else if(!TypeCheck.compatible(varType, initType.getCurrentResult())) {
        Log.error(ERROR_CODE + " " + String.format(ERROR_MSG_FORMAT,
          initType.getCurrentResult().print(), node.getDeclarator().getName(), varType.print()));
      }
    }
  }
}
