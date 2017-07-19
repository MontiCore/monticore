/*
 * ******************************************************************************
 * MontiCore Language Workbench
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

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._ast.ASTSymbolDefinition;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that Prods have one symbol and one scope keyword at most
 *
 * @author MB
 */
public class DuplicatedSymbolDefinitionInProd implements GrammarASTProdCoCo {
  
  public static final String ERROR_CODE = "0xA4041";
  
  public static final String ERROR_MSG_FORMAT = " Symbol or scope is mentioned more than once in the declaration '%s'.";
    
  @Override
  public void check(ASTProd a) {
    boolean isScope = false;
    boolean isSymbol = false;
    for (ASTSymbolDefinition c : a.getSymbolDefinitions()) {
      if ((c.isGenScope() && isScope) || (c.isGenSymbol() && isSymbol)) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()), a.get_SourcePositionStart());
      }
      isScope |= c.isGenScope();
      isSymbol |= c.isGenSymbol();
    }
  }
  
}
