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

import java.util.List;
import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleComponentSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that an attribute name is not used twice for different nonterminals.
 *
 * @author KH
 */
public class ProdAndOverriddenProdUseSameAttrNameForDiffNTs implements GrammarASTNonTerminalCoCo {

  public static final String ERROR_CODE = "0xA4025";

  public static final String ERROR_MSG_FORMAT = " The overriding production %s must not use " +
          "the name %s for the nonterminal %s as the overridden production uses this name for the nonterminal %s";

  @Override
  public void check(ASTNonTerminal a) {
    if (a.getUsageName().isPresent()) {
      String attributename = a.getUsageName().get();
      Optional<MCRuleComponentSymbol> componentSymbol = a.getEnclosingScope().get().resolve(attributename, MCRuleComponentSymbol.KIND);
      if (componentSymbol.isPresent()) {
        MCRuleSymbol rule = componentSymbol.get().getEnclosingRule();
        MCGrammarSymbol grammarSymbol = rule.getGrammarSymbol();
        List<MCGrammarSymbol> grammarSymbols = grammarSymbol.getSuperGrammars();
        for (MCGrammarSymbol g : grammarSymbols) {
          ASTClassProd prod = (ASTClassProd) rule.getAstNode().get();
          MCRuleSymbol ruleSymbol = g.getRule(prod.getName());
          if (ruleSymbol != null) {
            Optional<MCRuleComponentSymbol> rcs = ruleSymbol.getSpannedScope().resolve(attributename, MCRuleComponentSymbol.KIND);
            if (rcs.isPresent() && !rcs.get().getReferencedRuleName().equals(componentSymbol.get().getReferencedRuleName())) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
                              prod.getName(),
                              attributename,
                              componentSymbol.get().getReferencedRuleName(),
                              rcs.get().getReferencedRuleName()),
                      a.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }
}
