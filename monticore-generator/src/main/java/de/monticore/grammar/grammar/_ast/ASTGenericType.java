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

package de.monticore.grammar.grammar._ast;

/**
 * TODO: Write me!
 *
 * @author (last commit) $Author$
 * @version $Revision$, $Date$
 * @since TODO: add version number
 */
public class ASTGenericType extends ASTGenericTypeTOP {
  
  protected ASTGenericType() {
  }
  
  protected ASTGenericType(
      int dimension,
      java.util.List<String> names,
      java.util.List<de.monticore.grammar.grammar._ast.ASTGenericType> genericTypes)
  {
    setDimension(dimension);
    setNameList(names);
    setGenericTypeList(genericTypes);
  }
  
  public String toString() {
    return de.monticore.grammar.HelperGrammar.printGenericType(this);
  }
  
  public String getTypeName() {
    return de.monticore.grammar.HelperGrammar.printGenericType(this);
  }
  
  public boolean isExternal() {
    return true;
  };
  
}
