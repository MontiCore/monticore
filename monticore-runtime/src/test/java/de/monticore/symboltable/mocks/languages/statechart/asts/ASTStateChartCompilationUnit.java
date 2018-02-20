/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.statechart.asts;

import de.monticore.symboltable.mocks.asts.ASTCompilationUnit;

// Diese Klasse wird später aus der Grammatik generiert
public class ASTStateChartCompilationUnit extends ASTCompilationUnit implements ASTStateChartBase {

  private ASTStateChart stateChart;

  public void setStateChart(ASTStateChart stateChart) {
    this.stateChart = stateChart;
    addChild(stateChart);
  }

  public ASTStateChart getStateChart() {
    return stateChart;
  }

  @Override
  public void accept(StateChartLanguageBaseVisitor visitor) {
    visitor.traverse(this);
  }
}
