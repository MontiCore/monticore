/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io.paths;

import com.google.common.base.Charsets;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ModelCoordinates {

  private ModelCoordinates() {
    // noninstantiable
  }

  public static ModelCoordinate createEmptyCoordinate() {
    return new ModelCoordinateImpl();
  }

  public static ModelCoordinate createQualifiedCoordinate(Path qualifiedPath) {
    ModelCoordinate coordinate = new ModelCoordinateImpl();
    coordinate.setQualifiedPath(qualifiedPath);
    return coordinate;
  }

  public static ModelCoordinate createLocatedCoordinate(URL location) {
    ModelCoordinate coordinate = new ModelCoordinateImpl();
    coordinate.setLocation(location);
    return coordinate;
  }

  public static ModelCoordinate createFullCoordinate(Path qualifiedPath, URL location) {
    ModelCoordinate coordinate = new ModelCoordinateImpl();
    coordinate.setQualifiedPath(qualifiedPath);
    coordinate.setLocation(location);
    return coordinate;
  }

  public static ModelCoordinate createQualifiedCoordinate(String qualifiedModelName,
      String fileExtension) {
    Path qualifiedPath = Paths
        .get(Names.getPathFromPackage(qualifiedModelName) + "." + fileExtension);
    return createQualifiedCoordinate(qualifiedPath);
  }

  /**
   * This method creates a list of ModelCoordinates for a passed qualified model name and a passed
   * list of potential file extensions.
   * @param qualifiedModelName
   * @param fileExtensions
   * @return
   */
  public static List<ModelCoordinate> createQualifiedCoordinates(String qualifiedModelName, List<String> fileExtensions){
    List<ModelCoordinate> result = new ArrayList<>();
    for(String fileExtension : fileExtensions){
      result.add(createQualifiedCoordinate(qualifiedModelName, fileExtension));
    }
    return result;
  }

  /**
   * This method can be used to find a list of potential ModelCoordinates for a passed qualified
   * model name, for which the file extension is unknown.
   * @param mp
   * @param qualifiedModelName
   * @return
   */
  public static List<ModelCoordinate> createQualifiedCoordinates(ModelPath mp, String qualifiedModelName){
    List<ModelCoordinate> result = new ArrayList<>();
    String simpleFileName = Names.getSimpleName(qualifiedModelName);
    String packagePath = Names.getPathFromQualifiedName(qualifiedModelName);
    for(Path mpEntry : mp.getFullPathOfEntries()){
      try{
        File dir = mpEntry.resolve(packagePath).toFile();
        if(dir.exists() && dir.isDirectory()){
          for(File f : dir.listFiles()){
            if(FilenameUtils.getBaseName(f.getName()).equals(simpleFileName)){
              String fileExtension = FilenameUtils.getExtension(f.getName());
              result.add(createQualifiedCoordinate(qualifiedModelName, fileExtension));
            }
          }
        }
      }
      catch(InvalidPathException e){ }
    }
    return result;
  }

  /**
   *
   * @param qualifiedModel
   * @param mp
   * @return
   */
  public static Reader getReader(ModelCoordinate qualifiedModel, ModelPath mp) {
    return getReader(mp.resolveModel(qualifiedModel));
  }

  /**
   * Obtains the reader for a passed model coordinate. The resulting reader
   * can be used as argument for a parse method of a language's parser.
   * @param location
   * @return
   */
  public static Reader getReader(ModelCoordinate location) {
    if (!location.hasLocation()) {
      Log.error("0xA6103 Could not find location of model '" + location.getName() + "'.");
      return null;
    }

    URL loc = location.getLocation();
    try {
      Reporting.reportOpenInputFile(Optional.of(location.getParentDirectoryPath()),
          location.getQualifiedPath());

      if (!"jar".equals(loc.getProtocol())) {
        if (loc.getFile().charAt(2) == ':') {
          String filename = URLDecoder.decode(loc.getFile(), "UTF-8");
          return new FileReader(filename.substring(1));
        }
        return new FileReader(loc.getFile());
      }
      return new InputStreamReader(loc.openStream(), Charsets.UTF_8.name());
    }
    catch (IOException e) {
      Log.error("0xA6104 Exception occurred while reading the file at '" + loc + "':", e);
    }
    return null;
  }
}
