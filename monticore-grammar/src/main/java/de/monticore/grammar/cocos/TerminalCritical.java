/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTTerminalCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that used terminals are not digits.
 *
 */
public class TerminalCritical implements GrammarASTTerminalCoCo {

  public static final String ERROR_CODE = "0xA4058";

  public static final String ERROR_MSG_FORMAT =
          " If the string %s is defined as terminal, this string can no longer be part of an expression";

  @Override
  public void check(ASTTerminal a) {
    if(a.getName().matches("[0-9]+")) {
      Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()), a.get_SourcePositionStart());
    }
  }

}
