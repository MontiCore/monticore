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

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks that the productions, which implement an interface, use the
 * non-terminals of that interface.
 */
public class SubrulesUseInterfaceNTs implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4047";

  public static final String ERROR_MSG_FORMAT = " The production %s must use the Component %s from interface %s.";

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
          if (prodSymbol.getProdComponents().isEmpty() && !interfaceSymbol.getProdComponents().isEmpty()) {
            compareWithSuperProd(symbol, prodSymbol, interfaceSymbol);
          } else {
            compareComponents(prodSymbol, interfaceSymbol);
          }
        }
      }
    }
  }

  /**
   * used when the Prod which implements the interface is overwriting a prod from a super grammar
   * only when the overwriting prod is not specifying a right side (no RuleComponents)
   * e.g. grammar A { B = Name;}
   * grammar C { interface I = Name;
   * B implements I;}
   */
  private void compareWithSuperProd(MCGrammarSymbol grammarSymbol, ProdSymbol prodSymbol, ProdSymbol interfaceSymbol) {
    List<ProdSymbol> overwrittenProds = grammarSymbol.getAllSuperGrammars()
        .stream()
        .map(MCGrammarSymbol::getProds)
        .flatMap(Collection::stream)
        .filter(x -> x.getName().equals(prodSymbol.getName()))
        .collect(Collectors.toList());
    if (overwrittenProds.isEmpty()) {
      logError(prodSymbol, interfaceSymbol, interfaceSymbol.getProdComponents().stream().findFirst().get());
    }
    for (ProdSymbol overwrittenProd : overwrittenProds) {
      compareComponents(overwrittenProd, interfaceSymbol);
    }
  }

  private void compareComponents(ProdSymbol prodSymbol, ProdSymbol interfaceSymbol) {
    for (RuleComponentSymbol interfaceComponent : interfaceSymbol.getProdComponents()) {
      Optional<RuleComponentSymbol> prodComponentOpt = prodSymbol.getProdComponent(interfaceComponent.getName());
      if (!prodComponentOpt.isPresent()) {
        logError(prodSymbol, interfaceSymbol, interfaceComponent);
        continue;
      }
      RuleComponentSymbol prodComponent = prodComponentOpt.get();

      if (prodComponent.isIsList() != interfaceComponent.isIsList()
          || prodComponent.isIsOptional() != interfaceComponent.isIsOptional()) {
        logError(prodSymbol, interfaceSymbol, interfaceComponent);
        continue;
      }

      if (prodComponent.isIsTerminal() && interfaceComponent.isIsTerminal()) {
        if (!interfaceComponent.isPresentUsageName() || !prodComponent.isPresentUsageName()
            || interfaceComponent.getUsageName().equals(prodComponent.getUsageName())) {
          continue;
        }
      }

      Optional<ProdSymbolLoader> prodComponentRefOpt = prodComponent.getReferencedProd();
      Optional<ProdSymbolLoader> interfaceComponentRefOpt = interfaceComponent.getReferencedProd();

      if (prodComponentRefOpt.isPresent() != interfaceComponentRefOpt.isPresent()) {
        logError(prodSymbol, interfaceSymbol, interfaceComponent);
        continue;
      }

      if (!prodComponentRefOpt.isPresent() && !interfaceComponentRefOpt.isPresent()) {
        // Two termninals ==> no error?
        continue;
      }

      if (!prodComponentRefOpt.get().getName().equals(interfaceComponentRefOpt.get().getName())) {
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
