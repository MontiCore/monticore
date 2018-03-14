/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.symboltable;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.transformation.GrammarTransformer;
import de.monticore.modelloader.FileBasedAstProvider;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class MontiCoreGrammarModelLoader extends ModelingLanguageModelLoader<ASTMCGrammar> {

  public MontiCoreGrammarModelLoader(MontiCoreGrammarLanguage modelingLanguage) {
    super(modelingLanguage);
    FileBasedAstProvider<ASTMCGrammar> astProvider = new FileBasedAstProvider<>(modelingLanguage);
    setAstProvider(modelCoordinate -> {
      ASTMCGrammar mcGrammar = astProvider.getRootNode(modelCoordinate);
      GrammarTransformer.transform(mcGrammar);
      return mcGrammar;
    });
  }
  
  @Override
  public MontiCoreGrammarLanguage getModelingLanguage() {
    return (MontiCoreGrammarLanguage) super.getModelingLanguage();
  }

  @Override
  protected void createSymbolTableFromAST(ASTMCGrammar ast, String modelName, MutableScope
      enclosingScope, ResolvingConfiguration resolvingConfiguration) {

    final MontiCoreGrammarSymbolTableCreator symbolTableCreator =
        getModelingLanguage().getSymbolTableCreator(resolvingConfiguration, enclosingScope).orElse(null);

    if (symbolTableCreator != null) {
      Log.debug("Start creation of symbol table for model \"" + modelName + "\".",
          MontiCoreGrammarSymbolTableCreator.class.getSimpleName());
      final Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ArtifactScope)) {
        Log.warn("0xA1033 Top scope of model " + modelName + " is expected to be a compilation scope, but"
            + " is scope \"" + scope.getName() + "\"");
      }

      Log.debug("Created symbol table for model \"" + modelName + "\".", MontiCoreGrammarSymbolTableCreator.class
          .getSimpleName());
    }
    else {
      Log.warn("0xA1034 No symbol created, because '" + getModelingLanguage().getName()
          + "' does not define a symbol table creator.");
    }

  }

}
