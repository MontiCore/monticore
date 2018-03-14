/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.Collection;
import java.util.Optional;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdComponentSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.monticore.grammar.symboltable.MCProdSymbolReference;
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

  @Override
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
        for (MCProdSymbol interfaceSymbol : MCGrammarSymbolTableHelper
            .getAllSuperInterfaces(prodSymbol)) {
          compareComponents(prodSymbol, interfaceSymbol);
        }
      }
    }
  }
  
  private void compareComponents(MCProdSymbol prodSymbol, MCProdSymbol interfaceSymbol) {
    for (MCProdComponentSymbol interfaceComponent : interfaceSymbol.getProdComponents()) {
      Optional<MCProdComponentSymbol> prodComponent = prodSymbol.getProdComponent(interfaceComponent.getName());
      if (!prodComponent.isPresent()) {
        logError(prodSymbol, interfaceSymbol, interfaceComponent);
        continue;
      }
      Optional<MCProdSymbolReference> prodComponentRefOpt = prodComponent.get().getReferencedProd();
      Optional<MCProdSymbolReference> interfaceComponentRefOpt = interfaceComponent.getReferencedProd();

      if ((prodComponentRefOpt.isPresent() && !interfaceComponentRefOpt.isPresent())
        || (!prodComponentRefOpt.isPresent() && interfaceComponentRefOpt.isPresent())) {
        logError(prodSymbol, interfaceSymbol, interfaceComponent);
        continue;
      }

      if (!prodComponentRefOpt.get().getName().equals(interfaceComponentRefOpt.get().getName())) {
        logError(prodSymbol, interfaceSymbol, interfaceComponent);
      }
    }
  }

  private void logError(MCProdSymbol prodSymbol, MCProdSymbol interfaceSymbol, MCProdComponentSymbol interfaceComponent) {
    Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, prodSymbol.getName(),
          interfaceComponent.getName(), interfaceSymbol.getName()),
          prodSymbol.getSourcePosition());
  }
  
}
