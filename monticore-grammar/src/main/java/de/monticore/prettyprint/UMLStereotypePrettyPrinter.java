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

import de.monticore.umlstereotype._ast.ASTStereoValue;
import de.monticore.umlstereotype._ast.ASTStereotype;
import de.monticore.umlstereotype._ast.ASTUMLStereotypeNode;
import de.monticore.umlstereotype._visitor.UMLStereotypeVisitor;

public class UMLStereotypePrettyPrinter implements UMLStereotypeVisitor {
  
  private IndentPrinter printer = null;
  
  public UMLStereotypePrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }
  
  @Override
  public void handle(ASTStereotype a) {
    getPrinter().print("<<");
    String sep = "";
    for (ASTStereoValue value : a.getValueList()) {
      getPrinter().print(sep);
      value.accept(getRealThis());
      sep = ", ";
    }
    getPrinter().print(">>");
  }
  
  @Override
  public void handle(ASTStereoValue a) {
    getPrinter().print(a.getName());
    if (a.isPresentText()) {
      getPrinter().print("=" + "\"" + a.getText().getSource() + "\"");
    }
  }
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTUMLStereotypeNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
}
