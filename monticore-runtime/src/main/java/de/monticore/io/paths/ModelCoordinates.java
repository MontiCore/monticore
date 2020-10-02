/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io.paths;

import com.google.common.base.Charsets;
import de.monticore.generating.templateengine.reporting.Reporting;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Path;
import java.nio.file.Paths;
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

  public static Reader getReader(ModelCoordinate qualifiedModel, ModelPath mp) {
    return getReader(mp.resolveModel(qualifiedModel));
  }

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
