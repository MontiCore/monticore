/* (c) https://github.com/MontiCore/monticore */
package de.monticore.statements.mclowlevelstatements.cocos;

import de.monticore.statements.mclowlevelstatements.MCLowLevelStatementsMill;
import de.monticore.statements.mclowlevelstatements._ast.ASTContinueStatement;
import de.monticore.statements.mclowlevelstatements._ast.ASTLabel;
import de.monticore.statements.mclowlevelstatements._ast.ASTLabelledBreakStatement;
import de.monticore.statements.mclowlevelstatements._cocos.MCLowLevelStatementsASTLabelCoCo;
import de.monticore.statements.mclowlevelstatements._symboltable.LabelSymbol;
import de.monticore.statements.mclowlevelstatements._visitor.MCLowLevelStatementsTraverser;
import de.monticore.statements.mclowlevelstatements._visitor.MCLowLevelStatementsVisitor2;
import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;
import de.se_rwth.commons.logging.Log;

/**
 * Warns if a label is never used.
 * <p> Examples: <br>
 * {@code label: { break label; }} is valid and <br>
 * {@code label: { } } is invalid, because the label is not used.
 */
public class LabelIsUsed implements MCLowLevelStatementsASTLabelCoCo {

  @Override
  public void check(ASTLabel node) {
    if (!node.isPresentSymbol()) {
      return;
    }
    LabelSearch visitor = new LabelSearch(node.getSymbol());

    if (!visitor.isUsedLabel(node.getMCStatement())) {
      Log.warn(
          String.format("0xE0001 Unused label '%s'.", node.getName()),
          node.get_SourcePositionStart(),
          node.get_SourcePositionEnd()
      );
    }
  }

  /**
   * This class can be used to search for a specific label in a statement.
   */
  protected static class LabelSearch implements MCLowLevelStatementsVisitor2 {
    /** The symbol of the label to search for */
    protected LabelSymbol labelSymbol;
    /** Whether the symbol was found or not */
    protected boolean found;

    protected LabelSearch(LabelSymbol labelSymbol) {
      this.labelSymbol = labelSymbol;
    }

    /**
     * Traverses the given statement to look for usage of {@link labelSymbol}.
     *
     * @param statement The statement to traverse.
     * @return {@code true} if the same {@code LabelSymbol} was found in the statement,
     * {@code false} otherwise.
     */
    protected boolean isUsedLabel(ASTMCStatement statement) {
      MCLowLevelStatementsTraverser trav = MCLowLevelStatementsMill.inheritanceTraverser();
      trav.add4MCLowLevelStatements(this);
      statement.accept(trav);

      return this.found;
    }

    @Override
    public void visit(ASTLabelledBreakStatement node) {
      if (node.isPresentLabelSymbol() && labelSymbol == node.getLabelSymbol()) {
        this.found = true;
      }
    }

    @Override
    public void visit(ASTContinueStatement node) {
      if (node.isPresentLabelSymbol() && labelSymbol == node.getLabelSymbol()) {
        this.found = true;
      }
    }

  }

}
