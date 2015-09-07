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

import de.monticore.grammar.HelperGrammar;
import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._cocos.GrammarASTConstantGroupCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that alternatives of keywords are named.
 *
 * @author KH
 */
public class KeywordInvalidName implements GrammarASTConstantGroupCoCo {

  public static final String ERROR_CODE = "0xA4018";

  public static final String ERROR_MSG_FORMAT = " The production %s must not use the keyword %s without naming it.";

  @Override
  public void check(ASTConstantGroup a) {
    if(!a.getUsageName().isPresent()){
      for(ASTConstant c : a.getConstants()) {
        if (!HelperGrammar.hasValidName(c)) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
                  c.getEnclosingScope().get().getSpanningSymbol().get().getName(), c.getName()),
              a.get_SourcePositionStart());
        }
      }
    }
  }


}
