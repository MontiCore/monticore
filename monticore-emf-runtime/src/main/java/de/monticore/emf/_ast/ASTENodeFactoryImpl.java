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

package de.monticore.emf._ast;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * The Factory for the model object {@link ASTENode}
 */
public class ASTENodeFactoryImpl extends EFactoryImpl implements ASTENodeFactory {
  
  // Creates the default factory implementation.
  public static ASTENodeFactory init() {
    try {
      ASTENodeFactory theASTENodeFactory = (ASTENodeFactory) EPackage.Registry.INSTANCE
          .getEFactory(ASTENodePackage.eNS_URI);
      if (theASTENodeFactory != null) {
        return theASTENodeFactory;
      }
    }
    catch (Exception exception) {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new ASTENodeFactoryImpl();
  }
  
  @Override
  public EObject create(EClass eClass) {
    throw new IllegalArgumentException("The class '" + eClass.getName()
        + "' is not a valid classifier");
  }
  
  @Override
  public Object createFromString(EDataType eDataType, String initialValue) {
    switch (eDataType.getClassifierID()) {
      case ASTENodePackage.CONSTANTSASTENODE:
        return createConstantsASTENodeFromString(eDataType, initialValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName()
            + "' is not a valid classifier");
    }
  }
  
  @Override
  public String convertToString(EDataType eDataType, Object instanceValue) {
    switch (eDataType.getClassifierID()) {
      case ASTENodePackage.CONSTANTSASTENODE:
        return convertConstantsASTENodeToString(eDataType, instanceValue);
      default:
        throw new IllegalArgumentException("The datatype '" + eDataType.getName()
            + "' is not a valid classifier");
    }
  }
  
  public ASTENodePackage getASTENodePackage() {
    return (ASTENodePackage) getEPackage();
  }
  
  public ASTENodeLiterals createConstantsASTENodeFromString(EDataType eDataType,
      String initialValue) {
    return ASTENodeLiterals.valueOf(initialValue);
  }
  
  public String convertConstantsASTENodeToString(EDataType eDataType, Object instanceValue) {
    return instanceValue == null ? null : instanceValue.toString();
  }
  
  // TODO create methods for eDatatypes createEtypeFromString and
  // convertEtypeToString
}
