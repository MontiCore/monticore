/* (c) https://github.com/MontiCore/monticore */
package mc.embedding.composite._symboltable;

import de.monticore.io.paths.MCPath;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.utils.Names;
import de.se_rwth.commons.logging.Log;
import mc.embedding.composite.CompositeMill;
import mc.embedding.composite._parser.CompositeParser;
import mc.embedding.embedded._symboltable.TextSymbol;
import mc.embedding.host._ast.ASTHost;
import mc.embedding.host._symboltable.ContentSymbol;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CompositeGlobalScope extends CompositeGlobalScopeTOP{

  public CompositeGlobalScope(MCPath symbolPath, String fileExt) {
    super(symbolPath, fileExt);
  }

  public CompositeGlobalScope(){
    super();
  }

  @Override public List<ContentSymbol> resolveAdaptedContent(boolean foundSymbols,
                                                              String symbolName, AccessModifier modifier, Predicate<ContentSymbol> predicate) {
    Collection<TextSymbol> symbols = resolveTextMany(foundSymbols, symbolName, modifier, x -> true);
    return symbols.stream().map(s -> new Text2ContentAdapter(s)).collect(Collectors.toList());
  }
  
  @Override public CompositeGlobalScope getRealThis(){
    return this;
  }

  @Override public void loadFileForModelName(String modelName) {
    String fileExt = "host";
    Optional<URL> location = getSymbolPath().find(modelName, fileExt);
    String filePath = Paths.get(Names.getPathFromPackage(modelName) + "." + fileExt).toString();
    if (!isFileLoaded(filePath)) {
      addLoadedFile(filePath);
      if (location.isPresent()) {
        ASTHost parse = parse(location.get().getPath());
        CompositeMill.scopesGenitorDelegator().createFromAST(parse);
      }
    }
    else {
      Log.debug("Already tried to load model for '" + modelName
              + "'. If model exists, continue with cached version.",
          "CompositeGlobalScope");
    }
  }

  private ASTHost parse(String model) {
    try {
      Optional<ASTHost> ast = new CompositeParser().parse(new FileReader(model));
      if (ast.isPresent()) {
        return ast.get();
      }
    }
    catch (IOException e) {
    }
    return null;
  }
}
