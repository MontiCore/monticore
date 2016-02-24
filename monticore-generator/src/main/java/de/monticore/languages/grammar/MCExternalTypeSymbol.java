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

package de.monticore.languages.grammar;

/**
 * Represents an external type, i.e., a type that is not defined in the grammar, e.g.,
 * <code>java.lang.String</code>
 */
public class MCExternalTypeSymbol extends MCTypeSymbol {
  
  private boolean isASTNode = false;
  
  protected MCExternalTypeSymbol(String name, MCGrammarSymbol grammarSymbol) {
    super(name);
    setGrammarSymbol(grammarSymbol);
    setExternal(true);
  }

  @Override
  public KindType getKindOfType() {
    return KindType.EXTERN;
  }
  
  @Override
  public String getQualifiedName(String prefix, String suffix) {
    return getQualifiedName();
  }
  
  @Override
  public String getQualifiedName() {
   return getName();
  }
  
  @Override
  public boolean isASTNode() {
    return isASTNode;
  }
  
  public void setASTNode(boolean astNode) {
    this.isASTNode = astNode;
  }


  // ================================================================== //
  // ================ Generator-specific Information ================== //
  // ================================================================== //

  @Override
  public String getListType() {
    if (isASTNode()) {
      return getQualifiedName() + "List";
    }
    else {
      return "java.util.List<" + substitutePrimitive(getQualifiedName()) + ">";
    }
  }
  
  private String substitutePrimitive(String qualifiedName) {
    if (qualifiedName.equals("int")) {
      return "Integer";
    }
    if (qualifiedName.equals("float")) {
      return "Float";
    }
    if (qualifiedName.equals("double")) {
      return "Double";
    }
    return qualifiedName;
  }
  
}
