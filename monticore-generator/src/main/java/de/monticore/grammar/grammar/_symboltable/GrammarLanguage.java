/* (c)  https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar._symboltable;

import de.monticore.antlr4.MCConcreteParser;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;

public class GrammarLanguage extends GrammarLanguageTOP {

  public GrammarLanguage(String langName, String fileEnding) {
    super(langName, fileEnding);
  }

  @Override
  public MCConcreteParser getParser() {
    return new Grammar_WithConceptsParser();
  }

  @Override
  protected GrammarModelLoader provideModelLoader() {
    return new GrammarModelLoader(this);
  }

}
