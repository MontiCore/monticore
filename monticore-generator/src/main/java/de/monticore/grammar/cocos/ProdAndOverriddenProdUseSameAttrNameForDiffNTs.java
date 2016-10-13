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

import de.monticore.codegen.mc2cd.EssentialMCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.grammar.symboltable.EssentialMCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
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
      Optional<MCProdComponentSymbol> componentSymbol = a.getEnclosingScope().get()
          .resolve(attributename, MCProdComponentSymbol.KIND);
      if (!componentSymbol.isPresent()) {
        Log.error("0xA1124 ASTNonterminal " + a.getName() + " couldn't be resolved.");
      }
      Optional<MCProdSymbol> rule = EssentialMCGrammarSymbolTableHelper.getEnclosingRule(a);
      if (!rule.isPresent()) {
        Log.error("0xA1125 Symbol for enclosing produktion of the component " + a.getName()
            + " couldn't be resolved.");
      }
      Optional<EssentialMCGrammarSymbol> grammarSymbol = EssentialMCGrammarSymbolTableHelper
          .getMCGrammarSymbol(a);
      if (!grammarSymbol.isPresent()) {
        Log.error(
            "0xA1126 grammar symbol for the component " + a.getName() + " couldn't be resolved.");
      }
      for (EssentialMCGrammarSymbol g : grammarSymbol.get().getSuperGrammarSymbols()) {
        Optional<MCProdSymbol> ruleSymbol = g.getProd(rule.get().getName());
        if (ruleSymbol.isPresent()) {
          Optional<MCProdComponentSymbol> rcs = ruleSymbol.get().getSpannedScope()
              .resolve(attributename, MCProdComponentSymbol.KIND);
          if (rcs.isPresent() && !ruleSymbol.get().getName()
              .equals(rule.get().getName())) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
                ruleSymbol.get().getName(),
                attributename,
                rule.get().getName(),
                ruleSymbol.get().getName()),
                a.get_SourcePositionStart());
          }
        }
      }
    }
  }
}
