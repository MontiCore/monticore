/* generated from model Grammar_WithConcepts */
/* generated by template core.Class*/

/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.grammar_withconcepts._symboltable;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar_withconcepts._parser.Grammar_WithConceptsParser;
import de.monticore.io.paths.MCPath;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;


public class Grammar_WithConceptsGlobalScope extends Grammar_WithConceptsGlobalScopeTOP {
  public Grammar_WithConceptsGlobalScope(MCPath symbolPath, String modelFileExtension) {
    super(symbolPath, modelFileExtension);
  }

  public Grammar_WithConceptsGlobalScope() {
    super();
  }

  @Override
  public Grammar_WithConceptsGlobalScope getRealThis() {
    return this;
  }

  @Override
  public  void loadFileForModelName (String modelName)  {
    Optional<URL> location = getSymbolPath().find(modelName, "mc4");
    if(location.isPresent() && !isFileLoaded(location.get().toString())){
      addLoadedFile(location.get().toString());
      ASTMCGrammar ast = parse(location.get().getFile());
      IGrammar_WithConceptsArtifactScope as = new Grammar_WithConceptsPhasedSTC().createFromAST(ast);
      addSubScope(as);
    }
  }

  private ASTMCGrammar parse(String model){
    try {
      Optional<ASTMCGrammar> optAST = new Grammar_WithConceptsParser().parse(new FileReader(model));
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
