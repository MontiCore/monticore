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
 * This class implements the PPTNode interface, offering the basic
 * funcitionality for it. All nodes in the pretty printing tree should extend
 * from this one.
 * 
 * @author diego
 */
//STATE SMELL? needed for pretty printer generation, maybe this should be part of generator project.
@Deprecated
public class PPTCNode implements PPTNode {
  protected PPTNode parent;
  protected String value;
  protected String postFormat;
  protected String preFormat;
  protected Class<? extends ASTNode> astClass;
  protected int level;
  
  /**
   * Constructs a new PPTCNode.
   */
  public PPTCNode() {
    value = "";
    postFormat = "";
    preFormat = "";
  }
  
  /**
   * Returns the parent of node.
   * 
   * @return Parent of the node. null if it has no parent.
   */
  public PPTNode getParent() {
    return parent;
  }
  
  /**
   * Sets the parent of the node. Also, it adds this node to the list of
   * children of the parent node, but it doesn't remove it from the children
   * list of the previous parent.
   * 
   * @param parent Parent of the node in the tree.
   */
  public void setParent(PPTNode parent) {
    this.parent = parent;
    
    if (parent instanceof PPTNodeList) {
      ((PPTNodeList) parent).getChildren().add(this);
    }
  }
  
  /**
   * Returns the string with the post-format, i.e. the format applied after the
   * node is printed.
   * 
   * @return String with the post-format.
   */
  public String getPostFormat() {
    return this.postFormat;
  }
  
  /**
   * Sets the post-format of the node, i.e. the format applied after the node is
   * printed.
   * 
   * @param postFormat String with the post-format.
   */
  public void setPostFormat(String postFormat) {
    this.postFormat = postFormat;
  }
  
  /**
   * Returns the string with the pre-format, i.e. the format applied before the
   * node is printed.
   * 
   * @return String with the pre-format.
   */
  public String getPreFormat() {
    return this.preFormat;
  }
  
  /**
   * Sets the pre-format of the node, i.e. the format applied before the node is
   * printed.
   * 
   * @param preFormat String with the pre-format.
   */
  public void setPreFormat(String preFormat) {
    this.preFormat = preFormat;
  }
  
  /**
   * Returns the content of the node, which is the value that will be pretty
   * printed.
   * 
   * @return Content of the node.
   */
  public String getValue() {
    return this.value;
  }
  
  /**
   * Sets the content of the node, which is the value that will be pretty
   * printed.
   * 
   * @param value Content of the node.
   */
  public void setValue(String value) {
    this.value = value;
  }
  
  /**
   * Returns the AST class where the PPT node comes from. For example, for a
   * PPTRule node, it will be the AST class of the rule, but for a PPTTerminal,
   * the AST class will be the one of the rule that contains it.
   * 
   * @return AST class where the PPT node comes from.
   */
  public Class<? extends ASTNode> getAstClass() {
    return astClass;
  }
  
  /**
   * Sets the AST class where the PPT node comes from. For example, for a
   * PPTRule node, it will be the AST class of the rule, but for a PPTTerminal,
   * the AST class will be the one of the rule that contains it.
   * 
   * @param astClass AST class where the PPT node comes from.
   */
  public void setAstClass(Class<? extends ASTNode> astClass) {
    this.astClass = astClass;
  }
  
  /**
   * Returns the level of the node in the tree.
   * 
   * @return Level of the node in the tree.
   */
  public int getLevel() {
    return level;
  }
  
  /**
   * Sets the level of the node in the tree.
   * 
   * @param level Level of the node in the tree.
   */
  public void setLevel(int level) {
    this.level = level;
  }
  
  /**
   * Traverses the node visiting it and its children later. It is meant to be
   * used with the PPTVisitor.
   * 
   * @param visitor Visitor which traverse the pretty print tree.
   */
  public void traverse(PPTVisitor visitor) {
    // visits node
    visitor.visit(this);
    
    // visits its children
    if (this instanceof PPTNodeList) {
      for (PPTNode n : ((PPTNodeList) this).getChildren()) {
        visitor.startVisit(n);
      }
    }
  }
  
  /**
   * Returns the first upper node which is a PPTRule.
   * 
   * @return First upper node which is a PPTRule. If no PPTRule is found, null
   *         is returned.
   */
  public PPTRule getParentRule() {
    PPTNode parent = this.parent;
    
    while (parent != null && !(parent instanceof PPTRule)) {
      parent = parent.getParent();
    }
    
    return (PPTRule) parent;
  }
  
  /**
   * Returns the next node in the pretty print order, that is to say, the order
   * of the code.
   * 
   * @param from The node where the actual node was reached from.
   * @return Next node. It can be null if this is the last node.
   */
  @Override
  public PPTNode next(PPTNode from) {
    // if it comes from the parent, tries to look at the children
    if (parent == from) {
      if (parent instanceof PPTNodeList) {
        PPTNodeList list = (PPTNodeList) parent;
        
        if (list.getChildren().size() > 0)
          return list.getChildren().get(0);
        else
          return parent;
      }
      else {
        return parent;
      }
    }
    else {
      // it comes from a children, so if it wasn't the last children,
      // it go for the next children. if there are no more children, the
      // parent is returned.
      PPTNodeList list = (PPTNodeList) parent;
      int index = list.getChildren().indexOf(from);
      
      if (index == (list.getChildren().size() - 1))
        return parent;
      else
        return list.getChildren().get(index + 1);
    }
  }
  
  /**
   * Returns the previous node depending on if we are placed on the pre-format
   * or post-format.
   * 
   * @param pre true if we are in the pre-format. false if we are in the
   *          post-format.
   * @return Previous node.
   */
  public PPTNode getFrom(boolean pre) {
    if (pre) {
      return parent;
    }
    else {
      if (this instanceof PPTNodeList) {
        PPTNodeList list = (PPTNodeList) this;
        
        if (list.getChildren().size() > 0)
          return list.getChildren().get(list.getChildren().size() - 1);
        else
          return parent;
      }
      else {
        return parent;
      }
    }
  }
}
