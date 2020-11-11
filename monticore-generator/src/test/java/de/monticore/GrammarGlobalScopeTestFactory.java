/* (c) https://github.com/MontiCore/monticore */

package de.monticore;


import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsScope;

import java.nio.file.Path;
import java.nio.file.Paths;

import static de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill.grammar_WithConceptsGlobalScope;

public class GrammarGlobalScopeTestFactory {


  public static Grammar_WithConceptsGlobalScope create() {
    IGrammar_WithConceptsGlobalScope scope = grammar_WithConceptsGlobalScope();
    // reset global scope
    scope.clear();
    scope.setModelFileExtension("mc4");
    scope.getModelPath().addEntry(Paths.get("src/test/resources"));
    return (Grammar_WithConceptsGlobalScope) scope;
  }

}
