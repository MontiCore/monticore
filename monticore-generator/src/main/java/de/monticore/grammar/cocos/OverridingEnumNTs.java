/*
 * ******************************************************************************
 * MontiCore Language Workbench
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals only extends abstract or normal nonterminals.
 *
 * @author KH
 */
public class OverridingEnumNTs implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4027";

  public static final String ERROR_MSG_FORMAT = " The production for the enum nonterminal %s must not be overridden.";

  @Override
  public void check(ASTMCGrammar a) {
    List<ASTProd> prods = new ArrayList<>(a.getClassProds());
    prods.addAll(a.getExternalProds());
    prods.addAll(a.getLexProds());
    prods.addAll(a.getInterfaceProds());
    prods.addAll(a.getEnumProds());
    prods.addAll(a.getAbstractProds());
    
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol().get();
    
    for (ASTProd p : prods) {
      Optional<MCProdSymbol> typeSymbol = grammarSymbol.getInheritedProd(p.getName());
      if (typeSymbol.isPresent() && typeSymbol.get().isEnum()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName()));
      }
    }

  }

}
