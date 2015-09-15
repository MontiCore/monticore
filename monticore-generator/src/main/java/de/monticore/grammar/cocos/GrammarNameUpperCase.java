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

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminal names start lower-case.
 *
 * @author KH
 */
public class GrammarNameUpperCase implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4033";
  
  public static final String ERROR_MSG_FORMAT = " The grammar's name %s should start with an upper-case letter.";
  
  @Override
  public void check(ASTMCGrammar a) {
      if (!Character.isUpperCase(a.getName().charAt(0))) {
        Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
                a.get_SourcePositionStart());
      }

  }
}
