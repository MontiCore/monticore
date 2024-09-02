/* (c) https://github.com/MontiCore/monticore */
package de.monticore.gradle.gen;

import de.monticore.cli.MontiCoreTool;
import de.monticore.gradle.common.GradleLog;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;

public class MCGradleTool extends MontiCoreTool {

  /**
   * Main method that is called from command line and runs the MontiCore tool.
   *
   * @param args The input parameters for configuring the MontiCore tool
   */
  public static void main(String[] args) {
    GradleLog.init();
    Grammar_WithConceptsMill.init();
    new MontiCoreTool().run(args);
  }
}
