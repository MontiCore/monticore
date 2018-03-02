/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io.paths;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Iterables;

import de.monticore.AmbiguityException;
import de.monticore.io.FileReaderWriter;
import de.se_rwth.commons.logging.Log;

/**
 * A ModelPath encapsulates the domain of accessible models inside the running
 * language tool.
 *
 * @author Sebastian Oberhoff, Pedram Mir Seyed Nazari
 */
public final class ModelPath {

  private final Map<URLClassLoader, URL> classloaderMap = new LinkedHashMap<>();

  public ModelPath(Collection<Path> modelPathEntries) {
    modelPathEntries.stream()
        .map(Path::toUri)
        .map(ModelPath::tryURItoURL)
        .filter(Optional::isPresent)
        .map(Optional::get)
            // parent class loader MUST BE null here!
            // otherwise we would start to resolve from the system class path (or
            // worse) unknowingly
        .forEach(url -> classloaderMap.put(new URLClassLoader(new URL[] { url }, null), url));
  }

  public ModelPath(Path... modelPathEntries) {
    this(Arrays.asList(modelPathEntries));
  }

  private static Optional<URL> tryURItoURL(URI uri) {
    try {
      return Optional.of(uri.toURL());
    }
    catch (MalformedURLException e) {
      Log.error("0xA1022 The entry " + uri + " in the modelpath was invalid.", e);
      return Optional.empty();
    }
  }

  public void removeEntry(Path modelPathEntry) {
    Optional<URLClassLoader> urlClassLoader = tryURItoURL(modelPathEntry.toUri())
        .flatMap(url -> classloaderMap.entrySet().stream()
            .filter(entry -> entry.getValue().equals(url))
            .findFirst())
        .map(Map.Entry::getKey);
    urlClassLoader.ifPresent(classloaderMap::remove);
  }

  public void addEntry(Path modelPathEntry) {
    tryURItoURL(modelPathEntry.toUri()).ifPresent(url ->
        classloaderMap.put(new URLClassLoader(new URL[] { url }, null), url));
  }

  /**
   * Sets the model location of a ModelCoordinate.
   *
   * @param qualifiedModel a ModelFileInfo instance who's package has been
   * specified
   * @return the ModelCoordinate of the model who's location has been set if
   * possible (the success of the operation can be verified with
   * {@link ModelCoordinate#exists()})
   * @throws AmbiguityException if the search locates multiple potentially
   * matching models
   */
  public ModelCoordinate resolveModel(ModelCoordinate qualifiedModel) {
    String fixedPath = qualifiedModel.getQualifiedPath().toString()
        .replaceAll("\\" + File.separator, "/");
    FileReaderWriter ioWrapper = new FileReaderWriter();

    List<URL> resolvedURLS = classloaderMap.keySet().stream()
        .map(classloader -> ioWrapper.getResource(classloader, fixedPath))
        .filter(opturl -> opturl.isPresent())
        .map(url -> url.get())
        .collect(Collectors.toList());

    if (1 < resolvedURLS.size()) {
      StringBuilder ambiguitiyArray = new StringBuilder("{");
      String sep = "";
      for (URL url: resolvedURLS) {
        ambiguitiyArray.append(sep);
        sep = ",\n";
        ambiguitiyArray.append(url.toString());      
      }
      ambiguitiyArray.append("}");
      Log.error(
          "0xA1294 Multiple matching entries where located in the modelpath for the model "
              + fixedPath + "\n" + ambiguitiyArray.toString());
    }
    if (1 == resolvedURLS.size()) {
      qualifiedModel.setLocation(Iterables.getOnlyElement(resolvedURLS));
    }
    return qualifiedModel;
  }

  @Override
  public String toString() {
    String result = "[";
    result = result + this.classloaderMap.values().stream()
        .map(URL::toString)
        .collect(Collectors.joining(", "));
    return result + "]";
  }

  public Collection<Path> getFullPathOfEntries() {
    final Collection<Path> entries = new LinkedHashSet<>();

    for (URL entry : classloaderMap.values()) {
      try {
        entries.add(Paths.get(entry.toURI()));
      }
      catch (URISyntaxException e) {
        // ignore this entry
      }
    }

    return entries;
  }
  
}
