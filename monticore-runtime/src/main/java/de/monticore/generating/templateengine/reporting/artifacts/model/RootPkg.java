/*
 * ******************************************************************************
 * MontiCore Language Workbench, www.monticore.de
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

/**
 * Represents the topmost package in the package hierarchy of a recorded run
 */
public class RootPkg extends APkg {
  
  /**
   * @see visualization.model.APkg#getQualifiedName()
   */
  @Override
  public String getQualifiedName() {
    return "";
  }
  
  /**
   * Adds an element to the package tree of this root pkg. Missing subpackages are created on
   * demand. In addition, the Pkg of the provided Element is set.
   * 
   * @param packagePath qualified Name of the package
   * @param e Element
   */
  public void addElementToPkgTree(String packagePath, Element e) {
    APkg pkg = getPkg(packagePath);
    pkg.addElement(e);
    e.setPkg(pkg);
  }
  
  /**
   * @see visualization.model.APkg#resolveAncestorWithElements()
   */
  @Override
  public APkg resolveAncestorWithElements() {
    return null;
  }
  
}
