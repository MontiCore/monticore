/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Optional;
import java.util.Set;

/**
 * Checks that prods do not inherit their symbols from more than one class
 */
public class InheritedSymbolProperty implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA0125";

  public static final String ERROR_MSG_FORMAT = " The rule %s inherits symbols from more than one class.";

  @Override
  public void check(ASTProd a) {
    ProdSymbol s = a.getSymbol();
    Set<ProdSymbol> superProds = MCGrammarSymbolTableHelper.getAllSuperProds(s);
    Optional<ProdSymbol> found = Optional.empty();
    for (ProdSymbol prod : superProds) {
      if (found.isPresent() && prod.isIsSymbolDefinition()) {
        if (!MCGrammarSymbolTableHelper.getAllSuperProds(found.get()).contains(prod)) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()), a.get_SourcePositionStart());
        }
      } else if (prod.isIsSymbolDefinition()) {
        found = Optional.of(prod);
      }
    }
  }

}
