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

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminal names start lower-case.
 *
 * @author KH
 */
public class AttributeNameLowerCase implements GrammarASTNonTerminalCoCo {
  
  public static final String ERROR_CODE = "0xA4005";
  
  public static final String ERROR_MSG_FORMAT = " The name %s used for the nonterminal %s referenced by the production %s" +
          " should start with a lower-case letter.";
  
  @Override
  public void check(ASTNonTerminal a) {
    if (a.getUsageName().isPresent()) {
      if (!Character.isLowerCase(a.getUsageName().get().charAt(0))) {
        String rulename   =  a.getEnclosingScope().get().getSpanningSymbol().get().getName();
        Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getUsageName().get(), a.getName(), rulename),
                a.get_SourcePositionStart());
      }

    }
  }
}
