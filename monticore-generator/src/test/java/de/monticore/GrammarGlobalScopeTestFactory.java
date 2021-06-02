/* (c) https://github.com/MontiCore/monticore */

package de.monticore;


import de.monticore.grammar.grammarfamily.GrammarFamilyMill;
import de.monticore.grammar.grammarfamily._symboltable.GrammarFamilyGlobalScope;
import de.monticore.grammar.grammarfamily._symboltable.IGrammarFamilyGlobalScope;

import java.nio.file.Paths;

public class GrammarGlobalScopeTestFactory {

  public static GrammarFamilyGlobalScope create() {
    IGrammarFamilyGlobalScope scope = GrammarFamilyMill.globalScope();
    // reset global scope
    scope.clear();
    scope.getModelPath().addEntry(Paths.get("src/test/resources"));
    return (GrammarFamilyGlobalScope) scope;
  }

}
