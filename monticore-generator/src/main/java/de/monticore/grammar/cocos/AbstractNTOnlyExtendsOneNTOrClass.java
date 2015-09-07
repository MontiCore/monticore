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

import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._cocos.GrammarASTAbstractProdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that abstract nonterminals do not extend more than one nonterminals/class.
 *
 * @author KH
 */
public class AbstractNTOnlyExtendsOneNTOrClass implements GrammarASTAbstractProdCoCo {

  public static final String ERROR_CODE = "0xA4012";

  public static final String ERROR_MSG_FORMAT = " The abstract nonterminal %s must not %s more than one %s.";

  @Override
  public void check(ASTAbstractProd a) {
    if (a.getSuperRule().size()>1) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(), "extend", "nonterminal"),
              a.get_SourcePositionStart());
    }
    if(a.getASTSuperClass().size()>1){
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(), "astextend", "class"),
              a.get_SourcePositionStart());
    }
  }

}
