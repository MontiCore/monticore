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

package mc.embedding.host._symboltable;

import java.util.Deque;

import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolverConfiguration;
import mc.embedding.host._ast.ASTHost;
import mc.embedding.host._visitor.HostVisitor;

public class HostSymbolTableCreator extends HostSymbolTableCreatorTOP {

  private HostVisitor realThis = this;

  public HostSymbolTableCreator(ResolverConfiguration resolverConfig,
      MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }

  public HostSymbolTableCreator (ResolverConfiguration resolverConfiguration,
      Deque<MutableScope> scopeStack) {
    super(resolverConfiguration, scopeStack);
  }

  @Override public void visit(ASTHost node) {
    final HostSymbol hostSymbol = new HostSymbol(node.getName());

    putInScopeAndLinkWithAst(hostSymbol, node);
  }

  @Override public void endVisit(ASTHost node) {
    removeCurrentScope();
  }

  @Override public void setRealThis(HostVisitor realThis) {
    this.realThis = realThis;
  }

  @Override public HostVisitor getRealThis() {
    return realThis;
  }
}
