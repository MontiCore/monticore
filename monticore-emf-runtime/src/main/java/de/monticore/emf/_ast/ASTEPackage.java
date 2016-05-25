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

package de.monticore.emf._ast;

import java.util.List;

import org.eclipse.emf.ecore.EPackage;

/**
 * An interface for all MontiCore model packages.
 *
 */
public interface ASTEPackage extends EPackage {
  
  /**
   *  Returns the list of packages for all extended models 
   */
  public List<ASTEPackage> getASTESuperPackages();
  
  public String getName();
  
  /**
   * Returns the package's name of the referenced model 
   */
  public String getPackageName();

}
