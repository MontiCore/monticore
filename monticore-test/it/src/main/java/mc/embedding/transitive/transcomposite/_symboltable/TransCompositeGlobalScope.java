/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive.transcomposite._symboltable;

import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.logging.Log;
import mc.embedding.transitive.transcomposite.TransCompositeMill;
import mc.embedding.transitive.transcomposite._parser.TransCompositeParser;
import mc.embedding.transitive.transhost._ast.ASTTransStart;

import java.io.IOException;
import java.util.Optional;

public class TransCompositeGlobalScope extends TransCompositeGlobalScopeTOP {

  public TransCompositeGlobalScope(ModelPath modelPath,
      String modelFileExtension) {
    super(modelPath, modelFileExtension);
  }

  public TransCompositeGlobalScope() {
  }

  @Override public TransCompositeGlobalScope getRealThis() {
    return this;
  }

  @Override public void loadFileForModelName(String modelName) {
    super.loadFileForModelName(modelName);
    ModelCoordinate modelCoordinate = ModelCoordinates
        .createQualifiedCoordinate(modelName, "transhost");
    String filePath = modelCoordinate.getQualifiedPath().toString();
    if (!isFileLoaded(filePath)) {
      addLoadedFile(filePath);
      getModelPath().resolveModel(modelCoordinate);
      if (modelCoordinate.hasLocation()) {
        ASTTransStart parse = parse(modelCoordinate);
        TransCompositeMill.scopesGenitorDelegator().createFromAST(parse);
      }
    }
    else {
      Log.debug("Already tried to load model for '" + modelName
              + "'. If model exists, continue with cached version.",
          "CompositeGlobalScope");
    }
  }

  private ASTTransStart parse(ModelCoordinate model) {
    try {
      Optional<ASTTransStart> ast = new TransCompositeParser().parse(ModelCoordinates.getReader(model));
      if (ast.isPresent()) {
        return ast.get();
      }
    }
    catch (IOException e) {
    }
    return null;
  }

}
