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

import de.monticore.ast.ASTNode;

/**
 * Symbol table for a interface rule which may be explicitly written in the grammar
 * or be implicit by interfaces in rules
 * 
 * @author volkova
 */
public class MCInterfaceOrAbstractRuleSymbol extends MCRuleSymbol {

  private final boolean isInterface;

  protected MCInterfaceOrAbstractRuleSymbol(ASTNode astNode, String name, boolean isInterface) {
    super(name);
    this.isInterface = isInterface;
    setAstNode(astNode);
  } 
  
  @Override
  public MCTypeSymbol getDefinedType() {
    return getType();
  }
  
  @Override
  public void setDefinedType(MCTypeSymbol type) {
    setType(type);
  }

  public boolean isInterface() {
    return isInterface;
  }

  @Override
  public KindSymbolRule getKindSymbolRule() {
    return KindSymbolRule.INTERFACEORABSTRACTRULE;
  }


  
}
