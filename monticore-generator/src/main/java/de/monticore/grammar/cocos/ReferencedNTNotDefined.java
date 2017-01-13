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
import de.monticore.grammar.grammar._ast.ASTInterfaceProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.EssentialMCGrammarSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals only extends abstract or normal nonterminals.
 *
 * @author KH
 */
public class ReferencedNTNotDefined implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA2030";
  
  public static final String ERROR_MSG_FORMAT = " The production %s must not reference the " +
      "%snonterminal %s because there exists no defining production for %s.";
  
  @Override
  public void check(ASTMCGrammar a) {
    EssentialMCGrammarSymbol grammarSymbol = (EssentialMCGrammarSymbol) a.getSymbol().get();
    for (ASTClassProd p : a.getClassProds()) {
      if (!p.getSuperRule().isEmpty() && p.getSymbol().isPresent()) {
        for (ASTRuleReference sr : p.getSuperRule()) {
          if (!grammarSymbol.getProdWithInherited(sr.getName()).isPresent()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), "", sr.getName(),
                sr.getName()),
                p.get_SourcePositionStart());
          }
        }
      }
      if (!p.getSuperInterfaceRule().isEmpty()) {
        for (ASTRuleReference sr : p.getSuperInterfaceRule()) {
          if (!grammarSymbol.getProdWithInherited(sr.getName()).isPresent()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), "interface ",
                sr.getName(), sr.getName()),
                p.get_SourcePositionStart());
          }
        }
      }
    }
    for (ASTAbstractProd p : a.getAbstractProds()) {
      if (!p.getSuperRule().isEmpty() && p.getSymbol().isPresent()) {
        for (ASTRuleReference sr : p.getSuperRule()) {
          if (!grammarSymbol.getProdWithInherited(sr.getName()).isPresent()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), "", sr.getName(),
                sr.getName()),
                p.get_SourcePositionStart());
          }
        }
      }
      if (!p.getSuperInterfaceRule().isEmpty() && p.getSymbol().isPresent()) {
        for (ASTRuleReference sr : p.getSuperInterfaceRule()) {
          if (!grammarSymbol.getProdWithInherited(sr.getName()).isPresent()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), "interface ",
                sr.getName(), sr.getName()),
                p.get_SourcePositionStart());
          }
        }
      }
    }
    for (ASTInterfaceProd p : a.getInterfaceProds()) {
      if (!p.getSuperInterfaceRule().isEmpty() && p.getSymbol().isPresent()) {
        for (ASTRuleReference sr : p.getSuperInterfaceRule()) {
          if (!grammarSymbol.getProdWithInherited(sr.getName()).isPresent()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName(), "interface ",
                sr.getName(), sr.getName()),
                p.get_SourcePositionStart());
          }
        }
      }
    }
  }
  
}
