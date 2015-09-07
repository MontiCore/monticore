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

import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.se_rwth.commons.SourcePosition;

public class MCLexRuleSymbol extends MCRuleSymbol {
  
  private ASTLexProd ruleNode;
  
  protected MCLexRuleSymbol(ASTLexProd ruleNode) {
    super(ruleNode.getName());
    this.ruleNode = ruleNode;
  } 
  
  /**
   * Create a MCLexProdEntry for an implicit rule
   * 
   */
  protected MCLexRuleSymbol(String name) {
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
    return KindSymbolRule.LEXERRULE;
  }
  
  @Override
  public SourcePosition getSourcePosition() {
    if (ruleNode != null) {
      return ruleNode.get_SourcePositionStart();
    }
    return super.getSourcePosition();
  }

  public ASTLexProd getRuleNode() {
    return ruleNode;
  }
  
  public boolean isFragment() {
    return (ruleNode == null) || ruleNode.isFragment();
  }
  
}
