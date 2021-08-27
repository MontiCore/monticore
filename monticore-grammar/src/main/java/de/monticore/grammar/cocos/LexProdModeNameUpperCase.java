/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._cocos.GrammarASTLexProdCoCo;
import de.se_rwth.commons.logging.Log;

public class LexProdModeNameUpperCase implements GrammarASTLexProdCoCo {
  public static final String ERROR_CODE = "0xA40136";//              <-

  public static final String ERROR_MSG_FORMAT = " The lexical production %s must use Upper-case mode names.";

  @Override
  public void check(ASTLexProd node) {
    for (String m : node.getModeList()) {
      char[] charArray = m.toCharArray();
      for (char c : charArray) {
        if (!Character.isUpperCase(c)) {
          Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName()),
              node.get_SourcePositionStart());
        }
      }
    }
  }
}


