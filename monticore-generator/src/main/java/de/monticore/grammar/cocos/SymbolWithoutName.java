/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.grammar.grammar._symboltable.MCProdComponentSymbol;
import de.monticore.grammar.grammar._symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;

/**
 * Checks that Prods have one symbol and one scope keyword at most
 *
 */
public class SymbolWithoutName implements GrammarASTProdCoCo {
  
  public static final String ERROR_CODE = "0xA4058";
  
  public static final String ERROR_MSG_FORMAT = " Ensure that the symbol %s contains a 'Name'.";
    
  @Override
  public void check(ASTProd a) {
    if (a.getSymbol() instanceof MCProdSymbol) {
      MCProdSymbol symbol = (MCProdSymbol) a.getSymbol();
      if (symbol.isSymbolDefinition()) {
        Optional<MCProdComponentSymbol> ref = symbol.getProdComponent("Name");
        if (!ref.isPresent()) {
          ref = symbol.getProdComponent("name");
        }

        if (!ref.isPresent() || ref.get().isList() || (ref.get().getReferencedProd().isPresent() && !"Name".equals(ref.get().getReferencedProd().get().getName()))) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, symbol.getName()),
              a.get_SourcePositionStart());
        }
      }
    }
  }
  
}
