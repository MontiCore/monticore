// (c) https://github.com/MontiCore/monticore
package de.monticore.statements.mclowlevelstatements._symboltable;

import de.monticore.statements.mcstatementsbasis._ast.ASTMCStatement;

import java.util.Optional;

public class LabelSymbol extends LabelSymbolTOP {

  public LabelSymbol(String name) {
    super(name);
  }

  // helper

  /**
   * returns the label of a given statement
   *
   * @param statement the statement to get the label of
   * @return the label or empty
   */
  public static Optional<LabelSymbol> getLabelOfStatement(
      ASTMCStatement statement
  ) {
    Optional<LabelSymbol> labelOfStatement = Optional.empty();
    if (
        statement.getEnclosingScope()
            instanceof IMCLowLevelStatementsScope scope
    ) {
      for (LabelSymbol label : scope.getLocalLabelSymbols()) {
        if (label.isPresentAstNode() &&
                label.getAstNode().getMCStatement() == statement
        ) {
          labelOfStatement = Optional.of(label);
          break;
        }
      }
    }
    return labelOfStatement;
  }

}
