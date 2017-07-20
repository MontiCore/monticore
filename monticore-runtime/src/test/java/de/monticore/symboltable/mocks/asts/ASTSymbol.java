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

package de.monticore.symboltable.mocks.asts;

import java.util.Optional;
import com.google.common.base.Preconditions;
import de.monticore.ast.ASTNode;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public abstract class ASTSymbol extends ASTNodeMock {
  
  private String name;
  
  private boolean spansScope;
  private boolean definesNamespace;
  
  public ASTSymbol() {
  }

  /**
   * @param spansScope the spansScope to set
   */
  public void setSpansScope(boolean spansScope) {
    this.spansScope = spansScope;
  }
  
  public boolean spansScope() {
    return spansScope;
  }
  
  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * @return name
   */
  public String getName() {
    return this.name;
  }
  
  /**
   * @param definesNamespace the definesNamespace to set
   */
  public void setDefinesNamespace(boolean definesNamespace) {
    Preconditions.checkArgument(!definesNamespace || spansScope, "Cannot define a namespace without spanning a scope");
    
    this.definesNamespace = definesNamespace;
  }
  
  public boolean definesNamespace() {
    return definesNamespace;
  }
  
  // TODO GV -> PN
  /*
  public Optional<ASTSymbol> getFirstParentWithScope() {
    ASTNode parent = get_Parent();
    
    while(parent != null) {
      if (parent instanceof ASTSymbol) {
        ASTSymbol symbolParent = (ASTSymbol) parent;
        if (symbolParent.spansScope()) {
          return Optional.of(symbolParent);
        }
      }
    }
    
    return Optional.empty();
  }*/
  
}
