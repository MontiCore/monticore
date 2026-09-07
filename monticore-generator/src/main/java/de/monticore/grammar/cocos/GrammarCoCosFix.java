/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar_withconcepts._cocos.Grammar_WithConceptsCoCoChecker;

// TODO: Replace with GrammarCoCos once 7.9.0 is released
public class GrammarCoCosFix extends GrammarCoCos {
  @Override
  public Grammar_WithConceptsCoCoChecker getCoCoChecker() {
    Grammar_WithConceptsCoCoChecker checker = super.getCoCoChecker();
    checker.addCoCo(new LeftRecursivePriorityReductionFix()); // TODO: Remove once 7.9.0 is released
    return checker;
  }
}
