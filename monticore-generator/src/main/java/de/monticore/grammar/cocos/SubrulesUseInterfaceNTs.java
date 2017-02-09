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

import java.util.Collection;
import java.util.Optional;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that the productions, which implement an interface, use the
 * non-terminals of that interface.
 * 
 * @author BS
 */
public class SubrulesUseInterfaceNTs implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4047";
  
  public static final String ERROR_MSG_FORMAT = " The production %s must use the non-terminal %s from interface %s.";
  
  public void check(ASTMCGrammar a) {
    Optional<MCGrammarSymbol> symbol = MCGrammarSymbolTableHelper.getGrammarSymbol(a);
    if (!symbol.isPresent()) {
      Log.error(
          "0xA5001 The CoCo 'SubrulesUseInterfaceNTs' can't be checked: There is no grammar symbol for the grammar "
              + a.getName(),
          a.get_SourcePositionStart());
    }
    for (MCProdSymbol prodSymbol : symbol.get().getProds()) {
      if (!prodSymbol.isInterface()) {
        for (MCProdSymbol interfaceProd : MCGrammarSymbolTableHelper
            .getAllSuperInterfaces(prodSymbol)) {
          compareComponents(interfaceProd.getProdComponents(), prodSymbol, interfaceProd);
        }
      }
    }
  }
  
  private void compareComponents(Collection<MCProdComponentSymbol> interfaceComponents,
      MCProdSymbol prodSymbol, MCProdSymbol ruleSymbol) {
    for (MCProdComponentSymbol interfaceComponent : interfaceComponents) {
      if (interfaceComponent.getReferencedProd().isPresent()
          && prodSymbol.getProdComponent(interfaceComponent.getName()).isPresent()) {
        String prodName = interfaceComponent.getReferencedProd().get().getName();
        if (prodSymbol.getProdComponent(interfaceComponent.getName()).get().getReferencedProd()
            .isPresent()) {
          String usedName = prodSymbol.getProdComponent(interfaceComponent.getName()).get()
              .getReferencedProd().get().getName();
          if (usedName.equals(prodName)) {
            continue;
          }
        }
      }
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, prodSymbol.getName(),
          interfaceComponent.getName(), ruleSymbol.getName()),
          prodSymbol.getSourcePosition());
      
    }
  }
  
}
