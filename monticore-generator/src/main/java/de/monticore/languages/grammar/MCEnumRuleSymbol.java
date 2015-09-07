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

import de.monticore.grammar.grammar._ast.ASTEnumProd;

/**
 * This class represents a symbol for an enum rule (resp. production). E.g., in
 * <code>enum E = "t" | "s"</code>, <code>E</code> is the enum rule.
 *
 * @author  Pedram Mir Seyed Nazari
 * @version $Revision$,
 *          $Date$
 *
 */
public class MCEnumRuleSymbol extends MCRuleSymbol {
  
  private ASTEnumProd rule;
  
  protected MCEnumRuleSymbol(ASTEnumProd rule) {
    this(rule.getName());
    this.rule = rule;
  } 
  
  protected MCEnumRuleSymbol(String name) {
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
    return KindSymbolRule.ENUMRULE;
  }
  
  public ASTEnumProd getRule() {
    return rule;
  }

}
