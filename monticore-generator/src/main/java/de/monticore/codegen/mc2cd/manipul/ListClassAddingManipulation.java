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

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.umlcd4a.cd4analysis._ast.*;
import de.monticore.utils.ASTNodes;

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Adds the sibling list class for each already existing AST class.
 *
 * @author Sebastian Oberhoff
 */
final class ListClassAddingManipulation implements UnaryOperator<ASTCDCompilationUnit> {

  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {

    for (ASTCDDefinition cdDefinition : ASTNodes
        .getSuccessors(cdCompilationUnit, ASTCDDefinition.class)) {

      List<ASTCDClass> listClasses = Stream
          .concat(cdDefinition.getCDClasses().stream().map(this::createListClassForClass),
              cdDefinition.getCDInterfaces().stream().map(this::createListClassForInterface))
          .collect(Collectors.toList());
      cdDefinition.getCDClasses().addAll(listClasses);
    }
    return cdCompilationUnit;
  }

  private ASTCDClass createListClassForClass(ASTCDClass cdClass) {
    ASTCDClass listClass = CD4AnalysisNodeFactory.createASTCDClass();
    listClass.setName(cdClass.getName() + "List");

    ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    cdAttribute.setName("list");
    // TODO GV:
    cdAttribute.setType(TransformationHelper.createSimpleReference("EObjectContainmentEList", cdClass.getName()));
    listClass.getCDAttributes().add(cdAttribute);
    return listClass;
  }

  private ASTCDClass createListClassForInterface(ASTCDInterface cdInterface) {
    ASTCDClass listClass = CD4AnalysisNodeFactory.createASTCDClass();
    listClass.setName(cdInterface.getName() + "List");

    ASTCDAttribute cdAttribute = CD4AnalysisNodeFactory.createASTCDAttribute();
    cdAttribute.setName("list");
    cdAttribute.setType(TransformationHelper.createSimpleReference("ArrayList",
        cdInterface.getName()));
    listClass.getCDAttributes().add(cdAttribute);
    return listClass;
  }
}
