/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.transitive.transhost._symboltable;

import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import mc.embedding.transitive.transhost.TransHostMill;
import mc.embedding.transitive.transhost._ast.ASTTransStart;
import mc.embedding.transitive.transhost._parser.TransHostParser;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

public class TransHostGlobalScope extends TransHostGlobalScopeTOP {

  public TransHostGlobalScope(MCPath symbolPath,
                              String modelFileExtension) {
    super(symbolPath, modelFileExtension);
  }

  public TransHostGlobalScope() {
  }

  @Override public TransHostGlobalScope getRealThis() {
    return this;
  }

  @Override public void loadFileForModelName(String modelName) {
    super.loadFileForModelName(modelName);
    String fileExt = "transhost";
    Optional<URL> location = getSymbolPath().find(modelName, fileExt);
    String filePath = Paths.get(Names.getPathFromPackage(modelName) + "." + fileExt).toString();
    if (!isFileLoaded(filePath)) {
      addLoadedFile(filePath);
      if (location.isPresent()) {
        ASTTransStart parse = parse(location.get().getPath());
        TransHostMill.scopesGenitorDelegator().createFromAST(parse);
      }
    }
    else {
      Log.debug("Already tried to load model for '" + modelName
          + "'. If model exists, continue with cached version.",
        "TransHostGlobalScope");
    }
  }

  private ASTTransStart parse(String model) {
    try {
      Optional<ASTTransStart> ast = new TransHostParser().parse(new FileReader(model));
      if (ast.isPresent()) {
        return ast.get();
      }
    }
    catch (IOException e) {
    }
    return null;
  }

}
