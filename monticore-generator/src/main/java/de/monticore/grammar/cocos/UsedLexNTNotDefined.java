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

import de.monticore.grammar.grammar._ast.ASTLexNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTLexNonTerminalCoCo;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleComponentSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that used nonterminals are defined.
 *
 * @author KH
 */
public class UsedLexNTNotDefined implements GrammarASTLexNonTerminalCoCo {

  public static final String ERROR_CODE = "0xA4016";

  public static final String ERROR_MSG_FORMAT = " The lexical production %s must not" +
          " use the nonterminal %s because there exists no lexical production defining %s.";

  @Override
  public void check(ASTLexNonTerminal a) {
    Optional<MCRuleComponentSymbol> ruleComponentSymbol = (Optional<MCRuleComponentSymbol>) a.getSymbol();
    MCGrammarSymbol grammarSymbol = ruleComponentSymbol.get().getGrammarSymbol();
    MCRuleSymbol ruleSymbol= ruleComponentSymbol.get().getEnclosingRule();
    if(grammarSymbol.getRuleWithInherited(a.getName()) == null ){
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, ruleSymbol.getName(), a.getName(), a.getName()),
              a.get_SourcePositionStart());
    }
  }
}
