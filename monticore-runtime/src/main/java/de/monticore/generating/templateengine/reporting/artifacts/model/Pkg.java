/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2017, MontiCore, All rights reserved.
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

package de.monticore.generating.templateengine.reporting.artifacts.model;

public class Pkg extends APkg {
  
  // Package name, not qualified (relative to parent package)
  private String name;
  
  // Parent Pkg
  private APkg parentPkg;
  
  // Simple constructor
  public Pkg(APkg parent, String name) {
    this.name = name;
    this.parentPkg = parent;
  }
  
  public String getQualifiedName() {
    if (parentPkg instanceof RootPkg) {
      return name;
    }
    
    return parentPkg.getQualifiedName() + "." + name;
  }
  
  /**
   * @return name
   */
  public String getName() {
    return name;
  }

  /**
   * @see visualization.model.APkg#resolveAncestorWithElements()
   */
  @Override
  public APkg resolveAncestorWithElements() {
    if (parentPkg.hasElements()) {
      return parentPkg;
    }
    
    return parentPkg.resolveAncestorWithElements();
  }
  
}
