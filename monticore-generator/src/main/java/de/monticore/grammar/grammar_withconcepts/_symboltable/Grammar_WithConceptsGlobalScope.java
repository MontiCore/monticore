/* generated from model Grammar_WithConcepts */
/* generated by template core.Class*/

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts._symboltable;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts.Grammar_WithConceptsMill;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.Reader;
import java.util.*;


public class Grammar_WithConceptsGlobalScope extends Grammar_WithConceptsGlobalScopeTOP {
  public Grammar_WithConceptsGlobalScope(ModelPath modelPath, String modelFileExtension) {
    super(modelPath, modelFileExtension);
  }

  public Grammar_WithConceptsGlobalScope() {
    super();
  }

  @Override
  public Grammar_WithConceptsGlobalScope getRealThis() {
    return this;
  }

  @Override
  public  void loadFileForModelName (String modelName, String symbolName)  {
    // 1. call super implementation to start with employing the DeSer
    super.loadFileForModelName(modelName, symbolName);

    ModelCoordinate model = ModelCoordinates.createQualifiedCoordinate(modelName, getModelFileExtension());
    String filePath = model.getQualifiedPath().toString();
    if (!isFileLoaded(filePath)) {

      // 2. calculate potential location of model file and try to find it in model path
      model = getModelPath().resolveModel(model);

      // 3. if the file was found, parse the model and create its symtab
      if (model.hasLocation()) {
        ASTMCGrammar ast = parse(model);
        Grammar_WithConceptsArtifactScope artScope = Grammar_WithConceptsMill.grammar_WithConceptsSymbolTableCreatorDelegator().createFromAST(ast);
        addSubScope(artScope);
        addLoadedFile(filePath);
      }
    }
  }

  private ASTMCGrammar parse(ModelCoordinate model){
    try {
      Reader reader = ModelCoordinates.getReader(model);
      Optional<ASTMCGrammar> optAST = new Grammar_WithConceptsParser().parse(reader);
      if(optAST.isPresent()){
        return optAST.get();
      }
    }
    catch (IOException e) {
      Log.error("0x1A234 Error while parsing model", e);
    }
    return null;
  }


}
