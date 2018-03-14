/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTTerminalCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that used terminals are not empty strings.
 *
 * @author KH
 */
public class TerminalEmptyString implements GrammarASTTerminalCoCo {

  public static final String ERROR_CODE = "0xA4054";

  public static final String ERROR_MSG_FORMAT = " The empty string cannot be used as a keyword.";

  @Override
  public void check(ASTTerminal a) {
    if(a.getName().isEmpty()) {
      Log.error(ERROR_CODE + ERROR_MSG_FORMAT, a.get_SourcePositionStart());
    }
  }
}
