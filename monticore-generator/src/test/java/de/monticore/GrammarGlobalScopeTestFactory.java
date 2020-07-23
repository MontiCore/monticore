/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import de.monticore.grammar.grammar_withconcepts._symboltable.Grammar_WithConceptsGlobalScope;
import de.monticore.io.paths.ModelPath;

import java.nio.file.Paths;

public class GrammarGlobalScopeTestFactory {


  public static Grammar_WithConceptsGlobalScope create() {

    return  new Grammar_WithConceptsGlobalScope(new ModelPath(Paths.get("src/test/resources")),
        "mc4");
  }

}
