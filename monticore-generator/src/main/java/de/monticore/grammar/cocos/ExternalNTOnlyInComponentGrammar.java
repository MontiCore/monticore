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

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that external nonterminals only occur in a component grammar.
 *
 * @author KH
 */
public class ExternalNTOnlyInComponentGrammar implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA0276";
  
  public static final String ERROR_MSG_FORMAT = " The external nonterminal %s must not be used in a grammar not marked " +
          "as a grammar component.";
  
  @Override
  public void check(ASTMCGrammar a) {
    if (!a.isComponent()) {
      for (ASTProd p : a.getExternalProdList()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName()),
                a.get_SourcePositionStart());
      }
    }
  }

}

