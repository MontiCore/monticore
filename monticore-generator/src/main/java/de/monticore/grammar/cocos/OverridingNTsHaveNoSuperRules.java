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

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._cocos.GrammarASTClassProdCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that overriding nonterminals do not have super rules or
 * classes.
 *
 * @author KH
 */
public class OverridingNTsHaveNoSuperRules implements GrammarASTClassProdCoCo {
  
  public static final String ERROR_CODE = "0xA4001";
  
  public static final String ERROR_MSG_FORMAT = " The production %s overriding a production of " +
      "a super grammar must not extend the production %s.\n" +
      "Hint: Overriding productions can only implement interfaces.";
  
  @Override
  public void check(ASTClassProd a) {
    Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(a);
    List<MCGrammarSymbol> grammarSymbols = grammarSymbol.get().getSuperGrammarSymbols();
    
    if (!a.getSuperRule().isEmpty() || !a.getASTSuperClass().isEmpty()) {
      String extendedType;
      if (!a.getSuperRule().isEmpty()) {
        extendedType = a.getSuperRule().get(0).getName();
      }
      else {
        extendedType = a.getASTSuperClass().get(0).getTypeName();
      }
      for (MCGrammarSymbol s : grammarSymbols) {
        if (s.getProd(a.getName()).isPresent()) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(), extendedType),
              a.get_SourcePositionStart());
        }
      }
    }
  }
  
}
