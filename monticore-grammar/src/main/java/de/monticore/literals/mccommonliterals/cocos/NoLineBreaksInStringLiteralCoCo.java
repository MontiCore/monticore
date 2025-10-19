/* (c) https://github.com/MontiCore/monticore */
package de.monticore.literals.mccommonliterals.cocos;

import de.monticore.literals.mccommonliterals._ast.ASTStringLiteral;
import de.monticore.literals.mccommonliterals._cocos.MCCommonLiteralsASTStringLiteralCoCo;
import de.se_rwth.commons.logging.Log;

public class NoLineBreaksInStringLiteralCoCo implements MCCommonLiteralsASTStringLiteralCoCo {

  public static final String ERROR_MSG = " The string \"%s\" may not contain line breaks.";
  public static final String ERROR_CODE = "0xA0220";

  @Override
  public void check(ASTStringLiteral node) {
    if (node.getValue().contains("\n") || node.getValue().contains("\r")) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG, node.getValue()), node.get_SourcePositionStart());
    }
  }

}
