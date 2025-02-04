/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._cocos.GrammarASTLexProdCoCo;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.lang3.StringUtils;

public class LexProdModeNameUpperCase implements GrammarASTLexProdCoCo {
  public static final String ERROR_CODE = "0xA4038";

  public static final String ERROR_MSG_FORMAT = " The lexical production %s must use Upper-case mode names.";

  @Override
  public void check(ASTLexProd node) {
    if (node.isPresentMode()) {
      String m = node.getMode();
      if(!StringUtils.isAllUpperCase(m)){
        Log.warn(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName()),
          node.get_SourcePositionStart());
      }
    }
  }
}


