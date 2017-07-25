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

package de.monticore.codegen.cd2java.ast;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 */
public enum AstAdditionalMethods {
  
  deepEquals("public boolean deepEquals(Object o);"),
  
  deepEqualsWithOrder("public boolean deepEquals(Object o, boolean forceSameOrder);"),
  
  deepEqualsWithComments("public boolean deepEqualsWithComments(Object o);"),
  
  deepEqualsWithCommentsWithOrder("public boolean deepEqualsWithComments(Object o, boolean forceSameOrder);"),
  
  equalAttributes("public boolean equalAttributes(Object o);"),
  
  equalsWithComments("public boolean equalsWithComments(Object o);"),
  
  // %s the ast-class name as return type
  deepClone("public %s deepClone();"),
  
  // %s the ast-class name as return and input parameter type
  deepCloneWithOrder("public %s deepClone(%s result);"),
  
  get_Children("public java.util.Collection<de.monticore.ast.ASTNode> get_Children();"),
  
  remove_Child("public void remove_Child(de.monticore.ast.ASTNode child);"),
  
  // %s the ast-class name as return type
  _construct("protected %s _construct();"),
  
  // %s the language specific visitor-type as full-qualified-name
  accept("public void accept(%s visitor);"),
  
  getBuilder("public static Builder getBuilder();");
  
  private String methodDeclaration;
  
  private AstAdditionalMethods(String header) {
    this.methodDeclaration = header;
  }
  
  public String getDeclaration() {
    return methodDeclaration;
  }
  
}
