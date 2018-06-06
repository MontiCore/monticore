/* (c) https://github.com/MontiCore/monticore */

package de.monticore.modelloader;

import de.monticore.ModelingLanguage;
import de.monticore.ast.ASTNode;
import de.monticore.io.paths.ModelCoordinate;
import de.se_rwth.commons.logging.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.Optional;

import com.google.common.base.Charsets;

/**
 * Builds ASTs by going straight to the hard disk and reading in the model.
 *
 * @author Sebastian Oberhoff, Pedram Mir Seyed Nazari
 */
public final class FileBasedAstProvider<T extends ASTNode> implements AstProvider<T> {

  private final ModelingLanguage modelingLanguage;

  public FileBasedAstProvider(ModelingLanguage modelingLanguage) {
    this.modelingLanguage = modelingLanguage;
  }

  @Override public T getRootNode(ModelCoordinate modelCoordinate) {
    Optional<T> ast = Optional.empty();

    try {
      Log.debug("Start parsing model " + modelCoordinate + ".",
          ModelingLanguageModelLoader.class.getSimpleName());
  
      URL loc = modelCoordinate.getLocation();
      if (!loc.getProtocol().equals("jar")){
        if(loc.getFile().charAt(2) == ':'){
          ast = (Optional<T>) modelingLanguage.getParser().parse(loc.getFile().substring(1));
        } else {
           ast = (Optional<T>) modelingLanguage.getParser().parse(loc.getFile());
        }
      } else {
        Reader reader = new InputStreamReader(loc.openStream(), Charsets.UTF_8.name());
        ast = (Optional<T>) modelingLanguage.getParser().parse(reader,modelCoordinate.getQualifiedBaseName());
      }

      if (ast.isPresent()) {
        Log.debug("Parsed model " + modelCoordinate + " successfully.",
            ModelingLanguageModelLoader.class.getSimpleName());
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
