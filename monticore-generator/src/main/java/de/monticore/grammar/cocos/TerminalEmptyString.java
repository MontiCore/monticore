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

import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTTerminalCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that used terminals are not empty strings.
 *
 * @author KH
 */
public class TerminalEmptyString implements GrammarASTTerminalCoCo {

  public static final String ERROR_CODE = "0xA4054";

  public static final String ERROR_MSG_FORMAT = " The empty string cannot be used as a keyword.";

  @Override
  public void check(ASTTerminal a) {
    if(a.getName().isEmpty()) {
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, a.get_SourcePositionStart());
    }
  }
}
