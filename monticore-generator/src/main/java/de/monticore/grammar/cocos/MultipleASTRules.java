/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.ArrayList;
import java.util.List;

import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminal names start lower-case.
 *
 * @author KH
 */
public class MultipleASTRules implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4020";
  
  public static final String ERROR_MSG_FORMAT = " There must not exist more than one AST" +
          " rule for the nonterminal %s.";
  
  @Override
  public void check(ASTMCGrammar a) {
    List<String> nts = new ArrayList<>();
    for(ASTASTRule rule : a.getASTRuleList()){
      if (!nts.contains(rule.getType())) {
        nts.add(rule.getType());
      } else {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, rule.getType()),
                rule.get_SourcePositionStart());
      }
    }

  }
}
