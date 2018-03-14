/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import org.apache.commons.io.FileUtils;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;

import de.monticore.AmbiguityException;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.logging.Log;

/**
 * This class handles all I/O commands in Monticore.
 * <p>
 * Important conventions: File locations are always encoded as Path objects. When creating Path
 * objects please make use of the {@link Path#resolve} method whenever applicable. This convention
 * extends beyond the FileHandler. Do <b>not</b> exchange information between classes using String
 * literals or File objects. Doing so within classes is permitted, though discouraged.<br>
 * Failure to adhere to such a convention has caused innumerable trivial bugs in past
 * implementations.
 * <p>
 * This class uses UTF_8 encoding per default.
 * 
 * @author Sebastian Oberhoff
 */
public class FileReaderWriter {
  
  private Charset charset;
  
  /**
   * Sets the encoding for all subsequent operations until another encoding is assigned.
   * 
   * @param charset The encoding to be used
   */
  public void setCharset(Charset charset) {
    this.charset = charset;
  }
  
  /**
   * Uses the default encoding UTF_8;
   */
  public FileReaderWriter() {
    this.charset = Charsets.UTF_8;
  }
  
  /**
   * @param charset The initial encoding to be used until another encoding is assigned.
   * @see #setCharset
   */
  public FileReaderWriter(Charset charset) {
    this.charset = charset;
  }
  
  /**
   * Writes a String to a file using the specified encoding.
   * 
   * @param targetPath The location of the file to be written to.
   * @param content The String that's supposed to be written into the file
   * @see #setCharset(Charset)
   */
  public void storeInFile(Path targetPath, String content) {
    try {
      Reporting.reportFileCreation(targetPath.toString());
      FileUtils.write(targetPath.toFile(), content, this.charset);
    }
    catch (IOException e) {
      Log.error("0xA1023 IOException occured.", e);
      Log.debug("IOException occured while trying to write to the File " + targetPath + ".", e,
          this.getClass().getName());
    }
  }
  
  /**
   * Reads the String content from a file using the specified encoding.
   * 
   * @param sourcePath The absolute location (fully specifies the filename) of the file to be read
   * @return The String content of the file
   * @see #setCharset(Charset)
   */
  public String readFromFile(Path sourcePath) {
    String content = null;
    try {
      Reporting.reportOpenInputFile(sourcePath.toString());
      content = FileUtils.readFileToString(sourcePath.toFile(), this.charset);
    }
    catch (IOException e) {
      Log.error("0xA1024 IOException occured.", e);
      Log.debug("IOException while trying to read the content of " + sourcePath
          + ".", e, this.getClass().getName());
    }
    Log.errorIfNull(content);
    return content;
  }
  
  /**
   * Tries to load a resource with the passed name in the file system using the passed classLoader.
   * If multiple resources are found, throws an ambiguity exception. If no resource is found,
   * returns {@link Optional#empty()}
   * 
   * @param classLoader The classloader employed to load the resource
   * @param name The name of the resource to load
   * @return The optional URL of the resource, if present, or {@link Optional#empty()} else
   */
  public Optional<URL> getResource(ClassLoader classLoader, String name) {
    try {
      ArrayList<URL> results = Collections.list(classLoader.getResources(name));
      if (results.size() > 1) {
        throw new AmbiguityException("0xA4092 Multiple models were found with name '"
            + name + "':" + results.toString());
      }
      else if (results.size() < 1) {
        Reporting.reportFileExistenceChecking(Lists.newArrayList(), Paths.get(name));
      }
      else {
        Reporting.reportOpenInputFile(results.get(0).getFile());
        return Optional.ofNullable(results.get(0));
      }
    }
    catch (IOException e) {
      Log.error("0xA1024 IOException occured.", e);
      Log.debug("IOException while trying to find the URL of " + name, e,
          this.getClass().getName());
    }
    return Optional.empty();
  }
  
  public boolean existsFile(Path sourcePath) {
    Reporting.reportFileExistenceChecking(Lists.newArrayList(), sourcePath);
    return sourcePath.toFile().exists();
  }
  
}
