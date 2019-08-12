/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar_withconcepts._symboltable;

import java.util.Collection;

public class Grammar_WithConceptsModelLoader extends Grammar_WithConceptsModelLoaderTOP {

  public Grammar_WithConceptsModelLoader(Grammar_WithConceptsLanguage language) {
    super(language);
  }

  @Override
  protected void showWarningIfParsedModels(Collection<?> asts, String modelName) {
    // Do nothing
  }

}
