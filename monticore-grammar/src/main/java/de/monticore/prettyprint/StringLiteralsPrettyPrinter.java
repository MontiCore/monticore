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

import de.monticore.stringliterals._ast.ASTCharLiteral;
import de.monticore.stringliterals._ast.ASTStringLiteral;
import de.monticore.stringliterals._ast.ASTStringLiteralsNode;
import de.monticore.stringliterals._visitor.StringLiteralsVisitor;

public class StringLiteralsPrettyPrinter implements StringLiteralsVisitor {
  
  private IndentPrinter printer = null;
  
  public StringLiteralsPrettyPrinter(IndentPrinter printer) {
    this.printer = printer;
  }
  
  @Override
  public void handle(ASTCharLiteral node) {
    getPrinter().print("'" + node.getSource() + "'");
  }
  
  @Override
  public void handle(ASTStringLiteral node) {
    getPrinter().print("\"" + node.getSource() + "\"");
  }
  
  public IndentPrinter getPrinter() {
    return this.printer;
  }
  
  public String prettyprint(ASTStringLiteralsNode node) {
    getPrinter().clearBuffer();
    node.accept(getRealThis());
    return getPrinter().getContent();
  }
  
}
