/* (c) https://github.com/MontiCore/monticore */

package de.monticore.modelloader;

import com.google.common.base.Charsets;
import de.monticore.IModelingLanguage;
import de.monticore.ast.ASTNode;
import de.monticore.io.paths.ModelCoordinate;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Optional;

/**
 * Builds ASTs by going straight to the hard disk and reading in the model.
 *
 */
public final class ParserBasedAstProvider<T extends ASTNode> implements AstProvider<T> {

  private final IModelingLanguage<?> modelingLanguage;

  public ParserBasedAstProvider(IModelingLanguage<?> modelingLanguage) {
    this.modelingLanguage = modelingLanguage;
  }

  @Override public T getRootNode(ModelCoordinate modelCoordinate) {
    Optional<T> ast = Optional.empty();

    try {
      Log.debug("Start parsing model " + modelCoordinate + ".",
          ParserBasedAstProvider.class.getSimpleName());
  
      URL loc = modelCoordinate.getLocation();
      if (!"jar".equals(loc.getProtocol())){
        if(loc.getFile().charAt(2) == ':'){
          String filename = URLDecoder.decode(loc.getFile(),  "UTF-8");
          ast = (Optional<T>) modelingLanguage.getParser().parse(filename.substring(1));
        } else {
           ast = (Optional<T>) modelingLanguage.getParser().parse(loc.getFile());
        }
      } else {
        Reader reader = new InputStreamReader(loc.openStream(), Charsets.UTF_8.name());
        ast = (Optional<T>) modelingLanguage.getParser().parse(reader,modelCoordinate.getQualifiedBaseName());
      }

      if (ast.isPresent()) {
        Log.debug("Parsed model " + modelCoordinate + " successfully.",
            ParserBasedAstProvider.class.getSimpleName());
      }
      else {
        Log.error("0xA1025 Could not parse model '" + modelCoordinate + "' of the grammar "
            + "language " + modelingLanguage.getName() + ". There seem to be syntactical errors.");
      }
    }
    catch (IOException e) {
      Log.error("0xA1026 I/O problem while parsing model '" + modelCoordinate + "' of the grammar "
          + "language " + modelingLanguage.getName(), e);
    }

    return ast.get();
  }
}
