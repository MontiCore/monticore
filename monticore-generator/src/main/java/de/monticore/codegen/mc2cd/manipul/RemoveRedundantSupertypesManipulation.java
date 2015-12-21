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

import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.StreamSupport;

import com.google.common.collect.Iterables;

import de.monticore.codegen.mc2cd.TransformationHelper;
import de.monticore.types.types._ast.ASTReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDClass;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDInterface;
import de.monticore.utils.ASTNodes;

public class RemoveRedundantSupertypesManipulation implements UnaryOperator<ASTCDCompilationUnit> {
  
  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    for (ASTCDClass cdClass : ASTNodes.getSuccessors(cdCompilationUnit, ASTCDClass.class)) {
      // TODO SO <- GV: don't need it any more?
      // removeRedundantSuperTypes(cdClass.getSuperclass());
      removeRedundantSuperTypes(cdClass.getInterfaces());
    }
    for (ASTCDInterface cdInterface : ASTNodes.getSuccessors(cdCompilationUnit,
        ASTCDInterface.class)) {
      removeRedundantSuperTypes(cdInterface.getInterfaces());
    }
    return cdCompilationUnit;
  }
  
  private void removeRedundantSuperTypes(List<ASTReferenceType> superClasses) {
    for (int i = 0; i < superClasses.size();) {
      ASTReferenceType inspectedAttribute = superClasses.get(i);
      Iterable<ASTReferenceType> remainingAttributes = Iterables.filter(superClasses,
          attribute -> !attribute.equals(inspectedAttribute));
      if (isRedundant(inspectedAttribute, remainingAttributes)) {
        superClasses.remove(i);
      }
      else {
        i++;
      }
    }
  }
  
  private boolean isRedundant(ASTReferenceType inspectedReference,
      Iterable<ASTReferenceType> remainingReferences) {
    return StreamSupport.stream(remainingReferences.spliterator(), false)
        .anyMatch(
            ref -> TransformationHelper.typeToString(inspectedReference).equals(
                TransformationHelper.typeToString(ref)));
  }
}
