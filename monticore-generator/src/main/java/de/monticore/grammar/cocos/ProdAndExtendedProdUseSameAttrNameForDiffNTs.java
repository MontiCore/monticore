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
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._ast.ASTRuleReference;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that an attribute name is not used twice for different nonterminals.
 *
 * @author KH
 */
public class ProdAndExtendedProdUseSameAttrNameForDiffNTs implements GrammarASTNonTerminalCoCo {
  
  public static final String ERROR_CODE = "0xA4024";
  
  public static final String ERROR_MSG_FORMAT = " The production %s extending the production %s must not use the\n"
      +
      "name %s for the nonterminal %s as %s already uses this name for the nonterminal %s.";
  
  @Override
  public void check(ASTNonTerminal a) {
    if (a.getUsageName().isPresent()) {
      String attributename = a.getUsageName().get();
      Optional<MCProdComponentSymbol> componentSymbol = a.getEnclosingScope().get()
          .resolve(attributename, MCProdComponentSymbol.KIND);
      if (componentSymbol.isPresent()) {
        System.err.println(" componentSymbol " + componentSymbol.get().getName());
        Optional<MCProdSymbol> rule = EssentialMCGrammarSymbolTableHelper.getEnclosingRule(a);
        if (rule.isPresent() && rule.get().getAstNode().get() instanceof ASTClassProd) {
          ASTClassProd prod = (ASTClassProd) rule.get().getAstNode().get();
          if (!prod.getSuperRule().isEmpty()) {
            ASTRuleReference type = prod.getSuperRule().get(0);
            String typename = type.getTypeName();
            Optional<MCProdSymbol> ruleSymbol = type.getEnclosingScope().get().getEnclosingScope()
                .get().resolve(typename, MCProdSymbol.KIND);
            if (ruleSymbol.isPresent()) {
              Optional<MCProdComponentSymbol> rcs = ruleSymbol.get().getSpannedScope()
                  .resolve(attributename, MCProdComponentSymbol.KIND);
              if (rcs.isPresent() && !rcs.get().getReferencedProd().get().getName()
                  .equals(componentSymbol.get().getReferencedProd().get().getName())) {
                Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
                    prod.getName(),
                    ruleSymbol.get().getName(),
                    attributename,
                    componentSymbol.get().getReferencedProd().get().getName(),
                    ruleSymbol.get().getName(),
                    rcs.get().getReferencedProd().get().getName()),
                    a.get_SourcePositionStart());
              }
            }
          }
        }
      }
    }
  }
}
