/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that external nonterminals only occur in a component grammar.
 *
 * @author KH
 */
public class ExternalNTOnlyInComponentGrammar implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA0276";
  
  public static final String ERROR_MSG_FORMAT = " The external nonterminal %s must not be used in a grammar not marked " +
          "as a grammar component.";
  
  @Override
  public void check(ASTMCGrammar a) {
    if (!a.isComponent()) {
      for (ASTProd p : a.getExternalProdList()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, p.getName()),
                a.get_SourcePositionStart());
      }
    }
  }

}

