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

import de.monticore.completeness._ast.ASTCompleteness;
import de.monticore.completeness._ast.ASTCompletenessNode;
import de.monticore.completeness._visitor.CompletenessVisitor;

public class CompletenessPrettyPrinter implements CompletenessVisitor {
  
  private IndentPrinter printer = null;
  
  public CompletenessPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }
  
  @Override
  public void handle(ASTCompleteness node) {
    if (node.isComplete()) {
      getPrinter().print("(c)");
    }
    else if (node.isIncomplete()) {
      getPrinter().print("(...)");
    }
    else if (node.isLeftComplete()) {
      getPrinter().print("(c,...)");
    }
    else if (node.isRightComplete()) {
      getPrinter().print("(...,c)");
    }
  }
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTCompletenessNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
  
}
