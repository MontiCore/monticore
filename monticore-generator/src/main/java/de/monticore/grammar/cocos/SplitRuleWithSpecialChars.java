/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTSplitRule;
import de.monticore.grammar.grammar._cocos.GrammarASTSplitRuleCoCo;
import de.se_rwth.commons.logging.Log;


public class SplitRuleWithSpecialChars implements GrammarASTSplitRuleCoCo {

  public static final String ERROR_CODE = "0xA4062";

  public static final String ERROR_MSG_FORMAT =
          " The string '%s' may not contain any letters or digits and must be longer than 2.";

  @Override
  public void check(ASTSplitRule a) {
    for (String s:a.getStringList()) {
      if (s.length() < 2 || s.matches(".*[a-zA-Z0-9].*")) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, s), a.get_SourcePositionStart());
      }
    }
  }

}
