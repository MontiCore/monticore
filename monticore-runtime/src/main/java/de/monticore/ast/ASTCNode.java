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

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

import de.monticore.prettyprint.AstPrettyPrinter;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.SourcePosition;
import de.se_rwth.commons.logging.Log;


/**
 * Foundation class of all AST-classes Shouldn't be used in an implementation,
 * all AST-classes also share the interface ASTNode
 * 
 * @author krahn, volkova
 */
public abstract class ASTCNode implements ASTNode, Cloneable {
  
  protected Optional<SourcePosition> start = Optional.empty();
  
  protected Optional<SourcePosition> end = Optional.empty();
  
  protected List<Comment> precomments = Lists.newArrayList();
  
  protected List<Comment> postcomments = Lists.newArrayList();
  
  protected Optional<? extends Symbol> symbol = Optional.empty();
  
  protected Optional<? extends Scope> enclosingScope = Optional.empty();
  
  protected Optional<AstPrettyPrinter<ASTNode>> prettyPrinter = Optional.empty();
  
  public abstract ASTNode deepClone();
  
  public ASTNode deepClone(ASTNode result) {
    Log.errorIfNull(result, "0xA4040 Parameter 'result' must not be null.");
    
    result.set_SourcePositionStart(new de.se_rwth.commons.SourcePosition(
        get_SourcePositionStart().getLine(), get_SourcePositionStart()
            .getColumn()));
    result.set_SourcePositionEnd(new de.se_rwth.commons.SourcePosition(
        get_SourcePositionEnd().getLine(), get_SourcePositionEnd()
            .getColumn()));
    for (de.monticore.ast.Comment x : get_PreComments()) {
      result.get_PreComments().add(new de.monticore.ast.Comment(x.getText()));
    }
    for (de.monticore.ast.Comment x : get_PostComments()) {
      result.get_PostComments().add(new de.monticore.ast.Comment(x.getText()));
    }
    
    return result;
  }
  
  public SourcePosition get_SourcePositionEnd() {
    if (end.isPresent()) {
      return end.get();
    }
    return SourcePosition.getDefaultSourcePosition();
  }
  
  public void set_SourcePositionEnd(SourcePosition end) {
    this.end = Optional.ofNullable(end);
  }
  
  public SourcePosition get_SourcePositionStart() {
    if (start.isPresent()) {
      return start.get();
    }
    return SourcePosition.getDefaultSourcePosition();
  }
  
  public void set_SourcePositionStart(SourcePosition start) {
    this.start = Optional.ofNullable(start);
  }
  
  public List<Comment> get_PreComments() {
    return precomments;
  }
  
  public void set_PreComments(List<Comment> precomments) {
    this.precomments = precomments;
  }
  
  public List<Comment> get_PostComments() {
    return postcomments;
  }
  
  public void set_PostComments(List<Comment> postcomments) {
    this.postcomments = postcomments;
  }
  
  public boolean equalAttributes(Object o) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4041 Method equalAttributes is not implemented properly in class: " + o.getClass().getName());
  }
  
  public boolean equalsWithComments(Object o) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4042 Method equalsWithComments is not implemented properly in class: " + o.getClass().getName());
  }
  
  public boolean deepEquals(Object o) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4043 Method deepEquals is not implemented properly in class: " + o.getClass().getName());
  }
  
  public boolean deepEqualsWithComments(Object o) {
    throw new CompareNotSupportedException(
        "0xA4044 Method deepEqualsWithComments is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  public boolean deepEquals(Object o, boolean forceSameOrder) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4045 Method deepEquals is not implemented properly in class: " + o.getClass().getName());
  }
  
  public boolean deepEqualsWithComments(Object o, boolean forceSameOrder) {
    if (o == null) {
      return false;
    }
    throw new CompareNotSupportedException(
        "0xA4046 Method deepEqualsWithComments is not implemented properly in class: "
            + o.getClass().getName());
  }
  
  public void setEnclosingScope(Scope enclosingScope) {
    this.enclosingScope = Optional.ofNullable(enclosingScope);
  }
  
  public Optional<? extends Scope> getEnclosingScope() {
    return enclosingScope;
  }
  
  public void setSymbol(Symbol symbol) {
    this.symbol = Optional.ofNullable(symbol);
  }
  
  public Optional<? extends Symbol> getSymbol() {
    return symbol;
  }
  
  /**
   * @return prettyPrinter
   */
  public Optional<AstPrettyPrinter<ASTNode>> getPrettyPrinter() {
    return this.prettyPrinter;
  }

  /**
   * @param prettyPrinter the prettyPrinter to set
   */
  public void setPrettyPrinter(AstPrettyPrinter<ASTNode> prettyPrinter) {
    this.prettyPrinter = Optional.ofNullable(prettyPrinter);
  }
  
  public  Optional<String> prettyPrint() { 
    if (prettyPrinter.isPresent()) {
      return Optional.ofNullable(prettyPrinter.get().prettyPrint(this));
    }
    return Optional.empty();
  }
}
