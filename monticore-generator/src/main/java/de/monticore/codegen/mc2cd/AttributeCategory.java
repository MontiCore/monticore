/*
 * ******************************************************************************
 * MontiCore Language Workbench
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

package de.monticore.codegen.mc2cd;

import static de.monticore.codegen.mc2cd.TransformationHelper.typeToString;
import de.monticore.types.types._ast.ASTPrimitiveType;
import de.monticore.types.types._ast.ASTSimpleReferenceType;
import de.monticore.umlcd4a.cd4analysis._ast.ASTCDAttribute;

/**
 * An enumeration of the different categories that ASTCDAttributes in a CD AST can fall into.
 */
public enum AttributeCategory {
  
  /**
   * a primitive attribute such as boolean, int or char
   */
  PRIMITIVE,
  /**
   * an attribute wrapped by an Optional such as 'Optional<String>'
   */
  OPTIONAL,
  /**
   * a standard attribute such as 'String s'
   */
  STANDARD,
  /**
   * a reference to a generic List such as 'List<String>'
   */
  GENERICLIST;
  
  public static AttributeCategory determineCategory(ASTCDAttribute cdAttribute) {
    if (cdAttribute.getType() instanceof ASTPrimitiveType) {
      return PRIMITIVE;
    }
    if (isGenericList(cdAttribute)) {
      return GENERICLIST;
    }
    if (isOptional(cdAttribute)) {
      return OPTIONAL;
    }
    return STANDARD;
  }
  
  private static boolean isGenericList(ASTCDAttribute cdAttribute) {
    boolean hasGenerics = ((ASTSimpleReferenceType) cdAttribute.getType()).getTypeArguments() != null;
    return "java.util.List".equals(TransformationHelper.typeToString(cdAttribute.getType()))
        && hasGenerics;
  }
  
  private static boolean isOptional(ASTCDAttribute cdAttribute) {
    return "Optional".equals(TransformationHelper.typeToString(cdAttribute.getType()));
  }
  
}
