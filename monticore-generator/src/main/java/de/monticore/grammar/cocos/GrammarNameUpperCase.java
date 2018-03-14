/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminal names start lower-case.
 *
 * @author KH
 */
public class GrammarNameUpperCase implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4033";
  
  public static final String ERROR_MSG_FORMAT = " The grammar's name %s should start with an upper-case letter.";
  
  @Override
  public void check(ASTMCGrammar a) {
      if (!Character.isUpperCase(a.getName().charAt(0))) {
        Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
                a.get_SourcePositionStart());
      }

  }
}
