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

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals or only overridden by normal nonterminals.
 *
 * @author KH
 */
public class NTUniqueIgnoreCase implements GrammarASTMCGrammarCoCo {
  
public static final String ERROR_CODE = "0xA2026";
  
  public static final String ERROR_MSG_FORMAT = " The nonterminal %s must not be defined by more than one production: nonterminals aren't case-sensitive.";
  
  @Override
  public void check(ASTMCGrammar a) {
    List<String> prodnames = new ArrayList<>();
    List<String> prodnamesIgnoreCase = new ArrayList<>();
    List<ASTProd> prods = new ArrayList<>();
    prods.addAll(a.getAbstractProds());
    prods.addAll(a.getClassProds());
    prods.addAll(a.getEnumProds());
    prods.addAll(a.getInterfaceProds());
    prods.addAll(a.getLexProds());
    prods.addAll(a.getExternalProds());

    for(ASTProd p: prods){
      if(!prodnames.contains(p.getName()) && prodnamesIgnoreCase.contains(p.getName().toLowerCase())){
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName()),
                a.get_SourcePositionStart());
      } else {
        prodnames.add(p.getName());
        prodnamesIgnoreCase.add(p.getName().toLowerCase());
      }
    }
  }

}
