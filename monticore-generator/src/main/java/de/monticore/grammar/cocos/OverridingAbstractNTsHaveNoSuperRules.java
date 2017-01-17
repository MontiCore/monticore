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

import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._cocos.GrammarASTAbstractProdCoCo;
import de.monticore.languages.grammar.MCGrammarSymbol;
import de.monticore.languages.grammar.MCRuleSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that overriding abstract nonterminals do not have super rules or classes.
 *
 * @author KH
 */
public class OverridingAbstractNTsHaveNoSuperRules implements GrammarASTAbstractProdCoCo {
  
  public static final String ERROR_CODE = "0xA4002";
  
  public static final String ERROR_MSG_FORMAT = " The abstract production %s overriding a production of " +
          "a super grammar must not extend the production %s.\n" +
          "Hint: Overriding productions can only implement interfaces.";
  
  @Override
  public void check(ASTAbstractProd a) {
    MCRuleSymbol ruleSymbol = (MCRuleSymbol) a.getSymbol().get();
    MCGrammarSymbol grammarSymbol = ruleSymbol.getGrammarSymbol();
    List<MCGrammarSymbol> grammarSymbols =  grammarSymbol.getSuperGrammars();

    if(!a.getSuperRule().isEmpty() || !a.getASTSuperClass().isEmpty()) {
      String extendedType;
      if (!a.getSuperRule().isEmpty()){
        extendedType = a.getSuperRule().get(0).getName();
      }
      else{
        extendedType = a.getASTSuperClass().get(0).getTypeName();
      }
      for (MCGrammarSymbol s : grammarSymbols) {
        if (s.getType(a.getName()) != null ) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(), extendedType ),
                  a.get_SourcePositionStart());
        }
      }
    }
  }

}
