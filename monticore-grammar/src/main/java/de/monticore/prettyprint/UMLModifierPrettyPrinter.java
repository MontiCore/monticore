/*
 * ******************************************************************************
 * MontiCore Language Workbench
 * Copyright (c) 2015, MontiCore, All rights reserved.
 *
 * getRealThis() project is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 * getRealThis() library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with getRealThis() project. If not, see <http://www.gnu.org/licenses/>.
 * ******************************************************************************
 */
package de.monticore.prettyprint;

import de.monticore.umlmodifier._ast.ASTModifier;
import de.monticore.umlmodifier._ast.ASTUMLModifierNode;
import de.monticore.umlmodifier._visitor.UMLModifierVisitor;

public class UMLModifierPrettyPrinter implements UMLModifierVisitor {
  
  private IndentPrinter printer = null;
  
  public UMLModifierPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }
  
  @Override
  public void handle(ASTModifier a) {
    // print stereotypes
    if (a.isPresentStereotype()) {
      a.getStereotype().accept(getRealThis());
      getPrinter().print(" ");
    }
    if (a.isPublic()) {
      getPrinter().print("public ");
    }
    if (a.isPrivate()) {
      getPrinter().print("private ");
    }
    if (a.isProtected()) {
      getPrinter().print("protected ");
    }
    if (a.isFinal()) {
      getPrinter().print("final ");
    }
    if (a.isAbstract()) {
      getPrinter().print("abstract ");
    }
    if (a.isLocal()) {
      getPrinter().print("local ");
    }
    if (a.isDerived()) {
      getPrinter().print("derived ");
    }
    if (a.isReadonly()) {
      getPrinter().print("readonly ");
    }
    if (a.isStatic()) {
      getPrinter().print("static ");
    }
  }
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTUMLModifierNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
  
}
