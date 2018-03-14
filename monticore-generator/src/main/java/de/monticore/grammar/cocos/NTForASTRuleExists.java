/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.Map;

import de.monticore.grammar.grammar._ast.ASTASTRule;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.symboltable.MCGrammarSymbol;
import de.monticore.grammar.symboltable.MCProdSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminal names start lower-case.
 *
 * @author KH
 */
public class NTForASTRuleExists implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4021";
  
  public static final String ERROR_MSG_FORMAT = " There must not exist an AST rule for the nonterminal %s" +
          " because there exists no production defining %s";
  
  @Override
  public void check(ASTMCGrammar a) {
    MCGrammarSymbol grammarSymbol = (MCGrammarSymbol) a.getSymbol().get();
    boolean prodFound = false;
    for(ASTASTRule astrule : a.getASTRuleList()){
      if(!grammarSymbol.getProdWithInherited(astrule.getType()).isPresent()){
        for(Map.Entry<String, MCProdSymbol> entry : grammarSymbol.getProdsWithInherited().entrySet()){
          MCProdSymbol rs = (MCProdSymbol) entry.getValue();
            if (astrule.getType().equals(rs.getName())) {
              prodFound = true;
              break ;
          }
        }
        if (!prodFound) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, astrule.getType(), astrule.getType()),
              astrule.get_SourcePositionStart());
        }
      }
    }
  }
}
