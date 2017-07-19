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

import java.util.Optional;
import com.google.common.collect.Lists;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.se_rwth.commons.SourcePosition;

import java.util.Collection;
import java.util.List;

/**
 * Mock for ASTNode. DOES NOT IMPLEMENT ANY OF THE METHODS.
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class ASTNodeMock implements ASTNode {
  
  public static final ASTNode INSTANCE = new ASTNodeMock();

  /**
   * @see de.monticore.ast.ASTNode#deepClone()
   */
  @Override
  public ASTNode deepClone() {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#get_SourcePositionEnd()
   */
  @Override
  public SourcePosition get_SourcePositionEnd() {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#set_SourcePositionEnd(de.se_rwth.commons.SourcePosition)
   */
  @Override
  public void set_SourcePositionEnd(SourcePosition end) {
  }

  /**
   * @see de.monticore.ast.ASTNode#get_SourcePositionStart()
   */
  @Override
  public SourcePosition get_SourcePositionStart() {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#set_SourcePositionStart(de.se_rwth.commons.SourcePosition)
   */
  @Override
  public void set_SourcePositionStart(SourcePosition start) {
  }

  /**
   * @see de.monticore.ast.ASTNode#get_PreComments()
   */
  @Override
  public List<Comment> get_PreComments() {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#set_PreComments(java.util.List)
   */
  @Override
  public void set_PreComments(List<Comment> _precomments) {
  }

  /**
   * @see de.monticore.ast.ASTNode#get_PostComments()
   */
  @Override
  public List<Comment> get_PostComments() {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#set_PostComments(java.util.List)
   */
  @Override
  public void set_PostComments(List<Comment> _postcomments) {
  }

  /**
   * @see de.monticore.ast.ASTNode#equalAttributes(java.lang.Object)
   */
  @Override
  public boolean equalAttributes(Object o) {
    return false;
  }

  /**
   * @see de.monticore.ast.ASTNode#equalsWithComments(java.lang.Object)
   */
  @Override
  public boolean equalsWithComments(Object o) {
    return false;
  }

  /**
   * @see de.monticore.ast.ASTNode#deepEquals(java.lang.Object)
   */
  @Override
  public boolean deepEquals(Object o) {
    return false;
  }

  /**
   * @see de.monticore.ast.ASTNode#deepEqualsWithComments(java.lang.Object)
   */
  @Override
  public boolean deepEqualsWithComments(Object o) {
    return false;
  }

  /**
   * @see de.monticore.ast.ASTNode#deepEquals(java.lang.Object, boolean)
   */
  @Override
  public boolean deepEquals(Object o, boolean forceSameOrder) {
    return false;
  }

  /**
   * @see de.monticore.ast.ASTNode#deepEqualsWithComments(java.lang.Object, boolean)
   */
  @Override
  public boolean deepEqualsWithComments(Object o, boolean forceSameOrder) {
    return false;
  }

  /**
   * @see de.monticore.ast.ASTNode#get_Children()
   */
  @Override
  public Collection<ASTNode> get_Children() {
    return Lists.newArrayList();
  }

  /**
   * @see de.monticore.ast.ASTNode#remove_Child(de.monticore.ast.ASTNode)
   */
  @Override
  public void remove_Child(ASTNode child) {
    
  }

  @Override
  public void setEnclosingScope(Scope enclosingScope) {

  }

  @Override
  public Optional<? extends Scope> getEnclosingScope() {
    return null;
  }

  @Override
  public void setSymbol(Symbol symbol) {

  }

  @Override
  public Optional<? extends Symbol> getSymbol() {
    return null;
  }

  /**
   * @see de.monticore.ast.ASTNode#enclosingScopeIsPresent()
   */
  @Override
  public boolean enclosingScopeIsPresent() {
    return false;
  }

  /**
   * @see de.monticore.ast.ASTNode#symbolIsPresent()
   */
  @Override
  public boolean symbolIsPresent() {
    return false;
  }

  /**
   * @see de.monticore.ast.ASTNode#spannedScopeIsPresent()
   */
  @Override
  public boolean spannedScopeIsPresent() {
    return false;
  }
}

