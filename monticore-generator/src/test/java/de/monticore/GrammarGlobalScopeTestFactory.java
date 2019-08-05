/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsLanguage;
import de.monticore.io.paths.ModelPath;

import java.nio.file.Paths;

public class GrammarGlobalScopeTestFactory {

  public static Grammar_WithConceptsGlobalScope create() {
    return create(new Grammar_WithConceptsLanguage());
  }

  public static Grammar_WithConceptsGlobalScope createUsingEssentialMCLanguage() {
    return create(new Grammar_WithConceptsLanguage());
  }


  private static Grammar_WithConceptsGlobalScope create(Grammar_WithConceptsLanguage grammarLanguage) {

    return  new Grammar_WithConceptsGlobalScope(new ModelPath(Paths.get("src/test/resources")),
        grammarLanguage);
  }

}
