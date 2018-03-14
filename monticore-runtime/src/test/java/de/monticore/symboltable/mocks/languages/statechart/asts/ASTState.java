/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.statechart.asts;

import de.monticore.symboltable.mocks.asts.ASTSymbol;

public class ASTState extends ASTSymbol implements ASTStateChartBase {

  /**
   * Constructor for ASTState
   */
  public ASTState() {
    setSpansScope(true);
    setDefinesNamespace(true);
  }

  @Override
  public void accept(StateChartLanguageBaseVisitor visitor) {
    visitor.traverse(this);
  }
}
