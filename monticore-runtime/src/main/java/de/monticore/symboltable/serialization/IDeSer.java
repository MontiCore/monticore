/* (c) https://github.com/MontiCore/monticore */
package de.monticore.symboltable.serialization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;

import de.monticore.io.FileReaderWriter;

/**
 * Common interface for classes <b>De</b>serializing and <b>Ser</b>ializing objects of a generic
 * class parameter T. Further contains default implementations to load and store objects of this
 * type from/to the file system. Within MontiCore, classes implementing this interface typically set
 * the generic type parameter to a concrete symbol class or a concrete scope class.
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public interface IDeSer<T> {
  
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
   * @return
   * @throws IOException
   */
  public Optional<T> deserialize(String serialized);
  
  /**
   * Stores a given object of generic class parameter T in a (new) file located at given path using
   * the {@link FileReaderWriter}.
   * 
   * @param toSerialize
   * @param path
   */
  default public void store(T toSerialize, Path path) {
    String serialized = serialize(toSerialize);
    new FileReaderWriter().storeInFile(path, serialized);
  }
  
  /**
   * Tries to load an object of generic class parameter type T at the given location. If the file
   * does not exist or does not contained the expected stored symbols, returns an
   * {@link Optional#empty()}.
   * 
   * @param url
   * @return
   */
  default public Optional<T> load(URL url) {
    try {
      Reader reader = new InputStreamReader(url.openStream(), Charsets.UTF_8.name());
      BufferedReader buffer = new BufferedReader(reader);
      String serialized = buffer.lines().collect(Collectors.joining());
      return deserialize(serialized);
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }
  
}
