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

import java.util.Optional;

import de.monticore.ast.ASTNode;
import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminal names start lower-case.
 *
 * @author KH
 */
public class NTAndASTRuleExtendType implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4013";
  
  public static final String ERROR_MSG_FORMAT = " The AST rule for %s must not extend the type " +
      "%s because the production already extends a type.";
  
  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol().get();
    for (ASTASTRule rule : a.getASTRuleList()) {
      if (!rule.getASTSuperClassList().isEmpty()) {
        Optional<MCProdSymbol> ruleSymbol = grammarSymbol.getProdWithInherited(rule.getType());
        if (ruleSymbol.isPresent()) {
          if (ruleSymbol.get().isClass()) {
            Optional<ASTNode> prod = ruleSymbol.get().getAstNode();
            if (prod.isPresent() && prod.get() instanceof ASTClassProd
                && (!((ASTClassProd) prod.get()).getASTSuperClassList().isEmpty()
                    || !((ASTClassProd) prod.get()).getSuperRuleList().isEmpty())) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, rule.getType(),
                  rule.getASTSuperClassList().get(0).getTypeName()),
                  rule.get_SourcePositionStart());
            }
          }
          else if (ruleSymbol.get().getAstNode().isPresent()
              && ruleSymbol.get().getAstNode().get() instanceof ASTAbstractProd) {
            ASTAbstractProd prod = (ASTAbstractProd) ruleSymbol.get().getAstNode().get();
            if (!prod.getASTSuperClassList().isEmpty() || !prod.getSuperRuleList().isEmpty()) {
              Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, rule.getType(),
                  rule.getASTSuperClassList().get(0).getTypeName()),
                  rule.get_SourcePositionStart());
            }
          }
        }
      }
    }
  }
}
