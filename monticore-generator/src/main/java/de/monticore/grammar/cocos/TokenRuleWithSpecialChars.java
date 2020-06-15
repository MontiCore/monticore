/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTTokenRule;
import de.monticore.grammar.grammar._cocos.GrammarASTTokenRuleCoCo;
import de.se_rwth.commons.logging.Log;


public class TokenRuleWithSpecialChars implements GrammarASTTokenRuleCoCo {

  public static final String ERROR_CODE = "0xA4062";

  public static final String ERROR_MSG_FORMAT =
          " The string '%s' may not contain any letters or digits and must be longer than 2.";

  @Override
  public void check(ASTTokenRule a) {
    if (a.getString().length()<2 || a.getString().matches(".*[a-zA-Z0-9].*")) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getString()), a.get_SourcePositionStart());
    }
  }

}
