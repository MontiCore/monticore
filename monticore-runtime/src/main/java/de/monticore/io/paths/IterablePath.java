/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io.paths;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.logging.Log;

/**
 * An IterablePath is specified as a list of directories and/or explicit files and a set of file
 * extensions. It then provides iteration and checking for existence of qualified relative paths
 * which are supposed to be contained in the IterablePath. This functionality is in some ways
 * similar to the Java classpath mechanism. The IterablePath therefore abstracts away from the
 * actual location of a file.<br>
 * <br>
 * For instance, if an IterablePath was created with a directory <b>"src/test/resources/"</b> which
 * contains a file <b>"src/test/resources/a/AFile.txt"</b> and <b>"txt"</b> was specified as a
 * desired extension, then the path <b>"a/File.txt"</b> will be contained in the resolved elements
 * and accessible via the returned iterator ({@link #get()}). <br>
 * <br>
 * <b>Note that the order of the underlying path entries matter.</b> The first found resolved entry
 * for a qualified relative path is taken thus potentially hiding later matches (possible conflicts
 * are logged with level DEBUG).
 *
 */
public final class IterablePath {
  
  /* Singleton empty iterable path. */
  static final IterablePath EMPTY = new IterablePath(Collections.emptyList(),
      Collections.emptyMap());
  
  /**
   * A singleton {@link IterablePath} denoting an empty {@link IterablePath}.
   * 
   * @return
   */
  public static IterablePath empty() {
    return EMPTY;
  }
  
  /**
   * Creates a new {@link IterablePath} based on the supplied input {@link File} . The file
   * extension of the input {@link File} must be one of the supplied file extensions otherwise the
   * resulting {@link IterablePath} will be empty.
   * 
   * @param file
   * @param extensions
   * @return
   */
  public static IterablePath from(File file, Set<String> extensions) {
    return from(Lists.newArrayList(file.getAbsoluteFile()), extensions);
  }
  
  /**
   * Creates a new {@link IterablePath} based on the supplied input {@link File} . The file
   * extension of the input {@link File} must be equal to the supplied file extension otherwise the
   * resulting {@link IterablePath} will be empty.
   * 
   * @param file
   * @param extension
   * @return
   */
  public static IterablePath from(File file, String extension) {
    return from(file, Sets.newLinkedHashSet(Arrays.asList(extension)));
  }
  
  /**
   * Creates a new {@link IterablePath} based on the supplied list of {@link File}s containing all
   * files with the specified extensions. Note: an empty set of extensions will yield an empty
   * {@link IterablePath}.
   * 
   * @param files
   * @param extensions
   * @return
   */
  public static IterablePath from(List<File> files, Set<String> extensions) {
    List<Path> sourcePaths = files
        .stream()
        .map(file -> file.toPath())
        .collect(Collectors.toList());
    return fromPaths(sourcePaths, extensions);
  }
  
  /**
   * Creates a new {@link IterablePath} based on the supplied list of {@link File}s containing all
   * files with the specified extension. Note: an empty extension will yield an empty
   * {@link IterablePath}.
   * 
   * @param files
   * @param extension
   * @return
   */
  public static IterablePath from(List<File> files, String extension) {
    return from(files, Sets.newLinkedHashSet(Arrays.asList(extension)));
  }
  
  /**
   * Creates a new {@link IterablePath} based on the supplied set of {@link Path}s containing all
   * files with the specified extensions. Note: an empty set of extensions will yield an empty
   * {@link IterablePath}.
   * 
   * @param paths
   * @param extensions
   * @return
   */
  public static IterablePath fromPaths(List<Path> paths, Set<String> extensions) {
    Map<Path, Path> pMap = new LinkedHashMap<>();
    for (Path path : paths) {
      List<Path> entries = walkFileTree(path).filter(getExtensionsPredicate(extensions)).collect(
          Collectors.toList());
      for (Path entry : entries) {
        Path key = path.relativize(entry);
        if (key.toString().isEmpty()) {
          key = entry;
        }
        if (pMap.get(key) != null) {
          Log.debug("The qualified path " + key + " appears multiple times.",
              IterablePath.class.getName());
        }
        else {
          pMap.put(key, entry);
        }
      }
    }
    return new IterablePath(paths, pMap);
  }
  
  /**
   * Creates a new {@link IterablePath} based on the supplied set of {@link Path}s containing all
   * files with the specified extension. Note: an empty extension will yield an empty
   * {@link IterablePath}.
   * 
   * @param paths
   * @param extension
   * @return
   */
  public static IterablePath fromPaths(List<Path> paths, String extension) {
    return fromPaths(paths, Sets.newLinkedHashSet(Arrays.asList(extension)));
  }
  
