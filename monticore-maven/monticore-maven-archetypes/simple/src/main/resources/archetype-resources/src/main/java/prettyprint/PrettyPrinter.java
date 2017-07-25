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

package ${package}.prettyprint;

import ${package}.mydsl._ast.ASTMyElement;
import ${package}.mydsl._ast.ASTMyField;
import ${package}.mydsl._ast.ASTMyModel;
import ${package}.mydsl._visitor.MyDSLVisitor;

/**
 * Pretty prints models. Use {@link #print(ASTMyModel)} to start a pretty print
 * and get the result by using {@link #getResult()}.
 */
public class PrettyPrinter implements MyDSLVisitor {
  
  private String result = "";
  
  private int indention = 0;
  
  private String indent = "";
  
  /**
   * Prints the model
   * 
   * @param model
   */
  public void print(ASTMyModel model) {
    handle(model);
  }
  
  /**
   * Gets the printed result.
   * 
   * @return the result of the pretty print.
   */
  public String getResult() {
    return this.result;
  }
  
  @Override
  public void visit(ASTMyModel node) {
    println("model " + node.getName() + " {");
    indent();
  }
  
  @Override
  public void endVisit(ASTMyModel node) {
    unindent();
    println("}");
  }
  
  @Override
  public void traverse(ASTMyModel node) {
    node.getMyElements().stream().forEach(e -> e.accept(getRealThis()));
  }
  
  @Override
  public void visit(ASTMyElement node) {
    println("element " + node.getName() + " {");
    indent();
  }
  
  @Override
  public void endVisit(ASTMyElement node) {
    unindent();
    println("}");
  }
  
  @Override
  public void traverse(ASTMyElement node) {
    node.getMyFields().stream().forEach(e -> e.accept(getRealThis()));
  }
  
  @Override
  public void visit(ASTMyField node) {
    println(node.getName() + " " + node.getType() + ";");
  }
  
  private void print(String s) {
    result += (indent + s);
    indent = "";
  }
  
  private void println(String s) {
    result += (indent + s + "\n");
    indent = "";
    calcIndention();
  }
  
  private void calcIndention() {
    indent = "";
    for (int i = 0; i < indention; i++) {
      indent += "  ";
    }
  }
  
  private void indent() {
    indention++;
    calcIndention();
  }
  
  private void unindent() {
    indention--;
    calcIndention();
  }
}
