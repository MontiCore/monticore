/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that a grammar using keyword rules defines the token Name
 */
public class KeyRuleWithoutName implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA0142";
  
  public static final String ERROR_MSG_FORMAT = "Using the keyword rules a grammar must define the token Name.";
  
  @Override
  public void check(ASTMCGrammar gr) {
    MCGrammarSymbol grSymbol = gr.getSymbol();
    if (!gr.isComponent() && !grSymbol.getProdWithInherited("Name").isPresent()) {
      if (!grSymbol.getKeywordRulesWithInherited().isEmpty()) {
        Log.error(ERROR_CODE + ERROR_MSG_FORMAT, gr.get_SourcePositionStart());
      }
    }
  }

}
