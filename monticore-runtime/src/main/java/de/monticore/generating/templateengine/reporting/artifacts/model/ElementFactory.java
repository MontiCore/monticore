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

import de.monticore.generating.templateengine.reporting.artifacts.ReportingNameHelper;

public class ElementFactory {
  
  public Element createTemplate (RootPkg rootPkg, String qualifiedName, String extension) {       
    return createElement(rootPkg, qualifiedName, extension, ElementType.TEMPLATE);
  } 

  /**
   * @param qualifiedfilename
   * @param fileextension
   * @return 
   */
  public Element createFile(RootPkg rootPkg, String qualifiedName, String extension) {
    return createElement(rootPkg, qualifiedName, extension, ElementType.FILE);
  }

  /**
   * @param rootPkg
   * @param modelname
   * @param filename
   */
  public Element createModel(RootPkg rootPkg, String qualifiedName, String extension) {
    return createElement(rootPkg, qualifiedName, extension, ElementType.MODEL);
  }
  
  /**
   * @param rootPkg
   * @param modelname
   * @param filename
   */
  public Element createHelper(RootPkg rootPkg, String qualifiedName, String extension) {
    return createElement(rootPkg, qualifiedName, extension, ElementType.HELPER);
  }
  
  private Element createElement(RootPkg rootPkg, String qualifiedName, String extension, ElementType elementType) {
    Element e = new Element();
    e.setSimpleName(ReportingNameHelper.getSimpleName(qualifiedName));
    e.setExtension(extension);
    e.setType(elementType);
    rootPkg.addElementToPkgTree(ReportingNameHelper.getPath(qualifiedName), e);
    return e;
  }
}