  /**
   * Encapsulates creation of a {@link Stream} of {@link Path}s for a given start {@link Path}.
   * 
   * @param path
   * @return
   */
  protected static Stream<Path> walkFileTree(Path path) {
    if (path.toFile().exists()) {
      try {
        return Files.walk(path);
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    else {
      Log.warn("0xA4074 The requested path " + path.toString() + " does not exist.");
      return Stream.empty();
    }
  }
  
  /**
   * Creates a {@link Predicate} for (e.g.) filtering {@link Path} elements by the specified set of
   * file extensions.
   * 
   * @param extensions
   * @return
   */
  protected static Predicate<Path> getExtensionsPredicate(Set<String> extensions) {
    return path -> extensions.contains(FilenameUtils.getExtension(path.toString()));
  }
  
  /**
   * The paths used to initialize this iterable path.
   */
  final List<Path> paths;
  
  /**
   * The underlying map of qualified paths and their resolved paths.
   */
  final Map<Path, Path> pathMap;
  
  /**
   * Constructor for de.monticore.io.paths.IterablePath.
   * 
   * @param paths
   * @param pathMap
   */
  protected IterablePath(List<Path> paths, Map<Path, Path> pathMap) {
    if (paths == null) {
      throw new IllegalArgumentException("0xA4069 Path list must not be null.");
    }
    if (pathMap == null) {
      throw new IllegalArgumentException("0xA4068 Path map must not be null.");
    }
    this.paths = paths;
    this.pathMap = pathMap;
  }
  
  /**
   * Returns the actual list of paths underlying this iterable path, i.e., the list of paths used to
   * create this iterable path. The returned list is unmodifieable and may not be used to alter this
   * iterable path.
   * 
   * @return the list of paths used to create this iterable path
   */
  public List<Path> getPaths() {
    return Collections.unmodifiableList(this.paths);
  }
  
  /**
   * Returns an unmodifiable iterator over the resolved qualified elements of this
   * {@link IterablePath}. For instance, if this IterablePath was created with a directory
   * <b>"src/test/resources/"</b> which contains a file <b>"src/test/resources/a/AFile.txt"</b> and
   * <b>"txt"</b> was specified as a desired extension, then the path <b>"a/File.txt"</b> will be
   * contained in the resolved qualified elements and accessible via the returned iterator.
   * 
   * @return an iterator over all resolved qualified paths, i.e., qualified relative paths that are
   * contained in the specified path entries and that match the desired file extension(s)
   */
  public Iterator<Path> get() {
    return this.pathMap.keySet().stream().sorted().iterator();
  }
  
  /**
   * Returns an unmodifiable iterator over the resolved elements of this {@link IterablePath}. For
   * instance, if this IterablePath was created with a directory <b>"src/test/resources/"</b> which
   * contains a file <b>"src/test/resources/a/AFile.txt"</b> and <b>"txt"</b> was specified as a
   * desired extension, then the path <b>"src/test/resources/a/AFile.txt"</b> will be contained in
   * the resolved elements and accessible via the returned iterator.
   * 
   * @return an iterator over all resolved paths, i.e., paths that are contained in the specified
   * path entries and that match the desired file extension(s)
   */
  public Iterator<Path> getResolvedPaths() {
    return this.pathMap.values().stream().sorted().iterator();
  }
  
  /**
   * Checks whether a given qualified path exists in this IterablePath. Here, qualified means that a
   * file exists in one of the directories making up this IterablePath. The qualified path does not
   * require knowledge about where exactly the underlying file is located on the file system (or
   * project directory layout). It only suffices to know that a required file is qualified as
   * <b>a/AFile.txt</b>. The actually resolved path to a file that exists in this IterablePath can
   * be obtained by {@link IterablePath#getResolvedPath(Path)}. An (unmodifiable) iterator over all
   * qualified paths contained in this IterablePath can be obtained by {@link IterablePath#get()}.
   * 
   * @param path
   * @return
   */
  public boolean exists(Path path) {
    Reporting.reportFileExistenceChecking(getPaths(), path);
    return this.pathMap.containsKey(path);
  }
  
  /**
   * Returns the actually resolved path of a qualified path. For instance, if this IterablePath was
   * created with the directories <b>"src/test/resources/1/"</b> and <b>"src/test/resources/1/"</b>
   * (in that order) where both contain a file <b>a/AFile.txt"</b> and <b>"txt"</b> was specified as
   * a desired extension, then the qualified path <b>"a/File.txt"</b> will yield the resolved path
   * <b>"src/test/resources/1/a/AFile.txt"</b>. This is because the respective directory was
   * specified first, thus its entries hide possible other matches in later directories.
   * 
   * @param path qualified path
   * @return the first resolved path matching the qualified path (the order of path directories
   * matters here).
   */
  public Optional<Path> getResolvedPath(Path path) {
    return Optional.ofNullable(this.pathMap.get(path));
  }
  
  @Override
  public String toString() {
    if (this.pathMap.isEmpty()) {
      return "[empty iterable path]";
    }
    String result = "[";
    result = result + this.pathMap.values().stream()
        .sorted()
        .map(Path::toString)
        .collect(Collectors.joining(", "));
    return result + "]";
  }
  
}
