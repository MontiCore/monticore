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

package de.monticore.symboltable.mocks.asts;

import java.util.Collection;
import java.util.List;

import de.monticore.ast.ASTNode;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import de.monticore.ast.ASTCNode;

/**
 * TODO: Write me!
 *
 * @author  Pedram Mir Seyed Nazari
 *          $Date$
 *
 */
public abstract class ASTNodeMock extends ASTCNode {

  private List<ASTNode> children = Lists.newArrayList();
  
  public void addChild(ASTNode child) {
    Preconditions.checkArgument(child != this);
    
    children.add(child);
  }
  

  /**
   * @see de.monticore.ast.ASTNode#get_Children()
   */
  @Override
  public Collection<ASTNode> get_Children() {
    return ImmutableList.copyOf(children);
  }

  /**
   * @see de.monticore.ast.ASTNode#remove_Child(de.monticore.ast.ASTNode)
   */
  @Override
  public void remove_Child(ASTNode child) {
    // TODO Auto-generated method stub
    
  }

  /**
   * @see de.monticore.ast.ASTCNode#deepClone()
   */
  @Override
  public ASTNodeMock deepClone() {
    // TODO Auto-generated method stub
    return null;
  }
  
}
