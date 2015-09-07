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

package de.monticore.ppgen.formatter;

import de.monticore.ast.ASTNode;
import de.monticore.prettyprint.IndentPrinter;

/**
 * This printer is meant to be used as a compatibility layer between generated
 * pretty printers and old pretty printers. Generated ones use TreePrinter to
 * pretty print the code, so when a old grammar which uses IndentPrinter is
 * embedded in a grammar with generated pretty printers, instead of writing
 * directly in a IndentPrinter object, this class will be used, which provides
 * the same interface as IndentPrinter but writes the content in a TreePrinter
 * as nodes of type PPTText. To see how it is used, see:
 * 
 * @see mc.ast.PrettyPrinter
 * @author diego
 */
//STATE SMELL? needed for pretty printer generation, maybe this should be part of generator project.
@Deprecated
public class TreeCompatibleIndentPrinter extends IndentPrinter {
  private TreePrinter tp;
  private Class<? extends ASTNode> astClass;
  
  /**
   * Constructor.
   * 
   * @param tp The tree printer where the code should be written.
   * @param astClass The AST class for the nodes created for this printer.
   *          Usually it is the class of the external rule.
   */
  public TreeCompatibleIndentPrinter(TreePrinter tp, Class<? extends ASTNode> astClass) {
    this.tp = tp;
    this.astClass = astClass;
  }
  
  /**
   * Prints a string in the tree printer taking care of new lines.
   * 
   * @param s String to print.
   */
  @Override
  protected void doPrint(String s) {
    int pos = s.indexOf("\n");
    
    // each line is written in a different PPTText node and a new line is
    // added as a post-format
    while (pos >= 0) {
      String substring = s.substring(0, pos);
      
      PPTText n = tp.printText(substring, astClass);
      TreePrinter.addPostFormat(TreePrinter.NEW_LINE, n);
      s = s.substring(pos + 1);
      pos = s.indexOf("\n");
    }
    
    if (!s.equals(""))
      tp.printText(s, astClass);
  }
  
  /**
   * Prints a string in the tree printer without taking care of new lines.
   * 
   * @param o Object to print with toString() method.
   */
  @Override
  public void printWithoutProcessing(Object o) {
    tp.printText(o.toString(), astClass);
  }
  
  /**
   * Prints a new line.
   */
  @Override
  public void println() {
    doPrint("\n");
  }
  
  /**
   * Adds indent or uninden format rules to the tree printer.
   * 
   * @param i Number of times to indent or unindent. A positive value indent,
   *          while a negative one unindent.
   */
  @Override
  public void indent(int i) {
    if (i > 0) {
      PPTText n = tp.printText("", astClass);
      
      while (i > 0) {
        TreePrinter.addPreFormat(TreePrinter.INDENT, n);
        i--;
      }
    }
    else {
      PPTText n = tp.printText("", astClass);
      
      while (i < 0) {
        TreePrinter.addPreFormat(TreePrinter.UNINDENT, n);
        i++;
      }
    }
  }
  
  /**
   * Adds an empty node to the tree printer with an indent format rule.
   */
  @Override
  public void indent() {
    PPTText n = tp.printText("", astClass);
    TreePrinter.addPreFormat(TreePrinter.INDENT, n);
  }
  
  /**
   * Adds an empty node to the tree printer with an unindent format rule.
   */
  @Override
  public void unindent() {
    PPTText n = tp.printText("", astClass);
    TreePrinter.addPreFormat(TreePrinter.UNINDENT, n);
  }
}
