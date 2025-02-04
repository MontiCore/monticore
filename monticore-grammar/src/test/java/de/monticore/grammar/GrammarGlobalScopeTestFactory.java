/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar;


import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.grammar.grammar_withconcepts._symboltable.IGrammar_WithConceptsGlobalScope;

import java.nio.file.Paths;

public class GrammarGlobalScopeTestFactory {


  public static Grammar_WithConceptsGlobalScope create() {
    IGrammar_WithConceptsGlobalScope scope = Grammar_WithConceptsMill.globalScope();
    // reset global scope
    scope.clear();
    scope.setFileExt("mc4");
    scope.getSymbolPath().addEntry(Paths.get("src/test/resources"));
    scope.getSymbolPath().addEntry(Paths.get("target/test/resources"));
    return (Grammar_WithConceptsGlobalScope) scope;
  }

}
