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

package mc.embedding.external.embedded._symboltable;

import de.monticore.symboltable.ArtifactScope;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.embedding.external.embedded._ast.ASTText;
import mc.embedding.external.embedded._visitor.EmbeddedVisitor;

import java.util.ArrayList;
import java.util.Deque;

import static java.util.Optional.empty;

public class EmbeddedSymbolTableCreator extends EmbeddedSymbolTableCreatorTOP {
  private EmbeddedVisitor realThis = this;

  public EmbeddedSymbolTableCreator(ResolvingConfiguration resolverConfig, MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);

    final ArtifactScope artifactScope = new ArtifactScope(empty(), "", new ArrayList<>());

    putOnStack(artifactScope);
  }

  public EmbeddedSymbolTableCreator(ResolvingConfiguration resolverConfig,
      Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
  }

  @Override public void visit(ASTText node) {
    final TextSymbol textSymbol = new TextSymbol(node.getName());

    addToScopeAndLinkWithNode(textSymbol, node);
  }

  @Override public void setRealThis(EmbeddedVisitor realThis) {
    this.realThis = realThis;
  }

  @Override public EmbeddedVisitor getRealThis() {
    return realThis;
  }
}
