/*
 * Copyright (c) 2019 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
 */
package de.monticore.symboltable.serialization;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import de.monticore.io.FileReaderWriter;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public interface IDeSer<T> {
  
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
  
  default public void store(T toSerialize, Path path) {
    String serialized = serialize(toSerialize);
    new FileReaderWriter().storeInFile(path, serialized);
  }
  
  default public Optional<T> load(URL url) {
    Path path;
    try {
      path = Paths.get(new File(url.toURI()).getPath());
      String deserialized = new FileReaderWriter().readFromFile(path);
      return deserialize(deserialized);
    }
    catch (URISyntaxException e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }
  
}
