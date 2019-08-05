/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.grammar.symboltable.MontiCoreGrammarLanguage;

public class MontiCoreGrammarLanguageFamily extends ModelingLanguageFamily {

  public MontiCoreGrammarLanguageFamily() {
    addModelingLanguage(new MontiCoreGrammarLanguage());
  }
}
