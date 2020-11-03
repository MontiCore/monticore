/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.embedded._symboltable;

import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import mc.embedding.embedded.EmbeddedMill;
import mc.embedding.embedded._ast.ASTText;
import mc.embedding.embedded._parser.EmbeddedParser;

import java.io.IOException;
import java.util.Optional;

public class EmbeddedGlobalScope extends EmbeddedGlobalScopeTOP {

  public EmbeddedGlobalScope(ModelPath modelPath,
      String modelFileExtension) {
    super(modelPath, modelFileExtension);
  }

  public EmbeddedGlobalScope() {
  }

  @Override public EmbeddedGlobalScope getRealThis() {
    return this;
  }

  @Override public void loadFileForModelName(String modelName, String symbolName) {
    super.loadFileForModelName(modelName, symbolName);
    ModelCoordinate modelCoordinate = ModelCoordinates
        .createQualifiedCoordinate(modelName, getModelFileExtension());
    String filePath = modelCoordinate.getQualifiedPath().toString();
    if (!isFileLoaded(filePath)) {
      addLoadedFile(filePath);
      getModelPath().resolveModel(modelCoordinate);
      if (modelCoordinate.hasLocation()) {
        ASTText parse = parse(modelCoordinate);
        EmbeddedMill.embeddedSymbolTableCreatorDelegator().createFromAST(parse);
      }
    }
    else {
      Log.debug("Already tried to load model for '" + symbolName
              + "'. If model exists, continue with cached version.",
          "CompositeGlobalScope");
    }
  }

  private ASTText parse(ModelCoordinate model) {
    try {
      Optional<ASTText> ast = new EmbeddedParser().parse(ModelCoordinates.getReader(model));
      if (ast.isPresent()) {
        return ast.get();
      }
    }
    catch (IOException e) {
    }
    return null;
  }

}
