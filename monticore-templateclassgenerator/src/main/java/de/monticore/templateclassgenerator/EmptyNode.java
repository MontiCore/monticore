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
package de.monticore.templateclassgenerator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.SourcePosition;

/**
 * Without ASTNode it is not possible to generate in MontiCore. As there exists
 * no ASTNode for templates, we fool MontiCore with this class.
 *
 * @author Jerome Pfeiffer
 */
public class EmptyNode implements ASTNode {
  
  /**
   * Constructor for
   * de.montiarc.generator.typesafety.TemplateClassesGenerator.MyNode
   */
  public EmptyNode() {
    super();
  }
  
  /**
   * @see de.monticore.ast.ASTNode#deepClone()
   */
  @Override
  public ASTNode deepClone() {
    // TODO Auto-generated method stub
    return null;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#get_SourcePositionEnd()
   */
  @Override
  public SourcePosition get_SourcePositionEnd() {
    // TODO Auto-generated method stub
    return null;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#set_SourcePositionEnd(de.se_rwth.commons.SourcePosition)
   */
  @Override
  public void set_SourcePositionEnd(SourcePosition end) {
    // TODO Auto-generated method stub
    
  }
  
  /**
   * @see de.monticore.ast.ASTNode#get_SourcePositionStart()
   */
  @Override
  public SourcePosition get_SourcePositionStart() {
    // TODO Auto-generated method stub
    return null;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#set_SourcePositionStart(de.se_rwth.commons.SourcePosition)
   */
  @Override
  public void set_SourcePositionStart(SourcePosition start) {
    // TODO Auto-generated method stub
    
  }
  
  /**
   * @see de.monticore.ast.ASTNode#get_PreComments()
   */
  @Override
  public List<Comment> get_PreComments() {
    // TODO Auto-generated method stub
    return null;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#set_PreComments(java.util.List)
   */
  @Override
  public void set_PreComments(List<Comment> precomments) {
    // TODO Auto-generated method stub
    
  }
  
  /**
   * @see de.monticore.ast.ASTNode#get_PostComments()
   */
  @Override
  public List<Comment> get_PostComments() {
    // TODO Auto-generated method stub
    return null;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#set_PostComments(java.util.List)
   */
  @Override
  public void set_PostComments(List<Comment> postcomments) {
    // TODO Auto-generated method stub
    
  }
  
  /**
   * @see de.monticore.ast.ASTNode#equalAttributes(java.lang.Object)
   */
  @Override
  public boolean equalAttributes(Object o) {
    // TODO Auto-generated method stub
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#equalsWithComments(java.lang.Object)
   */
  @Override
  public boolean equalsWithComments(Object o) {
    // TODO Auto-generated method stub
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#deepEquals(java.lang.Object)
   */
  @Override
  public boolean deepEquals(Object o) {
    // TODO Auto-generated method stub
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#deepEqualsWithComments(java.lang.Object)
   */
  @Override
  public boolean deepEqualsWithComments(Object o) {
    // TODO Auto-generated method stub
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#deepEquals(java.lang.Object, boolean)
   */
  @Override
  public boolean deepEquals(Object o, boolean forceSameOrder) {
    // TODO Auto-generated method stub
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#deepEqualsWithComments(java.lang.Object,
   * boolean)
   */
  @Override
  public boolean deepEqualsWithComments(Object o, boolean forceSameOrder) {
    // TODO Auto-generated method stub
    return false;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#get_Children()
   */
  @Override
  public Collection<ASTNode> get_Children() {
    // TODO Auto-generated method stub
    return null;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#remove_Child(de.monticore.ast.ASTNode)
   */
  @Override
  public void remove_Child(ASTNode child) {
    // TODO Auto-generated method stub
    
  }
  
  /**
   * @see de.monticore.ast.ASTNode#setEnclosingScope(de.monticore.symboltable.Scope)
   */
  @Override
  public void setEnclosingScope(Scope enclosingScope) {
    // TODO Auto-generated method stub
    
  }
  
  /**
   * @see de.monticore.ast.ASTNode#getEnclosingScope()
   */
  @Override
  public Optional<? extends Scope> getEnclosingScope() {
    // TODO Auto-generated method stub
    return null;
  }
  
  /**
   * @see de.monticore.ast.ASTNode#setSymbol(de.monticore.symboltable.Symbol)
   */
  @Override
  public void setSymbol(Symbol symbol) {
    // TODO Auto-generated method stub
    
  }
  
  /**
   * @see de.monticore.ast.ASTNode#getSymbol()
   */
  @Override
  public Optional<? extends Symbol> getSymbol() {
    // TODO Auto-generated method stub
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
