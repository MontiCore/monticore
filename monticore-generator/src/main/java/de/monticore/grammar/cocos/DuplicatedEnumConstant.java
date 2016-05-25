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

import java.util.ArrayList;
import java.util.List;

import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._cocos.GrammarASTEnumProdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminal names start lower-case.
 *
 * @author KH
 */
public class DuplicatedEnumConstant implements GrammarASTEnumProdCoCo {

  public static final String ERROR_CODE = "0xA4014";

  public static final String ERROR_MSG_FORMAT = " Duplicate enum constant: %s.";
  public static final String HINT =   "\nHint: The constants of enumerations must be unique within an enumeration.";

  @Override
  public void check(ASTEnumProd a) {
    List<String> constants = new ArrayList<>();
    for(ASTConstant c: a.getConstants()) {
      if(!constants.contains(c.getName())){
        constants.add(c.getName());
      } else {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, c.getName()) + HINT,
                c.get_SourcePositionStart());
      }
    }
  }
}
