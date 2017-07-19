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

package de.monticore.codegen.mc2cd.manipul;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

import com.google.common.collect.Maps;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.utils.ASTNodes;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 */
public class AddAttributesOfExtendedInterfacesManipulation implements
    UnaryOperator<ASTCDCompilationUnit> {
  
  private Map<String, ASTCDInterface> cDInterfaces = Maps.newHashMap();
  
  private void initInterfaceMap(ASTCDCompilationUnit cdCompilationUnit) {
    for (ASTCDInterface cdInterface : ASTNodes.getSuccessors(cdCompilationUnit,
        ASTCDInterface.class)) {
      cDInterfaces.put(
          TransformationHelper.getPackageName(cdCompilationUnit) + cdInterface.getName(),
          cdInterface);
    }
  }
  
  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    
    initInterfaceMap(cdCompilationUnit);
    
    for (ASTCDClass cdClass : ASTNodes.getSuccessors(cdCompilationUnit, ASTCDClass.class)) {
      addAttributesOfExtendedInterfaces(cdClass);
    }
    
    return cdCompilationUnit;
  }
  
  private void addAttributesOfExtendedInterfaces(ASTCDClass cdClass) {
    List<ASTCDAttribute> attributes = new ArrayList<>();
    // TODO GV:use Cd4Analysis symboltable to get all interfaces recursively
    for (ASTReferenceType interf : cdClass.getInterfaces()) {
      if (interf instanceof ASTSimpleReferenceType) {
        List<String> names = ((ASTSimpleReferenceType) interf).getNames();
        String interfaceName = (names.isEmpty())? "" : names.get(names.size()-1);
        if (cDInterfaces.get(interfaceName) != null) {
          attributes.addAll(cDInterfaces.get(interfaceName).getCDAttributes());
        }
      }
    }
    for (ASTCDAttribute attr : attributes) {
      cdClass.getCDAttributes().add(attr.deepClone());
    }
  }
  
}
