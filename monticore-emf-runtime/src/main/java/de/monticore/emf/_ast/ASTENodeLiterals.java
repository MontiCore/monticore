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

package de.monticore.emf._ast;

import org.eclipse.emf.common.util.Enumerator;

public enum ASTENodeLiterals implements Enumerator {
  // Literal Object DEFAULT
  DEFAULT(0);
  
  public static final int DEFAULT_VALUE = 0;
  
  protected int intValue;
  
  private ASTENodeLiterals(int intValue) {
    this.intValue = intValue;
  }
  
  public int intValue() {
    return intValue;
  }
  
  public String getName() {
    return toString();
  }
  
  public String getLiteral() {
    return toString();
  }
  
  public int getValue() {
    return intValue;
  }
  
}
