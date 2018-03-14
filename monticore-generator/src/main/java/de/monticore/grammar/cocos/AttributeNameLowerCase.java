/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminal names start lower-case.
 *
 * @author KH
 */
public class AttributeNameLowerCase implements GrammarASTNonTerminalCoCo {
  
  public static final String ERROR_CODE = "0xA4005";
  
  public static final String ERROR_MSG_FORMAT = " The name %s used for the nonterminal %s referenced by the production %s" +
          " should start with a lower-case letter.";
  
  @Override
  public void check(ASTNonTerminal a) {
    if (a.isPresentUsageName()) {
      if (!Character.isLowerCase(a.getUsageName().charAt(0))) {
        String rulename   =  a.getEnclosingScope().get().getSpanningSymbol().get().getName();
        Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getUsageName(), a.getName(), rulename),
                a.get_SourcePositionStart());
      }

    }
  }
}
