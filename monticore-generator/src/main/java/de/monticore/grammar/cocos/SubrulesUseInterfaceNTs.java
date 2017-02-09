/*
 * Copyright (c) 2017 RWTH Aachen. All rights reserved.
 *
 * http://www.se-rwth.de/
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
