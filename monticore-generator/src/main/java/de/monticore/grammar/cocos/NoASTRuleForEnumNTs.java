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

import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that no ast rules exist for enum nonterminals.
 *
 * @author KH
 */
public class NoASTRuleForEnumNTs implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4032";
  
  public static final String ERROR_MSG_FORMAT = " There must not exist an AST rule for the enum nonterminal %s.";
  
  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol().get();
    for (ASTASTRule rule : a.getASTRules()) {
      Optional<MCProdSymbol> ruleSymbol = grammarSymbol.getProdWithInherited(rule.getType());
      if (ruleSymbol.isPresent() && ruleSymbol.get().isEnum()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, rule.getType()),
            rule.get_SourcePositionStart());
      }
    }
    
  }
}
