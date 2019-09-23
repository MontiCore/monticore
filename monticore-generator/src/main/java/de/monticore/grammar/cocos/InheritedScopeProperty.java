// (c) https://github.com/MontiCore/monticore

/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

import java.util.Set;

/**
 * Checks that prods do not inherit their symbols from more than one class
 */
public class InheritedScopeProperty implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA0135";

  public static final String ERROR_MSG_FORMAT = " The rule %s inherits scope property from more than super class.";

  @Override
  public void check(ASTProd a) {
    ProdSymbol s = a.getProdSymbol();
    Set<ProdSymbol> superProds = MCGrammarSymbolTableHelper.getAllSuperProds(s);
    boolean found = s.isScopeSpanning();
    for (ProdSymbol prod : superProds) {
      if (found && prod.isScopeSpanning()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()), a.get_SourcePositionStart());
      } else if (prod.isScopeSpanning()) {
        found = true;
      }
    }
  }

}
