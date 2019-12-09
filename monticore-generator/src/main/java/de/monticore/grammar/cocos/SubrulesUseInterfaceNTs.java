/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbolLoader;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Checks that the productions, which implement an interface, use the
 * non-terminals of that interface.
 * 
 */
public class SubrulesUseInterfaceNTs implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4047";
  
  public static final String ERROR_MSG_FORMAT = " The production %s must use the terminal %s from interface %s.";

  @Override
  public void check(ASTMCGrammar a) {
    if (!a.isPresentSymbol()) {
      Log.error(
              "0xA5001 The CoCo 'SubrulesUseInterfaceNTs' can't be checked: There is no grammar symbol for the grammar "
                      + a.getName(),
              a.get_SourcePositionStart());
    }

    MCGrammarSymbol symbol = a.getSymbol();
     for (ProdSymbol prodSymbol : symbol.getProds()) {
      if (!prodSymbol.isIsInterface()) {
        for (ProdSymbol interfaceSymbol : MCGrammarSymbolTableHelper
            .getAllSuperInterfaces(prodSymbol)) {
          compareComponents(prodSymbol, interfaceSymbol);
        }
      }
    }
  }
  
  private void compareComponents(ProdSymbol prodSymbol, ProdSymbol interfaceSymbol) {
    for (RuleComponentSymbol interfaceComponent : interfaceSymbol.getProdComponents()) {
      List<RuleComponentSymbol> prodComponents = prodSymbol.getSpannedScope().resolveRuleComponentDownMany(interfaceComponent.getName());
      if (prodComponents.isEmpty()) {
        logError(prodSymbol, interfaceSymbol, interfaceComponent);
        continue;
      }
      boolean found = false;
      Iterator<RuleComponentSymbol> it = prodComponents.iterator();
      while (!found && it.hasNext()) {
        RuleComponentSymbol prodComponent = it.next();
        if ((prodComponent.isIsList() == interfaceComponent.isIsList())
                && (prodComponent.isIsOptional() == interfaceComponent.isIsOptional())) {
          if (prodComponent.isIsTerminal() && interfaceComponent.isIsTerminal()) {
            found = true;
          } else if (prodComponent.isIsNonterminal() && interfaceComponent.isIsNonterminal()) {
            Optional<ProdSymbolLoader> prodComponentRefOpt = prodComponent.getReferencedProd();
            Optional<ProdSymbolLoader> interfaceComponentRefOpt = interfaceComponent.getReferencedProd();
            if (prodComponentRefOpt.isPresent() && interfaceComponentRefOpt.isPresent()) {
              found = prodComponentRefOpt.get().getName().equals(interfaceComponentRefOpt.get().getName());
            }
          }
        }
      }
      if (!found ){
        logError(prodSymbol, interfaceSymbol, interfaceComponent);
      }
    }
  }

  private void logError(ProdSymbol prodSymbol, ProdSymbol interfaceSymbol, RuleComponentSymbol interfaceComponent) {
    String suffix = interfaceComponent.isIsList() ? "*" : interfaceComponent.isIsOptional() ? "?" : "";
    Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, prodSymbol.getName(),
          interfaceComponent.getName() + suffix, interfaceSymbol.getName()),
          prodSymbol.getSourcePosition());
  }
  
}
