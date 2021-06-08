/* (c) https://github.com/MontiCore/monticore */

package de.monticore.io.paths;

import static com.google.common.base.Preconditions.checkState;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.apache.commons.io.FilenameUtils;

import de.se_rwth.commons.logging.Log;

final class ModelCoordinateImpl implements ModelCoordinate {
  
  /**
   * The relative location of the model.<br>
   * Example: {@code src/main/grammars/de/mc/statechart.mc}
   */
  private URL location;
  
  /**
   * The qualified path of the model. It is implied that any directories
   * preceding the file name are part of the model package.<br>
   * Example: {@code de/mc/statechart.mc}
   */
  private Path qualifiedPath;
  
  @Override
  public boolean hasLocation() {
    return this.location != null;
  }
  
  @Override
  public boolean isQualified() {
    return this.qualifiedPath != null;
  }
  
  @Override
  public void setLocation(URL location) {
    checkState(this.location == null, "The location of the ModelCoordinate was already set.");
    this.location = location;
  }
  
  @Override
  public void setQualifiedPath(Path qualifiedPath) {
    checkState(this.qualifiedPath == null,
        "The qualified path of the ModelCoordinate was already set.");
    this.qualifiedPath = qualifiedPath;
  }
  
  @Override
  public boolean exists() {
    checkState(location != null, "The location of the ModelCoordinate wasn't set.");
    return location != null;
  }
  
  @Override
  public String getBaseName() {
    if (hasLocation()) {
      return FilenameUtils.getBaseName(location.toString());
    }
    else {
      return FilenameUtils.getBaseName(qualifiedPath.toString());
    }
  }
  
  @Override
  public String getExtension() {
    if (hasLocation()) {
      return FilenameUtils.getExtension(location.toString());
    }
    else {
      return FilenameUtils.getExtension(qualifiedPath.toString());
    }
  }
  
  @Override
  public URL getLocation() {
    checkState(location != null, "The location of the ModelCoordinate wasn't set.");
    return this.location;
  }
  
  @Override
  public String getName() {
    if (hasLocation()) {
      return FilenameUtils.getName(location.toString());
    }
    else {
      return FilenameUtils.getName(qualifiedPath.toString());
    }
  }
  
  @Override
  public Path getPackagePath() {
    checkState(qualifiedPath != null, "The qualified path of the ModelCoordinate wasn't set.");
    return Paths.get(FilenameUtils.getPathNoEndSeparator(qualifiedPath.toString()));
  }
  
  @Override
  public Path getParentDirectoryPath() {
    checkState(qualifiedPath != null, "The qualified path of the ModelCoordinate wasn't set.");
    
    if (location.getProtocol().equals("jar")) {
      try {
        JarURLConnection connection = (JarURLConnection) location.openConnection();
        File result = new File(connection.getJarFileURL().getFile());
        return Paths.get(result.getAbsolutePath());
      }
      catch (IOException e) {
        Log.debug("Error reading jar file URL", e, ModelCoordinateImpl.class.getName());
      }
    }
    
    String locationString = location.toString();
    String protocol = location.getProtocol();
    int protocolLength = 0;
    if (locationString.startsWith(protocol)) {
      protocolLength = protocol.length() + 1;
    }
    
    int qualifiedPathLength = qualifiedPath.toString().length();
    String parentDirectoryString = locationString
        .substring(protocolLength, locationString.length() - qualifiedPathLength);
    
    String uri = protocol + ":" + parentDirectoryString;
    URI temp = URI.create(uri);
    Path p = Paths.get(temp);
    return p;
  }
  
  @Override
  public String getQualifiedBaseName() {
    checkState(qualifiedPath != null, "The qualified path of the ModelCoordinate wasn't set.");
    // regex for replacing all file separators with dots
    return getQualifiedBasePath().toString().replaceAll("\\\\|/", ".");
  }
  
  @Override
  public Path getQualifiedBasePath() {
    checkState(qualifiedPath != null, "The qualified path of the ModelCoordinate wasn't set.");
    return getPackagePath().resolve(getBaseName());
  }
  
  @Override
  public Path getQualifiedPath() {
    checkState(qualifiedPath != null, "The qualified path of the ModelCoordinate wasn't set.");
    return this.qualifiedPath;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof ModelCoordinate)) {
      return false;
    }
    ModelCoordinate coordinate = (ModelCoordinate) obj;
    return Objects.equals(location, coordinate.getLocation())
        && Objects.equals(qualifiedPath, coordinate.getQualifiedPath());
  }
  
  @Override
  public int hashCode() {
    int hashCode = 0;
    hashCode += location != null ? location.hashCode() : 0;
    hashCode += qualifiedPath != null ? qualifiedPath.hashCode() : 0;
    return hashCode;
  }
  
  @Override
  public String toString() {
    return "Location: " + Objects.toString(location) + " Package: "
        + Objects.toString(qualifiedPath);
  }
  
}
