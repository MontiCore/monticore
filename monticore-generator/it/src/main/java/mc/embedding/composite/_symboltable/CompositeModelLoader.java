/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.composite._symboltable;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import mc.embedding.host._ast.ASTHost;

public class CompositeModelLoader extends de.monticore.modelloader.ModelingLanguageModelLoader<ASTHost> {

  public CompositeModelLoader(CompositeLanguage language) {
    super(language);
  }

  protected void createSymbolTableFromAST(ASTHost ast, String modelName, MutableScope enclosingScope, ResolvingConfiguration resolvingConfiguration) {
    final CompositeSymbolTableCreator symbolTableCreator =
        getModelingLanguage().getSymbolTableCreator(resolvingConfiguration, enclosingScope).orElse(null);

    if (symbolTableCreator != null) {
      Log.debug("Start creation of symbol table for model \"" + modelName + "\".",
          CompositeModelLoader.class.getSimpleName());
      final Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ArtifactScope)) {
        Log.warn("Top scope of model " + modelName + " is expected to be an artifact scope, but"
            + " is scope \"" + scope.getName() + "\"");
      }

      Log.debug("Created symbol table for model \"" + modelName + "\".", CompositeModelLoader.class.getSimpleName());
    }
    else {
      Log.warn("No symbol created, because '" + getModelingLanguage().getName()
          + "' does not define a symbol table creator.");
    }
  }

  @Override
  public CompositeLanguage getModelingLanguage() {
    return (CompositeLanguage) super.getModelingLanguage();
  }
}
