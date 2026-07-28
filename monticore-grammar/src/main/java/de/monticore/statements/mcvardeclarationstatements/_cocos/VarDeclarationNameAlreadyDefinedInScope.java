/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mcvardeclarationstatements._cocos;

import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.symbols.basicsymbols._symboltable.VariableSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Checks whether the variable name has already been defined in the scope.
 * In an ordered scope, an error will not be logged for the first defining variable.
 */
public class VarDeclarationNameAlreadyDefinedInScope implements MCVarDeclarationStatementsASTVariableDeclaratorCoCo {

  /**
   * Indicates that the name of the variable has already been used in the scope
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

    List<VariableSymbol> matchingCandidates = node.getEnclosingScope().resolveVariableMany(
        node.getDeclarator().getName()
    );

    if (matchingCandidates.stream().anyMatch(v -> alreadyDefined(node, v))) {
      Log.error(ERROR_CODE + " " + String.format(ERROR_MSG_FORMAT,
          node.getDeclarator().getName()),
          node.get_SourcePositionStart(), node.get_SourcePositionEnd());
    }
  }

  protected boolean alreadyDefined(ASTVariableDeclarator node, VariableSymbol o) {
    if (node.getDeclarator().getSymbol() == o) return false;
    if (node.getEnclosingScope().isOrdered() && node.getEnclosingScope() == o.getEnclosingScope()) {
      // If the scope is ordered and both symbols are in the same scope the first defining location is not considered already defined
      return !o.isPresentAstNode()
          || !o.getAstNode().isPresent_SourcePositionStart()
          || !node.isPresent_SourcePositionStart()
          || o.getAstNode().get_SourcePositionStart().compareTo(node.get_SourcePositionStart()) < 0;
    } else {
      // if the scope is unordered the symbol is already defined
      return true;
    }
  }
}
