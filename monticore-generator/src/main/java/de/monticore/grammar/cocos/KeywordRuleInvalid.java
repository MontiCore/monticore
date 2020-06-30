/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTKeywordRule;
import de.monticore.grammar.grammar._cocos.GrammarASTKeywordRuleCoCo;
import de.se_rwth.commons.logging.Log;

public class KeywordRuleInvalid implements GrammarASTKeywordRuleCoCo {

  public static final String ERROR_CODE = "0xA4064";

  public static final String ERROR_MSG_FORMAT =
          " The string '%s' must be compatible to 'Name'";

  @Override
  public void check(ASTKeywordRule a) {
    for (String s :a.getStringList()) {
      if (!s.matches("[a-zA-Z_$][a-zA-Z0-9_$]*")) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, s), a.get_SourcePositionStart());
      }
    }
  }

}
