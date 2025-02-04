/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._cocos.GrammarASTConstantGroupCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that alternatives of keywords are named.
 *

 */
public class KeywordInvalidName implements GrammarASTConstantGroupCoCo {

  public static final String ERROR_CODE = "0xA4018";

  public static final String ERROR_MSG_FORMAT = " The production %s must not use the keyword %s without naming it.";

  @Override
  public void check(ASTConstantGroup a) {
    if (!a.isPresentUsageName()) {
      for (ASTConstant c : a.getConstantList()) {
        if (c.getHumanName().isEmpty()) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT,
              a.getEnclosingScope().getSpanningSymbol()
                  .getName(),
              c.getName()),
              a.get_SourcePositionStart());
        }
      }
    }
  }

}
