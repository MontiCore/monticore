/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2016, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.grammar.symboltable;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.transformation.GrammarTransformer;
import de.monticore.modelloader.FileBasedAstProvider;
import de.monticore.modelloader.ModelingLanguageModelLoader;
import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

/**
 * @author  Pedram Mir Seyed Nazari
 */
public class EssentialMontiCoreGrammarModelLoader extends ModelingLanguageModelLoader<ASTMCGrammar> {

  public EssentialMontiCoreGrammarModelLoader(EssentialMontiCoreGrammarLanguage modelingLanguage) {
    super(modelingLanguage);
    FileBasedAstProvider<ASTMCGrammar> astProvider = new FileBasedAstProvider<>(modelingLanguage);
    setAstProvider(modelCoordinate -> {
      ASTMCGrammar mcGrammar = astProvider.getRootNode(modelCoordinate);
      GrammarTransformer.transform(mcGrammar);
      return mcGrammar;
    });
  }

  @Override
  protected void createSymbolTableFromAST(ASTMCGrammar ast, String modelName, MutableScope
      enclosingScope, ResolverConfiguration resolverConfiguration) {

    final EssentialMontiCoreGrammarSymbolTableCreator symbolTableCreator =
        getModelingLanguage().getSymbolTableCreator(resolverConfiguration, enclosingScope).orElse(null);

    if (symbolTableCreator != null) {
      Log.debug("Start creation of symbol table for model \"" + modelName + "\".",
          EssentialMontiCoreGrammarSymbolTableCreator.class.getSimpleName());
      final Scope scope = symbolTableCreator.createFromAST(ast);

      if (!(scope instanceof ArtifactScope)) {
        Log.warn("0xA1033 Top scope of model " + modelName + " is expected to be a compilation scope, but"
            + " is scope \"" + scope.getName() + "\"");
      }

      Log.debug("Created symbol table for model \"" + modelName + "\".", EssentialMontiCoreGrammarSymbolTableCreator.class
          .getSimpleName());
    }
    else {
      Log.warn("0xA1034 No symbol created, because '" + getModelingLanguage().getName()
          + "' does not define a symbol table creator.");
    }

  }

  @Override
  public EssentialMontiCoreGrammarLanguage getModelingLanguage() {
    return (EssentialMontiCoreGrammarLanguage) super.getModelingLanguage();
  }
}
