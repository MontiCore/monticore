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

package de.monticore.ast;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.SourcePosition;

/**
 * Foundation interface for all AST-classes
 * 
 * @author krahn
 */
public interface ASTNode {
  
  /**
   * Performs a deep clone of this ASTNode and all of its successors
   * 
   * @return Clone of current ASTNode with a parent which is equal to null
   */
  public ASTNode deepClone();
  
  /**
   * Returns the start position of this ASTNode
   * 
   * @return start position of this ASTNode
   */
  // TODO GV: change return type to Optional after the next bootstrapping step
  public SourcePosition get_SourcePositionEnd();
  
  /**
   * Sets the end position of this ASTNode
   * 
   * @param end end position of this ASTNode
   */
  public void set_SourcePositionEnd(SourcePosition end);
  
  /**
   * Returns the end source position of this ASTNode
   * 
   * @return end position of this ASTNode
   */
  // TODO GV: change return type to Optional after the next bootstrapping step
  public SourcePosition get_SourcePositionStart();
  
  /**
   * Sets the start position of this ASTNode
   * 
   * @param start start position of this ASTNode
   */
  public void set_SourcePositionStart(SourcePosition start);
  
  /**
   * Returns list of all comments which are associated with this ASTNode and are
   * prior to the ASTNode in the input file
   * 
   * @return list of comments
   */
  public List<Comment> get_PreComments();
  
  /**
   * Sets list of all comments which are associated with this ASTNode and are
   * prior to the ASTNode in the input file
   * 
   * @param _precomments list of comments
   */
  public void set_PreComments(List<Comment> _precomments);
  
  /**
   * Returns list of all comments which are associated with this ASTNode and can
   * be found after the ASTNode in the input file
   * 
   * @return list of comments
   */
  public List<Comment> get_PostComments();
  
  /**
   * Sets list of all comments which are associated with this ASTNode and can be
   * found after the ASTNode in the input file
   * 
   * @param _postcomments list of comments
   */
  public void set_PostComments(List<Comment> _postcomments);
  
  public boolean equalAttributes(Object o);
  
  public boolean equalsWithComments(Object o);
  
  /**
   * Compare this object to another Object. Do not take comments into account.
   * This method returns the same value as <tt>deepEquals(Object o, boolean 
   * forceSameOrder)</tt> method when using the default value for forceSameOrder
   * of each Node.
   */
  public boolean deepEquals(Object o);
  
  /**
   * Compare this object to another Object. Take comments into account. This
   * method returns the same value as
   * <tt>deepEqualsWithComment(Object o, boolean forceSameOrder)</tt> method
   * when using the default value for forceSameOrder of each Node.
   */
  public boolean deepEqualsWithComments(Object o);
  
  /**
   * Compare this object to another Object. Do not take comments into account.
   * 
   * @param o the object to compare this node to
   * @param forceSameOrder consider the order in ancestor lists, even if these
   * lists are of stereotype <tt>&lt;&lt;unordered&gt;&gt;</tt> in the grammar.
   */
  public boolean deepEquals(Object o, boolean forceSameOrder);
  
  /**
   * Compare this object to another Object. Take comments into account.
   * 
   * @param o the object to compare this node to
   * @param forceSameOrder consider the order in ancestor lists, even if these
   * lists are of stereotype <tt>&lt;&lt;unordered&gt;&gt;</tt> in the grammar.
   */
  public boolean deepEqualsWithComments(Object o, boolean forceSameOrder);
  
  /**
   * @returns a collection of all child nodes of this node
   */
  public Collection<ASTNode> get_Children();
  
  /**
   * This method removes the reference from this node to a child node, no matter
   * in which attribute it is stored.
   * 
   * @param child the target node of the reference to be removed
   */
  public void remove_Child(ASTNode child);

  /**
   * Sets the enclosing scope of this ast node.
   *
   * @param enclosingScope the enclosing scope of this ast node
   */
  public void setEnclosingScope(Scope enclosingScope);

  /**
   * @return the enclosing scope of this ast node
   */
  public Optional<? extends Scope> getEnclosingScope();

  /**
   * Sets the corresponding symbol of this ast node.
   *
   * @param symbol the corresponding symbol of this ast node..
   */
  public void setSymbol(Symbol symbol);

  /**
   * @return the corresponding symbol of this ast node.
   */
  public Optional<? extends Symbol> getSymbol();
  
}
