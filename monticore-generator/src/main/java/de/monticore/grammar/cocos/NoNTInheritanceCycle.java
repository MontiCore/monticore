/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.codegen.mc2cd.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals do not have inheritance cycles.
 *

 */
public class NoNTInheritanceCycle implements GrammarASTProdCoCo {
  
  public static final String ERROR_CODE = "0xA4022";
  
  public static final String ERROR_MSG_FORMAT = " The production %s introduces an inheritance"
      + " cycle. Inheritance may not be cyclic.";
  
  @Override
  public void check(ASTProd a) {
    if (a.getSymbol() instanceof ProdSymbol) {
      ProdSymbol symbol = (ProdSymbol) a.getSymbol();
      for (ProdSymbol sr : MCGrammarSymbolTableHelper.getAllSuperProds(symbol)) {
        if (sr.getFullName().equals(symbol.getFullName())) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, symbol.getFullName()),
              a.get_SourcePositionStart());
        }
      }
    }
  }
}
