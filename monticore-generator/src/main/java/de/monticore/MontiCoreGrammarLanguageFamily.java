/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.grammar.symboltable.MontiCoreGrammarLanguage;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class MontiCoreGrammarLanguageFamily extends ModelingLanguageFamily {

  public MontiCoreGrammarLanguageFamily() {
    addModelingLanguage(new MontiCoreGrammarLanguage());
  }
}
