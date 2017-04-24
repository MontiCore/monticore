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

import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that Prods have one symbol and one scope keyword at most
 *
 * @author MB
 */
public class SymbolWithoutName implements GrammarASTProdCoCo {
  
  public static final String ERROR_CODE = "0xA4057";
  
  public static final String ERROR_MSG_FORMAT = " The symbol %s must contain the component 'Name'.";
    
  @Override
  public void check(ASTProd a) {
    if (a.getSymbol().get() instanceof MCProdSymbol) {
      MCProdSymbol symbol = (MCProdSymbol) a.getSymbol().get();
      if (symbol.isSymbolDefinition()) {
        Optional<MCProdComponentSymbol> ref = symbol.getProdComponent("Name");
        if (!ref.isPresent()) {
          ref = symbol.getProdComponent("name");
        }

        if (!ref.isPresent() || ref.get().isList() || (ref.get().getReferencedProd().isPresent() && !ref.get().getReferencedProd().get().getName().equals("Name"))) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, symbol.getName()),
              a.get_SourcePositionStart());
        }
      }
    }
  }
  
}
