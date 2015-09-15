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

import de.monticore.grammar.grammar._ast.ASTGrammarReference;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminal names start lower-case.
 *
 * @author KH
 */
public class GrammarInheritanceCycle implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4023";

  public static final String ERROR_MSG_FORMAT = " The grammar %s introduces an inheritance cycle.";

  @Override
  public void check(ASTMCGrammar a) {
    for(ASTGrammarReference ref : a.getSupergrammar()) {
      if (Names.getQualifiedName(ref.getNames()).equals(
          Names.getQualifiedName(a.getPackage()) +"."+ a.getName())) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
            a.get_SourcePositionStart());
        return;
      }
    }
  }


}
