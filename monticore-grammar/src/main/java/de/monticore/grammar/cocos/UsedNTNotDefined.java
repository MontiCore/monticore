/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.Optional;

import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that used nonterminals are defined.
 *

 */
public class UsedNTNotDefined implements GrammarASTNonTerminalCoCo {

  public static final String ERROR_CODE = "0xA2031";

  public static final String ERROR_MSG_FORMAT = " The production %s must not use the nonterminal " +
          "%s because there exists no production defining %s.";

  @Override
  public void check(ASTNonTerminal a) {
    Optional<MCGrammarSymbol> grammarSymbol = MCGrammarSymbolTableHelper
        .getMCGrammarSymbol(a.getEnclosingScope());
    Optional<ProdSymbol> ruleSymbol = MCGrammarSymbolTableHelper.getEnclosingRule(a);
    String ruleName = ruleSymbol.isPresent()? ruleSymbol.get().getName() : "";
    if (grammarSymbol.isPresent() && !grammarSymbol.get().getProdWithInherited(a.getName()).isPresent()) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, ruleName, a.getName(),
          a.getName()),
          a.get_SourcePositionStart());
    }
  }
}
