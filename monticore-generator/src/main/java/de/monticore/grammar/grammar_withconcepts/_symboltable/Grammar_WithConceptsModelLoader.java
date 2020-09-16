/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.grammar_withconcepts._symboltable;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.modelloader.AstProvider;

import java.util.List;

public class Grammar_WithConceptsModelLoader extends Grammar_WithConceptsModelLoaderTOP {

  public Grammar_WithConceptsModelLoader(AstProvider<ASTMCGrammar> astProvider, Grammar_WithConceptsSymbolTableCreatorDelegator symbolTableCreator, String modelFileExtension, String symbolFileExtension) {
    super(astProvider, symbolTableCreator, modelFileExtension, symbolFileExtension);
  }

  @Override
  protected void showWarningIfParsedModels(List<?> asts, String modelName) {
    // Do nothing
  }

}
