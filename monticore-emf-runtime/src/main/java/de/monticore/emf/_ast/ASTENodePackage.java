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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;

public interface ASTENodePackage extends ASTEPackage {
  // The package name.
  String eNAME = "ASTENode";
  
  // The package namespace URI.
  String eNS_URI = "http://ASTENode/1.0";
  
  // The package namespace name.
  String eNS_PREFIX = "ASTENode";
  
  // The singleton instance of the package.
  ASTENodePackage eINSTANCE = de.monticore.emf._ast.ASTENodePackageImpl.init();
  
  // The meta object id for the de.monticore.emf._ast.ConstantsASTENode
  int CONSTANTSASTENODE = 0;
  
  // The meta object id for the de.monticore.emf._ast.ENode
  int ENODE = 1;
  
  int ENODE_FEATURE_COUNT = 0;
  
  // Returns the meta object for enum ConstantsASTENode
  EEnum getConstantsASTENode();
  
  // Returns the meta object for class ENode
  EClass getENode();
  
  // Returns the factory that creates the instances of the model.
  ASTENodeFactory getASTENodeFactory();
  
  /**
   * Defines literals for the meta objects that represent each class, each
   * feature of each class, each enum, and each data type
   */
  interface Literals {
    EEnum CONSTANTSASTENODE = eINSTANCE.getConstantsASTENode();
    
    EClass ENODE = eINSTANCE.getENode();
    
  }
}
