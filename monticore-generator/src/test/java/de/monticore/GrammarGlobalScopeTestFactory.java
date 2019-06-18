/* (c) https://github.com/MontiCore/monticore */

package de.monticore;

import java.nio.file.Paths;

import de.monticore.grammar.grammar._symboltable._symboltable.MontiCoreGrammarLanguage;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.ResolvingConfiguration;

public class GrammarGlobalScopeTestFactory {

  public static GlobalScope create() {
    return create(new MontiCoreGrammarLanguage());
  }

  public static GlobalScope createUsingEssentialMCLanguage() {
    return create(new MontiCoreGrammarLanguage());
  }


  private static GlobalScope create(ModelingLanguage grammarLanguage) {
    final ResolvingConfiguration resolvingConfiguration = new ResolvingConfiguration();
    resolvingConfiguration.addDefaultFilters(grammarLanguage.getResolvingFilters());

    return  new GlobalScope(new ModelPath(Paths.get("src/test/resources")),
        grammarLanguage, resolvingConfiguration);
  }

}
