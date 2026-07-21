/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mcvardeclarationstatements._cocos;

import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Checks whether the variable name has already been defined in the local scope.
 * Logs an error for _each_ variable declaration (with the same name).
 */
public class VarDeclarationNameAlreadyDefinedInScope implements MCVarDeclarationStatementsASTVariableDeclaratorCoCo {

  /**
   * Indicates that the name of the variable has already been used in the scope
   * This will produce one error for _each_ variable declaration (with the same name)
   */
  public static final String ERROR_CODE = "0xA0923";

  public static final String ERROR_MSG_FORMAT = "Variable '%s' is already defined in the scope.";

  public static final String ERROR_CODE_MISSING_SYMBOL = "0xA0924";

  @Override
  public void check(ASTVariableDeclarator node) {
    if (!node.getDeclarator().isPresentSymbol()) {
      Log.error(String.format(ERROR_CODE_MISSING_SYMBOL + " Could not find a symbol for variable '%s', thus can not check coco '%s'. Check " +
              "whether you have run the symbol table creation before running this coco.",
          node.getDeclarator().getName(), this.getClass().getSimpleName()),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
      return;
    }

    List<VariableSymbol> localVarSymbols = node.getEnclosingScope().resolveVariableMany(
        node.getDeclarator().getName()
    );

    boolean hasPreviousDeclaration = localVarSymbols.stream().anyMatch(
        v -> v != node.getDeclarator().getSymbol() &&
            (!v.isPresentAstNode()
            || !v.getAstNode().isPresent_SourcePositionStart()
            || !node.isPresent_SourcePositionStart()
            || v.getAstNode().get_SourcePositionStart().compareTo(node.get_SourcePositionStart()) < 0)
    );

    if (hasPreviousDeclaration) {
      Log.error(ERROR_CODE + " " + String.format(ERROR_MSG_FORMAT,
          node.getDeclarator().getName()),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }
}
