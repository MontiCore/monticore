/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTSymbolRule;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks only at most 1 symbolrule exists per nonterminal
 */
public class NoMultipleSymbolRule implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4151";

  public static final String ERROR_MSG_FORMAT = " A symbolRule must not exist twice for a single Nonterminal. Violation by %s";

  @Override
  public void check(ASTMCGrammar g) {
    for (ASTSymbolRule rule : g.getSymbolRuleList()) {
      int count = 0;
      for (ASTSymbolRule r2 : g.getSymbolRuleList()) {
        if (rule.getType().equals(r2.getType())) {
          count++;
        }
      }
      if (count != 1) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, rule.getType()), rule.get_SourcePositionStart());
      }
    }
  }
}
