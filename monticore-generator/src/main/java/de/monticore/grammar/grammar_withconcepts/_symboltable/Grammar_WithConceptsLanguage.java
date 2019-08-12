
package de.monticore.grammar.grammar_withconcepts._symboltable;

import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;

public class Grammar_WithConceptsLanguage extends Grammar_WithConceptsLanguageTOP {

  public static final String FILE_ENDING = "mc4";

  public Grammar_WithConceptsLanguage(String langName, String fileEnding) {
    super(langName, fileEnding);
  }

  public Grammar_WithConceptsLanguage() {
    super("MontiCore Grammar Language", FILE_ENDING);
  }

  @Override
  public Grammar_WithConceptsParser getParser() {
    return new Grammar_WithConceptsParser();
  }

  @Override
  protected Grammar_WithConceptsModelLoader provideModelLoader() {
    return new Grammar_WithConceptsModelLoader(this);
  }

}
