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
  
  public static ModelCoordinate createLocatedCoordinate(Path location) {
    ModelCoordinate coordinate = new ModelCoordinateImpl();
    coordinate.setLocation(location);
    return coordinate;
  }
  
  public static ModelCoordinate createFullCoordinate(Path qualifiedPath, Path location) {
    ModelCoordinate coordinate = new ModelCoordinateImpl();
    coordinate.setQualifiedPath(qualifiedPath);
    coordinate.setLocation(location);
    return coordinate;
  }
}
