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

package de.monticore.utils;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ForwardingObject;

import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.SourcePosition;

/**
 * A ASTNode which forwards all its method calls to another ASTNode. Subclasses should override one
 * or more methods to modify the behavior of the backing ASTNode as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 * 
 * @author Sebastian Oberhoff
 */
public abstract class ForwardingASTNode<T extends ASTNode> extends ForwardingObject implements
    ASTNode {
  
  @Override
  protected abstract T delegate();
  
  @Override
  public ASTNode deepClone() {
    return delegate().deepClone();
  }
  
  @Override
  public SourcePosition get_SourcePositionEnd() {
    return delegate().get_SourcePositionEnd();
  }
  
  @Override
  public void set_SourcePositionEnd(SourcePosition end) {
    delegate().set_SourcePositionEnd(end);
  }
  
  @Override
  public SourcePosition get_SourcePositionStart() {
    return delegate().get_SourcePositionStart();
  }
  
  @Override
  public void set_SourcePositionStart(SourcePosition start) {
    delegate().set_SourcePositionStart(start);
  }
    
  @Override
  public List<Comment> get_PreComments() {
    return delegate().get_PreComments();
  }
  
  @Override
  public void set_PreComments(List<Comment> _precomments) {
    delegate().set_PreComments(_precomments);
  }
  
  @Override
  public List<Comment> get_PostComments() {
    return delegate().get_PostComments();
  }
  
  @Override
  public void set_PostComments(List<Comment> _postcomments) {
    delegate().set_PostComments(_postcomments);
  }
  
  @Override
  public boolean equalAttributes(Object o) {
    return delegate().equalAttributes(o);
  }
  
  @Override
  public boolean equalsWithComments(Object o) {
    return delegate().equalsWithComments(o);
  }
  
  @Override
  public boolean deepEquals(Object o) {
    return delegate().deepEquals(o);
  }
  
  @Override
  public boolean deepEqualsWithComments(Object o) {
    return delegate().deepEqualsWithComments(o);
  }
  
  @Override
  public boolean deepEquals(Object o, boolean forceSameOrder) {
    return delegate().deepEquals(o, forceSameOrder);
  }
  
  @Override
  public boolean deepEqualsWithComments(Object o, boolean forceSameOrder) {
    return delegate().deepEqualsWithComments(o, forceSameOrder);
  }
  
  @Override
  public Collection<ASTNode> get_Children() {
    return delegate().get_Children();
  }
  
  @Override
  public void remove_Child(ASTNode child) {
    delegate().remove_Child(child);
  }

  @Override
  public void setEnclosingScope(Scope enclosingScope) {
    delegate().setEnclosingScope(enclosingScope);
  }

  @Override
  public Optional<? extends Scope> getEnclosingScope() {
    return delegate().getEnclosingScope();
  }

  @Override
  public void setSymbol(Symbol symbol) {
    delegate().setSymbol(symbol);
  }

  @Override
  public Optional<? extends Symbol> getSymbol() {
    return delegate().getSymbol();
  }
}
