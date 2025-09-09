/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTTokenConstant;
import de.monticore.grammar.grammar._cocos.GrammarASTTokenConstantCoCo;
import de.se_rwth.commons.logging.Log;

public class TokenConstantInvalid implements GrammarASTTokenConstantCoCo {

  public static final String ERROR_CODE = "0xA4059";

  public static final String ERROR_MSG_FORMAT =
          " The string '%s' may not contain any letters or digits and must be longer than 2.";

  @Override
  public void check(ASTTokenConstant a) {
    if (a.getString().length()<2 || a.getString().matches(".*[a-zA-Z0-9].*")) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getString()), a.get_SourcePositionStart());
    }
  }
}
