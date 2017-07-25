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

package mc.embedding.composite._symboltable;

import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;
import mc.embedding.composite._visitor.CompositeDelegatorVisitor;
import mc.embedding.composite._visitor.CompositeVisitor;
import mc.embedding.embedded._symboltable.EmbeddedSymbolTableCreator;
import mc.embedding.host._ast.ASTHost;
import mc.embedding.host._symboltable.HostSymbolTableCreator;

import java.util.Deque;

public class CompositeSymbolTableCreator extends CommonSymbolTableCreator implements CompositeVisitor {

  private final HostSymbolTableCreator hostSymbolTableCreator;

  public final CompositeDelegatorVisitor visitor;

  private CompositeVisitor realThis = this;

  public CompositeSymbolTableCreator(final ResolvingConfiguration resolverConfig,
      final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);

    this.hostSymbolTableCreator = new HostSymbolTableCreator(resolverConfig, scopeStack);

    visitor = new CompositeDelegatorVisitor();
    visitor.setCompositeVisitor(new SimpleCompositeSymbolTableCreator(resolverConfig, scopeStack));
    visitor.setHostVisitor(this.hostSymbolTableCreator);
    visitor.setEmbeddedVisitor(
        new EmbeddedSymbolTableCreator(resolverConfig, scopeStack));
  }

  public CompositeSymbolTableCreator(final ResolvingConfiguration resolverConfig,
      final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);

    this.hostSymbolTableCreator = new HostSymbolTableCreator(resolverConfig, scopeStack);

    visitor = new CompositeDelegatorVisitor();
    visitor.setCompositeVisitor(new SimpleCompositeSymbolTableCreator(resolverConfig, scopeStack));
    visitor.setHostVisitor(this.hostSymbolTableCreator);
    visitor.setEmbeddedVisitor(
        new EmbeddedSymbolTableCreator(resolverConfig, scopeStack));
  }

  @Override public void setRealThis(CompositeVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }

  @Override public CompositeVisitor getRealThis() {
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
  public Scope createFromAST(ASTHost rootNode) {
    Log.errorIfNull(rootNode);
    rootNode.accept(visitor);
    return getFirstCreatedScope();
  }



}
