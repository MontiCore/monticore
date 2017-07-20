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

package de.monticore.emf._ast;

import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.impl.EObjectImpl;

import com.google.common.collect.Lists;

import de.monticore.ast.ASTNode;
import de.monticore.ast.Comment;
import de.monticore.prettyprint.AstPrettyPrinter;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.ScopeSpanningSymbol;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.SourcePosition;

/**
 * Foundation class of all EMF compatible AST-classes.
 * 
 * @author krahn, volkova
 */
public abstract class ASTECNode extends EObjectImpl implements ASTENode {
  
  protected Optional<SourcePosition> start = Optional.empty();
  
  protected Optional<SourcePosition> end = Optional.empty();
  
  protected List<Comment> precomments = Lists.newArrayList();
  
  protected List<Comment> postcomments = Lists.newArrayList();
  
  protected Optional<? extends Symbol> symbol = Optional.empty();
  
  protected Optional<? extends Scope> enclosingScope = Optional.empty();
  
  protected Optional<? extends Scope> spannedScope = Optional.empty();
  
  protected Optional<AstPrettyPrinter<ASTNode>> prettyPrinter = Optional.empty();
  
  public abstract ASTNode deepClone();
  
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
  
  @Override
  public void setEnclosingScope(Scope enclosingScope) {
    this.enclosingScope = Optional.ofNullable(enclosingScope);
  }
  
  @Override
  public Optional<? extends Scope> getEnclosingScope() {
    return enclosingScope;
  }
  
  @Override
  public void setSymbol(Symbol symbol) {
    this.symbol = Optional.ofNullable(symbol);
  }
  
  @Override
  public Optional<? extends Symbol> getSymbol() {
    return symbol;
  }
  
  @Override
  public boolean spannedScopeIsPresent() {
    return spannedScope.isPresent();
  }
  
  @Override
  public boolean symbolIsPresent() {
    return symbol.isPresent();
  }
  
  @Override
  public boolean enclosingScopeIsPresent() {
    return enclosingScope.isPresent();
  }
  
  @Override
  public Optional<? extends Scope> getSpannedScope() {
    if (spannedScope.isPresent()) {
      return spannedScope;
    }
    
    Optional<? extends Scope> result = Optional.empty();
    if (getSymbol().isPresent() && (getSymbol().get() instanceof ScopeSpanningSymbol)) {
      final ScopeSpanningSymbol sym = (ScopeSpanningSymbol) getSymbol().get();
      result = Optional.of(sym.getSpannedScope());
    }
    
    return result;
  }
  
  @Override
  public void setSpannedScope(Scope spannedScope) {
    this.spannedScope = Optional.ofNullable(spannedScope);
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
  
  public Optional<String> prettyPrint() {
    if (prettyPrinter.isPresent()) {
      return Optional.ofNullable(prettyPrinter.get().prettyPrint(this));
    }
    return Optional.empty();
  }
}
