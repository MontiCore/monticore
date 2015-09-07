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

import de.monticore.umlcd4a.cd4analysis._ast.ASTCDCompilationUnit;

import java.util.function.UnaryOperator;

/**
 * The term manipulation refers to transformations that write to the same AST that they're reading
 * from.
 * <p>
 * This function composes all CD manipulations into a single function.
 * 
 * @author Sebastian Oberhoff
 */
public final class CDManipulation implements UnaryOperator<ASTCDCompilationUnit> {
  
  @Override
  public ASTCDCompilationUnit apply(ASTCDCompilationUnit cdCompilationUnit) {
    return new ListClassAddingManipulation()
        .andThen(new BaseInterfaceAddingManipulation())
        .andThen(new AddAttributesOfExtendedInterfacesManipulation())
        .andThen(new RemoveRedundantAttributesManipulation())
        .andThen(new RemoveRedundantSupertypesManipulation())
        .andThen(new JavaAndCdConformNameManipulation())
        .andThen(new VisibilityManipulation())
        .apply(cdCompilationUnit);
  }
}
