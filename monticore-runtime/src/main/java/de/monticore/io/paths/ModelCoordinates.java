/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io.paths;

import java.net.URL;
import java.nio.file.Path;

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
}
