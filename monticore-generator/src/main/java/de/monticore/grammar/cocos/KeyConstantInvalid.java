/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTKeyConstant;
import de.monticore.grammar.grammar._cocos.GrammarASTKeyConstantCoCo;
import de.se_rwth.commons.logging.Log;

public class KeyConstantInvalid implements GrammarASTKeyConstantCoCo {

  public static final String ERROR_CODE = "0xA4063";

  public static final String ERROR_MSG_FORMAT =
          " The string '%s' must be compatible to 'Name'";

  @Override
  public void check(ASTKeyConstant a) {
    for (String s :a.getStringList()) {
      if (!s.matches("[a-zA-Z_$][a-zA-Z0-9_$]*")) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, s), a.get_SourcePositionStart());
      }
    }
  }

}
