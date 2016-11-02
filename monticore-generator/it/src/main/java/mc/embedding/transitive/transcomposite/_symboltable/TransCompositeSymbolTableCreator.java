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

package mc.embedding.transitive.transcomposite._symboltable;

import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import mc.embedding.composite._symboltable.CompositeSymbolTableCreator;
import mc.embedding.transitive.transcomposite._visitor.CommonTransCompositeDelegatorVisitor;
import mc.embedding.transitive.transcomposite._visitor.TransCompositeDelegatorVisitor;
import mc.embedding.transitive.transcomposite._visitor.TransCompositeVisitor;
import mc.embedding.transitive.transhost._ast.ASTTransStart;
import mc.embedding.transitive.transhost._symboltable.TransHostSymbolTableCreator;

import java.util.Deque;

public class TransCompositeSymbolTableCreator extends CommonSymbolTableCreator implements
    TransCompositeVisitor {
  
  private final TransHostSymbolTableCreator hostSymbolTableCreator;
  
  public final TransCompositeDelegatorVisitor visitor;
  
  private TransCompositeVisitor realThis = this;
  
  public TransCompositeSymbolTableCreator(final ResolvingConfiguration resolverConfig,
      final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
    
    this.hostSymbolTableCreator = new TransHostSymbolTableCreator(resolverConfig, scopeStack);
    
    visitor = new CommonTransCompositeDelegatorVisitor();
    
    visitor.set_mc_embedding_composite__visitor_CompositeVisitor(new CompositeSymbolTableCreator(
        resolverConfig, scopeStack));
    visitor.set_mc_embedding_transitive_transcomposite__visitor_TransCompositeVisitor(this);
    visitor
        .set_mc_embedding_transitive_transhost__visitor_TransHostVisitor(this.hostSymbolTableCreator);
  }
  
  public TransCompositeSymbolTableCreator(final ResolvingConfiguration resolverConfig,
      final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
    
    this.hostSymbolTableCreator = new TransHostSymbolTableCreator(resolverConfig, scopeStack);
    
    visitor = new CommonTransCompositeDelegatorVisitor();
    
    visitor.set_mc_embedding_composite__visitor_CompositeVisitor(new CompositeSymbolTableCreator(
        resolverConfig, scopeStack));
    visitor.set_mc_embedding_transitive_transcomposite__visitor_TransCompositeVisitor(this);
    visitor
        .set_mc_embedding_transitive_transhost__visitor_TransHostVisitor(this.hostSymbolTableCreator);
    
  }
  
  @Override
  public void setRealThis(TransCompositeVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }
  
  @Override
  public TransCompositeVisitor getRealThis() {
    return realThis;
  }
  
  @Override
  public MutableScope getFirstCreatedScope() {
    return hostSymbolTableCreator.getFirstCreatedScope();
  }
  
  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public Scope createFromAST(ASTTransStart rootNode) {
    Log.errorIfNull(rootNode);
    rootNode.accept(visitor);
    return getFirstCreatedScope();
  }
  
}
