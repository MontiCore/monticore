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

import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._cocos.GrammarASTClassProdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals do not extend more than one nonterminals/class.
 *
 * @author KH
 */
public class NTOnlyExtendsOneNTOrClass implements GrammarASTClassProdCoCo {

  public static final String ERROR_CODE = "0xA4011";

  public static final String ERROR_MSG_FORMAT = " The nonterminal %s must not %s more than one %s.";

  @Override
  public void check(ASTClassProd a) {
    if (a.getSuperRuleList().size()>1) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(), "extend", "nonterminal"),
              a.get_SourcePositionStart());
    }
    if(a.getASTSuperClassList().size()>1){
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(), "astextend", "class"),
              a.get_SourcePositionStart());
    }
  }

}
