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

import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.grammar.symboltable.MCProdSymbolReference;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals do not have inheritance cycles.
 *
 * @author KH
 */
public class NoNTInheritanceCycle implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA4022";

  public static final String ERROR_MSG_FORMAT = " The production %s introduces an inheritance"
      + " cycle. Inheritance may not be cyclic.";

  @Override
  public void check(ASTProd a) {
    if (a.getSymbol().get() instanceof MCProdSymbol){
      List<MCProdSymbolReference> superProds = ((MCProdSymbol) a.getSymbol().get()).getSuperProds();
      for(MCProdSymbolReference sr : superProds){
        if(sr.getReferencedSymbol().getFullName().equals(a.getSymbol().get().getFullName())){
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getSymbol().get().getFullName()),
              a.get_SourcePositionStart());
        }
      }
    }
  }
}
