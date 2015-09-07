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

import de.monticore.grammar.grammar._ast.ASTExternalProd;

/**
 * This class represents a symbol for an external rule (resp. production). E.g., <code>external A;</code>.
 *
 * @author  Pedram Mir Seyed Nazari
 * @version $Revision$,
 *          $Date$
 */
public class MCExternalRuleSymbol extends MCRuleSymbol {
  
  protected MCExternalRuleSymbol(ASTExternalProd astNode, String name) {
    this(name);
    setAstNode(astNode);
  }
  
  protected MCExternalRuleSymbol(String name) {
    super(name);
  }

  @Override
  public void setDefinedType(MCTypeSymbol definedType) {
    setType(definedType);
  }

  @Override
  public MCTypeSymbol getDefinedType() {
    return getType();
  }
  
  @Override
  public KindSymbolRule getKindSymbolRule() {
    return KindSymbolRule.HOLERULE;
  }
  
}
