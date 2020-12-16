/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.host._symboltable;

import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import mc.embedding.host.HostMill;
import mc.embedding.host._parser.HostParser;
import mc.embedding.host._ast.ASTHost;

import java.io.IOException;
import java.util.Optional;

public class HostGlobalScope extends HostGlobalScopeTOP {

  public HostGlobalScope(ModelPath modelPath,
      String modelFileExtension) {
    super(modelPath, modelFileExtension);
  }

  public HostGlobalScope() {
  }

  @Override public HostGlobalScope getRealThis() {
    return this;
  }

  @Override public void loadFileForModelName(String modelName) {
    super.loadFileForModelName(modelName);
    ModelCoordinate modelCoordinate = ModelCoordinates
        .createQualifiedCoordinate(modelName, "host");
    String filePath = modelCoordinate.getQualifiedPath().toString();
    if (!isFileLoaded(filePath)) {
      addLoadedFile(filePath);
      getModelPath().resolveModel(modelCoordinate);
      if (modelCoordinate.hasLocation()) {
        ASTHost parse = parse(modelCoordinate);
        HostMill.hostSymbolTableCreatorDelegator().createFromAST(parse);
      }
    }
    else {
      Log.debug("Already tried to load model for '" + modelName
              + "'. If model exists, continue with cached version.",
          "CompositeGlobalScope");
    }
  }

  private ASTHost parse(ModelCoordinate model) {
    try {
      Optional<ASTHost> ast = new HostParser().parse(ModelCoordinates.getReader(model));
      if (ast.isPresent()) {
        return ast.get();
      }
    }
    catch (IOException e) {
    }
    return null;
  }


}
