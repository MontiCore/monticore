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

package de.monticore.generating.templateengine.reporting.commons;

import java.util.List;
import java.util.Map;
import java.util.Stack;

import de.monticore.ast.ASTNode;

import com.google.common.collect.Lists;

import de.monticore.visitor.CommonVisitor;

/**
 * We use the visit mechanism to map the AST to a list of showing the AST-Nodes
 * as tree. As a basis we use ast2idents that maps each node into a string in a
 * compact form. Result is store as list for print
 * 
 * @author BR
 */
public class TreePrintVisitor implements CommonVisitor {
  private ReportingRepository repo;
  
  // output to be stored here:
  protected List<String> treeResult;
  
  Stack<String> indent;
  
  Stack<Integer> siblingsLeft;
  
  static final String INITALINDENT = "";
  
  static final String INDENT1 = "+--";
  
  static final String INDENT2 = "|  ";
  
  static final String INDENT3 = "   ";
  
  // contains possible decorations for each entry in the tree
  // printed at the end of the line
  // null is a valid value (= no decoration)
  Map<String, String> endLineDecoration;
  
  // contains possible extra infos for each entry in the tree
  // printed in individual lines (and indented)
  // null is a valid value (= no extra info) and no empty line
  Map<String, List<String>> astNodeExtraInfos;
  
  /* visits all nodes and prints them as one liner with correct indentation */
  @Override
  public void visit(ASTNode a) {
    indent.pop();
    int cl = siblingsLeft.pop();
    
    // prepare the output
    String ident = repo.getASTNodeNameFormatted(a);
    String out = indent.peek() + INDENT1 + ident;
    if (endLineDecoration != null && endLineDecoration.containsKey(ident)) {
      String decor = endLineDecoration.get(ident);
      out = Layouter.padleft(out, 60) + " " + decor;
    }
    treeResult.add(out);
    
    cl--; // one less sibling
    String nextIndent = (cl >= 1) ? INDENT2 : INDENT3;
    
    indent.push(indent.peek() + nextIndent);
    siblingsLeft.push(cl);
    
    // care for potential children:
    indent.push(indent.peek() + "|  ");
    siblingsLeft.push(a.get_Children().size());
    
    // print the extra infos
    if (astNodeExtraInfos != null && astNodeExtraInfos.containsKey(ident)) {
      List<String> extras = astNodeExtraInfos.get(ident);
      for (String s : extras) {
        treeResult.add(Layouter.padleft(indent.peek(), 20) + "      "
            + s);
      }
    }
    
  }
  
  @Override
  public void endVisit(ASTNode a) {
    // remove children stuff
    indent.pop();
    siblingsLeft.pop();
  }
  
  /**
   * produces the raw tree without any decoration
   * 
   * @param ast2idents
   * @param treeResult
   */
  public TreePrintVisitor() {
    this(null, null, null);
  }
  
  /**
   * produces the tree with an inline decoration (at the end of each line)
   * 
   * @param ast2idents
   * @param treeResult
   * @param endLineDecoration
   */
  public TreePrintVisitor(ReportingRepository repo,
      Map<String, String> endLineDecoration,
      Map<String, List<String>> astNodeExtraInfos) {
    super();
    this.repo = repo;
    this.treeResult = Lists.newArrayList();
    this.endLineDecoration = endLineDecoration;
    this.astNodeExtraInfos = astNodeExtraInfos;
    indent = new Stack<String>();
    indent.add(INITALINDENT); // initial indentation
    indent.add(INITALINDENT + INDENT2); // + one child
    siblingsLeft = new Stack<Integer>();
    siblingsLeft.add(1);
    siblingsLeft.add(1);
  }
  
  public List<String> getTreeResult() {
    return treeResult;
  }
}
