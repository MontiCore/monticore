/* (c) https://github.com/MontiCore/monticore */
package mc.embedding.external.composite._symboltable;

import de.monticore.io.FileReaderWriter;
import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import mc.embedding.external.composite.CompositeMill;
import mc.embedding.external.composite._parser.CompositeParser;
import mc.embedding.external.embedded._symboltable.TextSymbol;
import mc.embedding.external.host._ast.ASTHost;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CompositeGlobalScope extends CompositeGlobalScopeTOP {

  public CompositeGlobalScope(MCPath symbolPath) {
    super(symbolPath, "host");
  }

  public CompositeGlobalScope(MCPath symbolPath, String modelFileExtension) {
    super(symbolPath, modelFileExtension);
  }

  public CompositeGlobalScope(){
    super();
  }

  @Override public List<ContentSymbol> resolveAdaptedContent(boolean foundSymbols,
      String symbolName, AccessModifier modifier, Predicate<ContentSymbol> predicate) {
    Collection<TextSymbol> symbols = resolveTextMany(foundSymbols, symbolName, modifier, x -> true);
    return symbols.stream().map(s -> new Text2ContentAdapter(s)).collect(Collectors.toList());
  }

  @Override public CompositeGlobalScope getRealThis() {
    return this;
  }

  @Override public void loadFileForModelName(String modelName) {
    String fileExt = "host";
    Optional<URL> location = getSymbolPath().find(modelName, fileExt);
    String filePath = Paths.get(Names.getPathFromPackage(modelName) + "." + fileExt).toString();
    if (!isFileLoaded(filePath)) {
      addLoadedFile(filePath);
      if (location.isPresent()) {
        ASTHost parse = parse(location.get());
        CompositeMill.scopesGenitorDelegator().createFromAST(parse);
      }
    }
    else {
      Log.debug("Already tried to load model for '" + modelName
          + "'. If model exists, continue with cached version.",
        "CompositeGlobalScope");
    }
  }

  private ASTHost parse(URL model) {
    try (Reader reader = FileReaderWriter.getReader(model)){
      Optional<ASTHost> ast = new CompositeParser().parse(reader);
      if (ast.isPresent()) {
        return ast.get();
      }
    }
    catch (IOException e) {
    }
    return null;
  }
}
