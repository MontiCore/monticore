/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTClassProd;
import de.monticore.grammar.grammar._cocos.GrammarASTClassProdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminals do not extend more than one nonterminals/class.
 *
 * @author KH
 */
public class NTOnlyExtendsOneNTOrClass implements GrammarASTClassProdCoCo {

  public static final String ERROR_CODE = "0xA4011";

  public static final String ERROR_MSG_FORMAT = " The nonterminal %s must not %s more than one %s.";

  @Override
  public void check(ASTClassProd a) {
    if (a.getSuperRuleList().size()>1) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(), "extend", "nonterminal"),
              a.get_SourcePositionStart());
    }
    if(a.getASTSuperClassList().size()>1){
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName(), "astextend", "class"),
              a.get_SourcePositionStart());
    }
  }

}
