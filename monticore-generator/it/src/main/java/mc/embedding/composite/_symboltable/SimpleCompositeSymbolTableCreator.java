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

import java.util.Deque;

import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import mc.embedding.composite._visitor.CompositeVisitor;

public class SimpleCompositeSymbolTableCreator extends CommonSymbolTableCreator implements CompositeVisitor {


  private CompositeVisitor realThis = this;

  public SimpleCompositeSymbolTableCreator(final ResolvingConfiguration resolverConfig,
      final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
  }

  public SimpleCompositeSymbolTableCreator(final ResolvingConfiguration resolverConfig,
      final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);

  }

  @Override public void setRealThis(CompositeVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
    }
  }

  @Override public CompositeVisitor getRealThis() {
    return realThis;
  }


}
