/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import de.monticore.AmbiguityException;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.io.SharedCloseable;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

/**
 * This class handles all I/O commands in Monticore.
 * <p>
 * Important conventions: File locations are always encoded as Path or URL objects. When creating
 * Path objects please make use of the {@link Path#resolve} method whenever applicable. This
 * convention extends beyond the FileHandler. Do <b>not</b> exchange information between classes
 * using String literals or File objects. Doing so within classes is permitted, though discouraged.
 * <br>
 * Failure to adhere to such a convention has caused innumerable trivial bugs in past
 * implementations.
 * <p>
 * This class uses UTF_8 encoding per default and is realized as singleton.
 */
public class FileReaderWriter {

  protected static FileReaderWriter INSTANCE = null;

  protected Charset charset;

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
  protected FileReaderWriter() {
    this.charset = StandardCharsets.UTF_8;
  }

  /**
   * @param charset The initial encoding to be used until another encoding is assigned.
   * @see #setCharset
   */
  protected FileReaderWriter(Charset charset) {
    this.charset = charset;
  }

  public static void init() {
    INSTANCE = new FileReaderWriter();
  }

  public static void init(Charset charset) {
    INSTANCE = new FileReaderWriter(charset);
  }

  public static void init(FileReaderWriter instance) {
    INSTANCE = instance;
  }

  protected static FileReaderWriter getFileReaderWriter(){
    if(null == INSTANCE){
      init();
    }
    return INSTANCE;
  }

  /**
   * Writes a String to a file using the specified encoding.
   *
   * @param targetPath The location of the file to be written to.
   * @param content    The String that's supposed to be written into the file
   * @see #setCharset(Charset)
   */
  public static void storeInFile(Path targetPath, String content) {
    getFileReaderWriter()._storeInFile(targetPath, content);
  }

  protected void _storeInFile(Path targetPath, String content) {
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
  public static String readFromFile(Path sourcePath) {
    return getFileReaderWriter()._readFromFile(sourcePath);
  }

  protected String _readFromFile(Path sourcePath) {
    String content = null;
    try {
      Reporting.reportOpenInputFile(sourcePath.toString());
      content = FileUtils.readFileToString(sourcePath.toFile(), this.charset);
    }
    catch (IOException e) {
      Log.error("0xA1027 IOException occured.", e);
      Log.debug("IOException while trying to read the content of " + sourcePath
        + ".", e, this.getClass().getName());
    }
    Log.errorIfNull(content);
    return content;
  }

  /**
   * Reads the String content from a file using the specified encoding. URLs can also address files
   * within jars.
   *
   * @param sourcePath The absolute location (fully specifies the filename) of the file to be read
   * @return The String content of the file
   * @see #setCharset(Charset)
   */
  public static String readFromFile(URL sourcePath) {
    return getFileReaderWriter()._readFromFile(sourcePath);
  }

  protected String _readFromFile(URL sourcePath) {
    String content = null;
    try {
      Reporting.reportOpenInputFile(sourcePath.toString());
      URLConnection conn = sourcePath.openConnection();
      if (conn instanceof JarURLConnection) {
        synchronized (SharedCloseable.class) {
          // We have to ensure the JarURLConnection#getJarFile and new SharedCloseable are performed atomic.
          // Otherwise, the backing JarFile might be closed in between.
          // Note: the JVM shares JarFiles across classloader isolations
          openedJarFiles.add(new SharedCloseable<>(((JarURLConnection) conn).getJarFile()));
        }
      }
      Reader reader = new InputStreamReader(conn.getInputStream(), charset.name());
      content = _readFromFile(reader);
      reader.close();
    }
    catch (IOException e) {
      Log.error("0xA0577 IOException occured.", e);
      Log.debug("IOException while trying to read the content of " + sourcePath
        + ".", e, this.getClass().getName());
    }
    Log.errorIfNull(content);
    return content;
  }

  public static String readFromFile(Reader reader) {
    return getFileReaderWriter()._readFromFile(reader);
  }

  protected String _readFromFile(Reader reader) {
    BufferedReader buffer = new BufferedReader(reader);
    String content = buffer.lines().collect(Collectors.joining());
    Log.errorIfNull(content);
    return content;
  }

  /**
   * Tries to load a resource with the passed name in the file system using the passed classLoader.
   * If multiple resources are found, throws an ambiguity exception. If no resource is found,
   * returns {@link Optional#empty()}
   *
   * @param classLoader The classloader employed to load the resource
   * @param name        The name of the resource to load
   * @return The optional URL of the resource, if present, or {@link Optional#empty()} else
   */
  public static Optional<URL> getResource(ClassLoader classLoader, String name) {
    return getFileReaderWriter()._getResource(classLoader, name);
  }

  protected Optional<URL> _getResource(ClassLoader classLoader, String name) {
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

  public static boolean existsFile(Path sourcePath) {
    return getFileReaderWriter()._existsFile(sourcePath);
  }

  protected boolean _existsFile(Path sourcePath) {
    Reporting.reportFileExistenceChecking(Lists.newArrayList(), sourcePath);
    return sourcePath.toFile().exists();
  }

  /**
   * Saves all {@link JarFile}s opened by {@link FileReaderWriter#getReader(URL)} if the protocol "jar:" is used.
   * These files must be closed at the end of programm via {@link FileReaderWriter#closeOpenedJarFiles()}.
   */
  protected static Set<SharedCloseable<JarFile>> openedJarFiles = new HashSet<>();

  /**
   * Obtains the reader for a passed model coordinate. The resulting reader
   * can be used as argument for a parse method of a language's parser.
   * @param location
   * @return
   */
  public static Reader getReader(URL location) {
    try {
      if (!"jar".equals(location.getProtocol())) {
        Path p = Paths.get(location.toURI());
        Reporting.reportOpenInputFile(Optional.of(p.getParent()),
          p.getParent().relativize(p));
        if (location.getFile().charAt(2) == ':') {
          String filename = URLDecoder.decode(location.getFile(), "UTF-8");
          return new FileReader(filename.substring(1));
        }
        return new FileReader(location.getFile());
      }
      String[] parts = location.toURI().toString().split("!");
      Path p = Paths.get(parts[1].substring(1));
      Reporting.reportOpenInputFile(Optional.of(Paths.get(parts[0].substring(10))),
        p);

      // Save opened jar files for later cleanup
      URLConnection conn = location.openConnection();
      if(conn instanceof JarURLConnection){
        synchronized (SharedCloseable.class) {
          // We have to ensure the JarURLConnection#getJarFile and new SharedCloseable are performed atomic.
          // Otherwise, the backing JarFile might be closed in between.
          // Note: the JVM shares JarFiles across classloader isolations
          openedJarFiles.add(new SharedCloseable<>(((JarURLConnection) conn).getJarFile()));
        }
      }
      return new InputStreamReader(conn.getInputStream(), Charsets.UTF_8.name());
    }
    catch (IOException | URISyntaxException e) {
      Log.error("0xA6104 Exception occurred while reading the file at '" + location + "':", e);
    }
    return null;
  }

  /**
   * Closes all jar files opened by {@link FileReaderWriter#getReader(URL)}.
   * Must be called at the end of the program to ensure all resources are freed.
   */
  public static void closeOpenedJarFiles(){
    openedJarFiles.forEach(SharedCloseable::close);
    openedJarFiles.clear();
  }
}
