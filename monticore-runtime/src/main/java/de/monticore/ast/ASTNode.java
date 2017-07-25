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

package de.monticore.ast;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;

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
  default public ASTNode deepClone(ASTNode result) {
    Log.errorIfNull(result, "0xA4040 The argument ASTNode of the 'deepClone' method must not be null.");
    result.set_SourcePositionStart(get_SourcePositionStart().clone());
    result.set_SourcePositionEnd(get_SourcePositionEnd().clone());
    for (de.monticore.ast.Comment x : get_PreComments()) {
      result.get_PreComments().add(new de.monticore.ast.Comment(x.getText()));
    }
    for (de.monticore.ast.Comment x : get_PostComments()) {
      result.get_PostComments().add(new de.monticore.ast.Comment(x.getText()));
    }
    
    return result;
  }
  
  default public boolean equalAttributes(Object o) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4078 Method equalAttributes is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  default public boolean equalsWithComments(Object o) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4042 Method equalsWithComments is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  /**
   * Compare this object to another Object. Do not take comments into account.
   * This method returns the same value as <tt>deepEquals(Object o, boolean 
   * forceSameOrder)</tt> method when using the default value for forceSameOrder
   * of each Node.
   */
  default public boolean deepEquals(Object o) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4043 Method deepEquals is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  /**
   * Compare this object to another Object. Take comments into account.
   * 
   * @param o the object to compare this node to
   * @param forceSameOrder consider the order in ancestor lists, even if these
   * lists are of stereotype <tt>&lt;&lt;unordered&gt;&gt;</tt> in the grammar.
   */
  default public boolean deepEqualsWithComments(Object o) {
    throw new CompareNotSupportedException(
        "0xA4044 Method deepEqualsWithComments is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  /**
   * Compare this object to another Object. Do not take comments into account.
   * 
   * @param o the object to compare this node to
   * @param forceSameOrder consider the order in ancestor lists, even if these
   * lists are of stereotype <tt>&lt;&lt;unordered&gt;&gt;</tt> in the grammar.
   */
  default public boolean deepEquals(Object o, boolean forceSameOrder) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4045 Method deepEquals is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  /**
   * Compare this object to another Object. Take comments into account. This
   * method returns the same value as
   * <tt>deepEqualsWithComment(Object o, boolean forceSameOrder)</tt> method
   * when using the default value for forceSameOrder of each Node.
   */
  default public boolean deepEqualsWithComments(Object o, boolean forceSameOrder) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4046 Method deepEqualsWithComments is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  /**
   * Performs a deep clone of this ASTNode and all of its successors
   * 
   * @return Clone of current ASTNode with a parent which is equal to null
   */
  ASTNode deepClone();
  
  /**
   * Returns the start position of this ASTNode
   * 
   * @return start position of this ASTNode
   */
  SourcePosition get_SourcePositionEnd();
  
  /**
   * Sets the end position of this ASTNode
   * 
   * @param end end position of this ASTNode
   */
  void set_SourcePositionEnd(SourcePosition end);
  
  /**
   * Returns the end source position of this ASTNode
   * 
   * @return end position of this ASTNode
   */
  SourcePosition get_SourcePositionStart();
  
  /**
   * Sets the start position of this ASTNode
   * 
   * @param start start position of this ASTNode
   */
  void set_SourcePositionStart(SourcePosition start);
  
  /**
   * Returns list of all comments which are associated with this ASTNode and are
   * prior to the ASTNode in the input file
   * 
   * @return list of comments
   */
  List<Comment> get_PreComments();
  
  /**
   * Sets list of all comments which are associated with this ASTNode and are
   * prior to the ASTNode in the input file
   * 
   * @param _precomments list of comments
   */
  void set_PreComments(List<Comment> precomments);
  
  /**
   * Returns list of all comments which are associated with this ASTNode and can
   * be found after the ASTNode in the input file
   * 
   * @return list of comments
   */
  List<Comment> get_PostComments();
  
  /**
   * Sets list of all comments which are associated with this ASTNode and can be
   * found after the ASTNode in the input file
   * 
   * @param _postcomments list of comments
   */
  void set_PostComments(List<Comment> postcomments);
 
  /**
   * @returns a collection of all child nodes of this node
   */
  Collection<ASTNode> get_Children();
  
  /**
   * This method removes the reference from this node to a child node, no matter
   * in which attribute it is stored.
   * 
   * @param child the target node of the reference to be removed
   */
  void remove_Child(ASTNode child);
  
  /**
   * Sets the enclosing scope of this ast node.
   *
   * @param enclosingScope the enclosing scope of this ast node
   */
  void setEnclosingScope(Scope enclosingScope);
  
  /**
   * @return the enclosing scope of this ast node
   */
  Optional<? extends Scope> getEnclosingScope();
  
  /**
   * @return true if the enclosing scope is present
   */
  boolean enclosingScopeIsPresent();
  
  /**
   * Sets the corresponding symbol of this ast node.
   *
   * @param symbol the corresponding symbol of this ast node..
   */
  void setSymbol(Symbol symbol);
  
  /**
   * @return the corresponding symbol of this ast node.
   */
  Optional<? extends Symbol> getSymbol();
  
  /**
   * @return true if the symbol is present
   */
  boolean symbolIsPresent();

  /**
   * Sets the spanned scope of this ast node.
   *
   * @param spannedScope the spanned scope of this ast node
   */
  default void setSpannedScope(Scope spannedScope) {}

  /**
   * @return the spanned scope of this ast node.
   */
  default Optional<? extends Scope> getSpannedScope() {
    return Optional.empty();
  }
  
  /**
   * 
   *  @return true if the spanned scope is present
   */
  boolean spannedScopeIsPresent();
  
}
