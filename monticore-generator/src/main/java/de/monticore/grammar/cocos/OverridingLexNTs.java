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

import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCLexRuleSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.List;

/**
 * Checks that nonterminals or only overridden by normal nonterminals.
 *
 * @author KH
 */
public class OverridingLexNTs implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4026";

  public static final String ERROR_MSG_FORMAT = " The lexical production %s must not use a different type to "
      + "store the token than the overridden production.";

  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol().get();
    List<MCGrammarSymbol> grammarSymbols =  grammarSymbol.getSuperGrammars();

    for(MCGrammarSymbol s: grammarSymbols) {
      for (ASTLexProd p : a.getLexProds()) {
          doCheck((MCLexRuleSymbol) s.getRuleWithInherited(p.getName()), p);
      }
    }
  }

  private void doCheck(MCLexRuleSymbol ruleSymbol, ASTLexProd lexProd) {
    if (ruleSymbol != null && !ruleSymbol.getRuleNode().getType().equals(lexProd.getType())) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, lexProd.getName()));
    }
  }

}
