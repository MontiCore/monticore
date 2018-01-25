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

import de.monticore.mcnumbers._ast.ASTDecimal;
import de.monticore.mcnumbers._ast.ASTInteger;
import de.monticore.mcnumbers._ast.ASTMCNumbersNode;
import de.monticore.mcnumbers._visitor.MCNumbersVisitor;

public class MCNumbersPrettyPrinter implements MCNumbersVisitor {
  
  private IndentPrinter printer = null;
  
  public MCNumbersPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }
  
  @Override
  public void handle(ASTDecimal node) {
    getPrinter().print(node.getSource());
  }
  
  @Override
  public void handle(ASTInteger node) {
    if (node.isNegative()) {
      getPrinter().print("-");
    }
    getPrinter().print(node.getDecimalpart());
  }
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTMCNumbersNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
  
}
