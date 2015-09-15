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
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that abstract nonterminals witheout extending productions only occur in a component grammar.
 *
 * @author KH
 */
public class AbstractNTWithoutExtensionOnlyInComponentGrammar implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA0277";

  public static final String ERROR_MSG_FORMAT = " The abstract nonterminal %s must not be used without nonterminals " +
          "extending it in a grammar not marked as a grammar component.";

  @Override
  public void check(ASTMCGrammar a) {
    if (!a.isComponent()) {
      for (ASTProd p : a.getAbstractProds()) {
        boolean extensionFound = false;
        for (ASTAbstractProd ep : a.getAbstractProds()) {
          for (ASTRuleReference r : ep.getSuperRule()) {
            if (p.getName().equals(r.getName())) {
              extensionFound = true;
              break;
            }
          }
          if (extensionFound) {
            break;
          }
        }
        if (!extensionFound) {
          for (ASTClassProd ep : a.getClassProds()) {
            for (ASTRuleReference r : ep.getSuperRule()) {
              if (p.getName().equals(r.getName())) {
                extensionFound = true;
                break;
              }
            }
            if (extensionFound) {
              break;
            }
          }
        }
        if (!extensionFound) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName()),
                  a.get_SourcePositionStart());
        }
      }
    }
  }

}
