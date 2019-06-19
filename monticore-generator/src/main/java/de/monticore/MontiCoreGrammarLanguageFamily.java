/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.grammar.grammar._symboltable.GrammarLanguage;

public class MontiCoreGrammarLanguageFamily extends ModelingLanguageFamily {

  public MontiCoreGrammarLanguageFamily() {
    addModelingLanguage(new GrammarLanguage());
  }
}
