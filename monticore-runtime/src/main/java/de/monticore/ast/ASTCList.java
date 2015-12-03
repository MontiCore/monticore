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

/**
 * 
 */
package de.monticore.ast;

import java.util.Collection;

/**
 * super class for all lists, in addition to ASTCNode it allows for the
 * distinction between the state "empty" and "null"
 * 
 * @author groen
 * 
 * @deprecated Remove this class after MC-Release 4.2.1. It is no longer necessary.
 */
@Deprecated
public abstract class ASTCList extends ASTCNode implements ASTList, ASTNode {
  
  protected boolean _existent;
  
  public boolean is_Existent() {
    return _existent;
  }
  
  public void set_Existent(boolean existent) {
    this._existent = existent;
  }
  
  protected boolean _strictlyOrdered = true;
  
  /**
   * @return <true> iff the order of elements in a list shopuld be taken into
   *         consideration when this list is compared to another list (e.g. in
   *         the <tt>deepEquals</tt> and
   *         <tt>deepEqualsWithCommments<tt> methods)
   */
  public boolean isStrictlyOrdered() {
    return _strictlyOrdered;
  }
  
  @Override
  public Collection<ASTNode> get_Children() {
     return (Collection<ASTNode>) this;
  }
}
