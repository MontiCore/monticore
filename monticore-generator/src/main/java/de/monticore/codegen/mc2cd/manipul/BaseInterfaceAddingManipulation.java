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

package de.monticore.codegen.mc2cd.manipul;

import java.util.function.UnaryOperator;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDDefinition;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.umlcd4a.cd4analysis._ast.CD4AnalysisNodeFactory;

/**
 * Adds the base interface for all AST nodes of the handled grammar
 * 
 * @author Galina Volkova
 */
public final class BaseInterfaceAddingManipulation implements UnaryOperator<ASTCDCompilationUnit> {
  
  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    ASTCDInterface baseInterface = CD4AnalysisNodeFactory.createASTCDInterface();
    baseInterface.setName(getBaseInterfaceName(cdCompilationUnit.getCDDefinition()));
  //  baseInterface.getInterfaces().add(TransformationHelper.createSimpleReference("ASTNode"));
    cdCompilationUnit.getCDDefinition().getCDInterfaces().add(baseInterface); 
    return cdCompilationUnit;
  }
  
  public static String getBaseInterfaceName(ASTCDDefinition astcdDefinition) {
    return TransformationHelper.AST_PREFIX + astcdDefinition.getName() + "Node";
  }
  
}
