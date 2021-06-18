/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.host._symboltable;

import de.monticore.io.paths.MCPath;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;
import mc.embedding.host.HostMill;
import mc.embedding.host._parser.HostParser;
import mc.embedding.host._ast.ASTHost;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

public class HostGlobalScope extends HostGlobalScopeTOP {

  public HostGlobalScope(MCPath symbolPath,
                         String modelFileExtension) {
    super(symbolPath, modelFileExtension);
  }

  public HostGlobalScope() {
  }

  @Override public HostGlobalScope getRealThis() {
    return this;
  }

  @Override public void loadFileForModelName(String modelName) {
    super.loadFileForModelName(modelName);
    String fileExt = "host";
    Optional<URL> location = getSymbolPath().find(modelName, fileExt);
    String filePath = Paths.get(Names.getPathFromPackage(modelName) + "." + fileExt).toString();
    if (!isFileLoaded(filePath)) {
      addLoadedFile(filePath);
      if (location.isPresent()) {
        ASTHost parse = parse(location.get().getPath());
        HostMill.scopesGenitorDelegator().createFromAST(parse);
      }
    }
    else {
      Log.debug("Already tried to load model for '" + modelName
          + "'. If model exists, continue with cached version.",
        "HostGlobalScope");
    }
  }

  private ASTHost parse(String model) {
    try {
      Optional<ASTHost> ast = new HostParser().parse(new FileReader(model));
      if (ast.isPresent()) {
        return ast.get();
      }
    }
    catch (IOException e) {
    }
    return null;
  }


}
