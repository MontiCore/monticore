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


/**
 * All nodes in the pretty printing tree have to implement this interface. If
 * provides methods for the basic attributes, like parent, value, etc., and
 * methods to traverse the node.
 * 
 * @author diego
 */
//STATE SMELL? needed for pretty printer generation, maybe this should be part of generator project.
@Deprecated
public interface PPTNode {
  /**
   * Returns the parent of node.
   * 
   * @return Parent of the node. null if it has no parent.
   */
  PPTNode getParent();
  
  /**
   * Returns the AST class where the PPT node comes from. For example, for a
   * PPTRule node, it will be the AST class of the rule, but for a PPTTerminal,
   * the AST class will be the one of the rule that contains it.
   * 
   * @return AST class where the PPT node comes from.
   */
  Class<? extends ASTNode> getAstClass();
  
  /**
   * Returns the string with the pre-format, i.e. the format applied before the
   * node is printed.
   * 
   * @return String with the pre-format.
   */
  String getPreFormat();
  
  /**
   * Sets the pre-format of the node, i.e. the format applied before the node is
   * printed.
   * 
   * @param preFormat String with the pre-format.
   */
  void setPreFormat(String s);
  
  /**
   * Returns the string with the post-format, i.e. the format applied after the
   * node is printed.
   * 
   * @return String with the post-format.
   */
  String getPostFormat();
  
  /**
   * Sets the post-format of the node, i.e. the format applied after the node is
   * printed.
   * 
   * @param postFormat String with the post-format.
   */
  void setPostFormat(String s);
  
  /**
   * Returns the content of the node, which is the value that will be pretty
   * printed.
   * 
   * @return Content of the node.
   */
  String getValue();
  
  /**
   * Returns the first upper node which is a PPTRule.
   * 
   * @return First upper node which is a PPTRule. If no PPTRule is found, null
   *         is returned.
   */
  PPTRule getParentRule();
  
  /**
   * Returns the level of the node in the tree.
   * 
   * @return Level of the node in the tree.
   */
  int getLevel();
  
  /**
   * Traverses the node visiting it and its children later. It is meant to be
   * used with the PPTVisitor.
   * 
   * @param visitor Visitor which traverse the pretty print tree.
   */
  void traverse(PPTVisitor visitor);
  
  /**
   * Returns the next node in the pretty print order, that is to say, the order
   * of the code.
   * 
   * @param from The node where the actual node was reached from.
   * @return Next node. It can be null if this is the last node.
   */
  PPTNode next(PPTNode from);
  
  /**
   * Returns the previous node depending on if we are placed on the pre-format
   * or post-format.
   * 
   * @param pre true if we are in the pre-format. false if we are in the
   *          post-format.
   * @return Previous node.
   */
  PPTNode getFrom(boolean pre);
}
