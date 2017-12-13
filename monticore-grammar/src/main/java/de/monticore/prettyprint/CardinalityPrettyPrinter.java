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

import de.monticore.cardinality._ast.ASTCardinality;
import de.monticore.cardinality._ast.ASTCardinalityNode;
import de.monticore.cardinality._visitor.CardinalityVisitor;
import de.monticore.prettyprint.IndentPrinter;

public class CardinalityPrettyPrinter implements CardinalityVisitor {
  
  private IndentPrinter printer = null;
  
  public CardinalityPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }
  
  @Override
  public void handle(ASTCardinality node) {
    getPrinter().print("[");
    if (node.isMany()) {
      getPrinter().print("*");
    }
    else {
      getPrinter().print(node.getLowerBound());
      if (node.getLowerBound() != node.getUpperBound() || node.isNoUpperLimit()) {
        getPrinter().print("..");
        if (node.isNoUpperLimit()) {
          getPrinter().print("*");
        }
        else {
          getPrinter().print(node.getUpperBound());
        }
      }
    }
    getPrinter().print("]");
  }
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTCardinalityNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
  
}
