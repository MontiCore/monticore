/* (c) https://github.com/MontiCore/monticore */

package de.monticore.symboltable.mocks.languages.entity;

import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.mocks.languages.entity.asts.ASTEntityCompilationUnit;
import de.se_rwth.commons.logging.Log;

public class EntityLanguageModelLoader extends ModelingLanguageModelLoader<ASTEntityCompilationUnit> {

  public EntityLanguageModelLoader(EntityLanguage modelingLanguage) {
    super(modelingLanguage);
  }

  @Override
  protected void createSymbolTableFromAST(final ASTEntityCompilationUnit ast, final String modelName,
      final MutableScope enclosingScope, final ResolvingConfiguration resolvingConfiguration) {
    final EntityLanguageSymbolTableCreator symbolTableCreator = getModelingLanguage().getSymbolTableCreator
        (resolvingConfiguration, enclosingScope).orElse(null);

    if (symbolTableCreator != null) {
      Log.debug("Start creation of symbol table for model \"" + modelName + "\".",
          EntityLanguageModelLoader.class
              .getSimpleName());
      final Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ArtifactScope)) {
        Log.warn("0xA1050 Top scope of model " + modelName + " is expected to be a compilation scope, but"
            + " is scope \"" + scope.getName() + "\"");
      }

      Log.debug("Created symbol table for model \"" + modelName + "\".", EntityLanguageModelLoader.class
          .getSimpleName());
    }
    else {
      Log.warn("0xA1051 No symbol created, because '" + getModelingLanguage().getName()
          + "' does not define a symbol table creator.");
    }
  }

  @Override
  public EntityLanguage getModelingLanguage() {
    return (EntityLanguage) super.getModelingLanguage();
  }
}
