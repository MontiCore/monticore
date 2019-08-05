/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.symboltable;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.transformation.GrammarTransformer;
import de.monticore.modelloader.FileBasedAstProvider;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

import static de.monticore.grammar.transformation.GrammarTransformer.transform;
import static de.se_rwth.commons.logging.Log.debug;
import static de.se_rwth.commons.logging.Log.warn;

public class MontiCoreGrammarModelLoader extends ModelingLanguageModelLoader<ASTMCGrammar> {

  public MontiCoreGrammarModelLoader(MontiCoreGrammarLanguage modelingLanguage) {
    super(modelingLanguage);
    FileBasedAstProvider<ASTMCGrammar> astProvider = new FileBasedAstProvider<>(modelingLanguage);
    setAstProvider(modelCoordinate -> {
      ASTMCGrammar mcGrammar = astProvider.getRootNode(modelCoordinate);
      transform(mcGrammar);
      return mcGrammar;
    });
  }

  @Override
  public MontiCoreGrammarLanguage getModelingLanguage() {
    return (MontiCoreGrammarLanguage) super.getModelingLanguage();
  }

  @Override
  protected void createSymbolTableFromAST(ASTMCGrammar ast, String modelName, Scope
          enclosingScope, ResolvingConfiguration resolvingConfiguration) {

    final MontiCoreGrammarSymbolTableCreator symbolTableCreator =
            getModelingLanguage().getSymbolTableCreator(resolvingConfiguration, enclosingScope).orElse(null);

    if (symbolTableCreator != null) {
      debug("Start creation of symbol table for model \"" + modelName + "\".",
              MontiCoreGrammarSymbolTableCreator.class.getSimpleName());
      final Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ArtifactScope)) {
        warn("0xA1033 Top scope of model " + modelName + " is expected to be a compilation scope, but"
                + " is scope \"" + scope.getName() + "\"");
      }

      debug("Created symbol table for model \"" + modelName + "\".", MontiCoreGrammarSymbolTableCreator.class
              .getSimpleName());
    } else {
      warn("0xA1034 No symbol created, because '" + getModelingLanguage().getName()
              + "' does not define a symbol table creator.");
    }

  }

}
