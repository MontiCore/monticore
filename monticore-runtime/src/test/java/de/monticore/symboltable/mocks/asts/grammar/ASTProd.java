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

package de.monticore.symboltable.mocks.asts.grammar;

import com.google.common.collect.Lists;
import de.monticore.symboltable.mocks.asts.ASTNodeMock;
import de.monticore.ast.ASTNode;

import java.util.List;

/**
 * TODO: Write me!
 *
 * @author  (last commit) $Author$
 *          $Date$
 *
 */
public class ASTProd extends ASTNodeMock {
  
  private String name;
  
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
  
  public List<ASTProd> getSubProductions() {
    List<ASTProd> subProductions = Lists.newArrayList();
    
    for (ASTNode child : get_Children()) {
      if (child instanceof ASTProd) {
        subProductions.add((ASTProd) child);
      }
    }
    
    return subProductions;
  }
  
  /**
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() + " " + getName();
  }

}
