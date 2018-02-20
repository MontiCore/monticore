/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import java.util.Optional;
import com.google.common.collect.Maps;

import de.monticore.io.FileReaderWriter;

public class FileReaderWriterMock extends FileReaderWriter {
  
  private Map<Path, String> storedFilesAndContents = Maps.newHashMap();
  
  /**
   * @see de.monticore.io.FileReaderWriter#storeInFile(java.nio.file.Path, String)
   */
  @Override
  public void storeInFile(Path targetPath, String content) {
    storedFilesAndContents.put(targetPath, content);
  }
  
  public Optional<String> getContentForFile(String filePath) {
    Path p = Paths.get(filePath);
    if (storedFilesAndContents.containsKey(p)) {
      return Optional.of(storedFilesAndContents.get(p));
    }
    
    return Optional.empty();
  }
  
  public Map<Path, String> getStoredFilesAndContents() {
    return this.storedFilesAndContents;
  }
  
}
