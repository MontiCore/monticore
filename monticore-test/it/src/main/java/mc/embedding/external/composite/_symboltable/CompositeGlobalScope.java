/* (c) https://github.com/MontiCore/monticore */
package mc.embedding.external.composite._symboltable;

import de.monticore.io.paths.ModelCoordinate;
import de.monticore.io.paths.ModelCoordinates;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.se_rwth.commons.logging.Log;
import mc.embedding.external.composite.CompositeMill;
import mc.embedding.external.composite._parser.CompositeParser;
import mc.embedding.external.embedded._symboltable.TextSymbol;
import mc.embedding.external.host._ast.ASTHost;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CompositeGlobalScope extends CompositeGlobalScopeTOP {

  public CompositeGlobalScope(ModelPath modelPath) {
    super(modelPath, "host");
  }

  public CompositeGlobalScope(ModelPath modelPath, String modelFileExtension) {
    super(modelPath, modelFileExtension);
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

  @Override public void loadFileForModelName(String modelName, String symbolName) {
    super.loadFileForModelName(modelName, symbolName);
    ModelCoordinate modelCoordinate = ModelCoordinates
        .createQualifiedCoordinate(modelName, getModelFileExtension());
    String filePath = modelCoordinate.getQualifiedPath().toString();
    if (!isFileLoaded(filePath)) {
      addLoadedFile(filePath);
      getModelPath().resolveModel(modelCoordinate);
      if (modelCoordinate.hasLocation()) {
        ASTHost parse = parse(modelCoordinate);
        CompositeMill.compositeSymbolTableCreatorDelegator().createFromAST(parse);
      }
    }
    else {
      Log.debug("Already tried to load model for '" + symbolName
              + "'. If model exists, continue with cached version.",
          "CompositeGlobalScope");
    }
  }

  private ASTHost parse(ModelCoordinate model) {
    try {
      Optional<ASTHost> ast = new CompositeParser().parse(ModelCoordinates.getReader(model));
      if (ast.isPresent()) {
        return ast.get();
      }
    }
    catch (IOException e) {
    }
    return null;
  }
}
