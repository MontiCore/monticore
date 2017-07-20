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

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._cocos.GrammarASTConstantGroupCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that alternatives of keywords are named.
 *
 * @author KH
 */
public class KeywordAlternativeName implements GrammarASTConstantGroupCoCo {
  
  public static final String ERROR_CODE = "0xA4019";
  
  public static final String ERROR_MSG_FORMAT = " The production %s must not use an alternative of keywords without naming it.";
  
  @Override
  public void check(ASTConstantGroup a) {
    if (!a.getUsageName().isPresent()&& a.getConstants().size() >1) {
          String rulename = MCGrammarSymbolTableHelper.getEnclosingRule(a).get().getName();
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, rulename),
                  a.get_SourcePositionStart());

    }
  }


}
