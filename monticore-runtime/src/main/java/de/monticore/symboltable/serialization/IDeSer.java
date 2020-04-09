/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import de.monticore.io.FileReaderWriter;
import de.monticore.symboltable.IScope;

import java.net.URL;
import java.nio.file.Path;

/**
 * Common interface for classes <b>De</b>serializing and <b>Ser</b>ializing objects of a generic
 * class parameter T. Further contains default implementations to load and store objects of this
 * type from/to the file system. Within MontiCore, classes implementing this interface typically set
 * the generic type parameter to a concrete symbol class or a concrete scope class.
 */
public interface IDeSer<T, S extends IScope> {

  /**
   * A String representation of the
   *
   * @return
   */
  public String getSerializedKind();

  /**
   * Serializes a given object of generic class parameter T and returns the resulting String.
   *
   * @param toSerialize
   * @return
   */
  public String serialize(T toSerialize);

  /**
   * Deserializes a given String and returns the resulting object of the generic class parameter T.
   *
   * @param serialized
   * @param enclosingScope
   * @return
   */
  public T deserialize(String serialized, S enclosingScope);

  /**
   * Stores a given object of generic class parameter T in a (new) file located at given path using
   * the {@link FileReaderWriter}.
   *
   * @param toSerialize
   * @param path
   */
  default public void store(T toSerialize, Path path) {
    String serialized = serialize(toSerialize);
    FileReaderWriter.storeInFile(path, serialized);
  }

  /**
   * Tries to load an object of generic class parameter type T at the given location. If the file
   * does not exist or does not contained the expected stored symbols, returns an error.
   *
   * @param url
   * @return
   */
  default public T load(URL url, S enclosingScope) {
    String serialized = FileReaderWriter.readFromFile(url);
    return deserialize(serialized, enclosingScope);
  }

}
