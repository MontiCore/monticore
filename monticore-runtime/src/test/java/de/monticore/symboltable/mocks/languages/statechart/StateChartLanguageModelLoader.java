/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.statechart;

import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.mocks.languages.statechart.asts.ASTStateChartCompilationUnit;
import de.se_rwth.commons.logging.Log;

public class StateChartLanguageModelLoader extends ModelingLanguageModelLoader<ASTStateChartCompilationUnit> {

  public StateChartLanguageModelLoader(StateChartLanguage modelingLanguage) {
    super(modelingLanguage);
  }

  @Override
  protected void createSymbolTableFromAST(ASTStateChartCompilationUnit ast, String modelName, MutableScope enclosingScope, ResolvingConfiguration resolvingConfiguration) {
    final StateChartLanguageSymbolTableCreator symbolTableCreator = getModelingLanguage().getSymbolTableCreator
        (resolvingConfiguration, enclosingScope).orElse(null);

    if (symbolTableCreator != null) {
      Log.debug("Start creation of symbol table for model \"" + modelName + "\".",
          StateChartLanguageModelLoader.class
              .getSimpleName());
      final Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ArtifactScope)) {
        Log.warn("0xA1054 Top scope of model " + modelName + " is expected to be a compilation scope, but"
            + " is scope \"" + scope.getName() + "\"");
      }

      Log.debug("Created symbol table for model \"" + modelName + "\".", StateChartLanguageModelLoader.class
          .getSimpleName());
    }
    else {
      Log.warn("0xA1055 No symbol created, because '" + getModelingLanguage().getName()
          + "' does not define a symbol table creator.");
    }

  }

  @Override
  public StateChartLanguage getModelingLanguage() {
    return (StateChartLanguage) super.getModelingLanguage();
  }
}
