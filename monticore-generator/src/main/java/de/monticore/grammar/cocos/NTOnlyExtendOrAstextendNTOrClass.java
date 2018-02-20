/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._cocos.GrammarASTClassProdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals do not extend and astextend a type.
 *
 * @author KH
 */
public class NTOnlyExtendOrAstextendNTOrClass implements GrammarASTClassProdCoCo {

  public static final String ERROR_CODE = "0xA4029";

  public static final String ERROR_MSG_FORMAT = " The nonterminal %s must not extend and astextend a type.";

  @Override
  public void check(ASTClassProd a) {
    if (!a.getSuperRuleList().isEmpty() && !a.getASTSuperClassList().isEmpty()) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
              a.get_SourcePositionStart());
    }
  }

}
