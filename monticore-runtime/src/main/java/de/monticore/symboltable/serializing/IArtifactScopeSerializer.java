package de.monticore.symboltable.serializing;

import de.monticore.io.FileReaderWriter;
import de.monticore.symboltable.ArtifactScope;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * This interface realizes storing {@link ArtifactScope} instances to files and loading these, and
 * abstracts from a concrete mechanism to (de)serialize {@link ArtifactScope} instances.
 *
 */
public interface IArtifactScopeSerializer {
  
  public static final String SYMBOL_STORE_LOCATION = "target/generated-sources/monticore/symbols";
  
  public static final String SYMBOL_FILE_ENDING = "json";
  
  /**
   * An implementation realizes a method to serialize an {@link ArtifactScope} instance into a
   * String
   * 
   * @param as
   * @return
   */
  public Optional<String> serialize(ArtifactScope as);
  
  /**
   * An implementation realizes a method to deserialize an {@link ArtifactScope} from a String.
   * 
   * @param s
   * @return
   */
  public Optional<ArtifactScope> deserialize(String s);
  
  default void store(ArtifactScope as) {
    StringBuilder fileName = new StringBuilder();
    fileName.append(SYMBOL_STORE_LOCATION);
    fileName.append(File.separator);
    fileName.append(Names.getPathFromPackage(as.getPackageName()));
    fileName.append(File.separator);
    fileName.append(as.getName().get());
    fileName.append(".");
    fileName.append(SYMBOL_FILE_ENDING);
    store(as, Paths.get(fileName.toString()));
  }
  
  /**
   * Stores an {@link ArtifactScope} instance to a given path. A failure of the operation causes an
   * error.
   * 
   * @param as
   * @param targetPath
   */
  default void store(ArtifactScope as, Path targetPath) {
    Optional<String> content = serialize(as);
    if (content.isPresent()) {
      new FileReaderWriter().storeInFile(targetPath, content.get());
    }
    else {
      Log.error("0x Serialization of symbols to " + targetPath.toString() + " failed.");
    }
  }
  
  /**
   * Stores an {@link ArtifactScope} instance to a given path. A failure of the operation is
   * ignored.
   * 
   * @param as
   * @param targetPath
   */
  default void storeOpt(ArtifactScope as, Path targetPath) {
    Optional<String> content = serialize(as);
    if (content.isPresent()) {
      new FileReaderWriter().storeInFile(targetPath, content.get());
    }
  }
  
  default ArtifactScope load(String qualifiedName) {
    StringBuilder fileName = new StringBuilder();
    fileName.append(SYMBOL_STORE_LOCATION);
    fileName.append(File.separator);
    fileName.append(Names.getPathFromPackage(qualifiedName));
    fileName.append(".");
    fileName.append(SYMBOL_FILE_ENDING);
    return load(Paths.get(fileName.toString()));
  }
  
  /**
   * Loads an {@link ArtifactScope} instance from a file in the given path and logs an error if the
   * operation fails.
   * 
   * @param sourcePath
   * @return
   */
  default ArtifactScope load(Path sourcePath) {
    Optional<ArtifactScope> deserialized = loadOpt(sourcePath);
    if (!deserialized.isPresent()) {
      Log.error("0x Deserialization of symbols in " + sourcePath.toString() + " failed.");
    }
    return deserialized.get();
  }
  
  /**
   * Loads an {@link ArtifactScope} instance from a file in the given path. If the operation fails,
   * returns {@link Optional#empty()}.
   * 
   * @param sourcePath
   * @return
   */
  default Optional<ArtifactScope> loadOpt(Path sourcePath) {
    String content = new FileReaderWriter().readFromFile(sourcePath);
    return deserialize(content);
  }
  
}
