/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * This project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */

package de.monticore.io.paths;

import java.net.URL;
import java.nio.file.Path;

/**
 * A ModelCoordinate provides various pieces of information associated with the name of a model and
 * its location in the file system.
 * <p>
 * The actual presence of a file denoted by a ModelCoordinate object is <i>not</i> guaranteed.
 * 
 * @author Sebastian Oberhoff
 */
public interface ModelCoordinate {
  
  /**
   * @param location a Path representing the location of the model.
   * <p>
   * Example {@code src/main/grammars/de/mc/statechart.mc}
   */
  void setLocation(URL location);
  
  /**
   * @param qualifiedPath a Path representing the qualified model file.
   * <p>
   * Example: {@code de/mc/statechart.mc}
   */
  void setQualifiedPath(Path qualifiedPath);
  
  /**
   * @return true if the location of this ModelCoordinate has been set.
   * @see #setLocation(Path)
   */
  boolean hasLocation();
  
  /**
   * @return true if the qualified path of this ModelCoordinate has been set.
   * @see #setQualifiedPath(Path)
   */
  boolean isQualified();
  
  /**
   * @return a String representing the model name
   * <p>
   * Example: "statechart.mc"
   */
  String getName();
  
  /**
   * @return the model name without extension
   * <p>
   * Example: "statechart"
   */
  String getBaseName();
  
  /**
   * @return the extension of the source file of the model
   * <p>
   * Example: "mc"
   */
  String getExtension();
  
  /**
   * @return a Path representing the location of the model.
   * <p>
   * Example {@code src/main/grammars/de/mc/statechart.mc}
   */
  URL getLocation();
  
  /**
   * Checks whether the file at the location returned by {@link #getLocation()} is present in the
   * file system.
   * 
   * @return true if the model file exists
   */
  boolean exists();
  
  /**
   * @return a Path representing the qualified model file.
   * <p>
   * Example: {@code de/mc/statechart.mc}
   */
  Path getQualifiedPath();
  
  /**
   * @return a Path representing the package segment of this model.
   * <p>
   * Example: {@code de/mc}
   */
  Path getPackagePath();
  
  /**
   * @return a String representation of the name of this model without extension.
   * <p>
   * Example: "de.mc.statechart"
   */
  String getQualifiedBaseName();
  
  /**
   * @return a Path representing this model without extension.
   * <p>
   * Example: {@code de/mc/statechart}
   */
  Path getQualifiedBasePath();
  
  /**
   * @return a Path representing the parent directory of the location of this model.
   * <p>
   * Example: {@code src/main/grammars}
   */
  Path getParentDirectoryPath();
}
