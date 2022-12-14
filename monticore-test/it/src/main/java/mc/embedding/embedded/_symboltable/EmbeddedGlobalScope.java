/* (c) https://github.com/MontiCore/monticore */

package mc.embedding.embedded._symboltable;

import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import mc.embedding.embedded.EmbeddedMill;
import mc.embedding.embedded._ast.ASTText;
import mc.embedding.embedded._parser.EmbeddedParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

public class EmbeddedGlobalScope extends EmbeddedGlobalScopeTOP {

  public EmbeddedGlobalScope(MCPath symbolPath,
                             String modelFileExtension) {
    super(symbolPath, modelFileExtension);
  }

  public EmbeddedGlobalScope() {
  }

  @Override public EmbeddedGlobalScope getRealThis() {
    return this;
  }

  @Override public void loadFileForModelName(String modelName) {
    super.loadFileForModelName(modelName);
    String fileExt = "embedded";
    Optional<URL> location = getSymbolPath().find(modelName, fileExt);
    String filePath = Paths.get(Names.getPathFromPackage(modelName) + "." + fileExt).toString();
    if (!isFileLoaded(filePath)) {
      addLoadedFile(filePath);
      if (location.isPresent()) {
        ASTText parse = parse(location.get());
        EmbeddedMill.scopesGenitorDelegator().createFromAST(parse);
      }
    }
    else {
      Log.debug("Already tried to load model for '" + modelName
          + "'. If model exists, continue with cached version.",
        "EmbeddedGlobalScope");
    }
  }

  private ASTText parse(URL model) {
    try (Reader reader = FileReaderWriter.getReader(model)){
      Optional<ASTText> ast = new EmbeddedParser().parse(reader);
      if (ast.isPresent()) {
        return ast.get();
      }
    }
    catch (IOException e) {
    }
    return null;
  }

}
