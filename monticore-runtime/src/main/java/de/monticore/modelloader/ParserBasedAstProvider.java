/* (c) https://github.com/MontiCore/monticore */

package de.monticore.modelloader;

import com.google.common.base.Charsets;
import de.monticore.IModelingLanguage;
import de.monticore.antlr4.MCConcreteParser;
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

  private final MCConcreteParser parser;

  private final String languageName;

  public ParserBasedAstProvider(MCConcreteParser parser, String languageName) {
    this.parser = parser;
    this.languageName = languageName;
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
          ast = (Optional<T>) parser.parse(filename.substring(1));
        } else {
           ast = (Optional<T>) parser.parse(loc.getFile());
        }
      } else {
        Reader reader = new InputStreamReader(loc.openStream(), Charsets.UTF_8.name());
        ast = (Optional<T>) parser.parse(reader,modelCoordinate.getQualifiedBaseName());
      }

      if (ast.isPresent()) {
        Log.debug("Parsed model " + modelCoordinate + " successfully.",
            ParserBasedAstProvider.class.getSimpleName());
      }
      else {
        Log.error("0xA1025 Could not parse model '" + modelCoordinate + "' of the grammar "
            + "language " + languageName + ". There seem to be syntactical errors.");
      }
    }
    catch (IOException e) {
      Log.error("0xA1026 I/O problem while parsing model '" + modelCoordinate + "' of the grammar "
          + "language " + languageName, e);
    }

    return ast.get();
  }
}
