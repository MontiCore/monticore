/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.Optional;

import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that no ast rules exist for enum nonterminals.
 *
 * @author KH
 */
public class NoASTRuleForEnumNTs implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4032";
  
  public static final String ERROR_MSG_FORMAT = " There must not exist an AST rule for the enum nonterminal %s.";
  
  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol().get();
    for (ASTASTRule rule : a.getASTRuleList()) {
      Optional<MCProdSymbol> ruleSymbol = grammarSymbol.getProdWithInherited(rule.getType());
      if (ruleSymbol.isPresent() && ruleSymbol.get().isEnum()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, rule.getType()),
                rule.get_SourcePositionStart());
      }
    }

  }
}
