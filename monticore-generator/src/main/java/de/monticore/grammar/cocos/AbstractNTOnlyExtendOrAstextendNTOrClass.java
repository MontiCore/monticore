/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTAbstractProd;
import de.monticore.grammar.grammar._cocos.GrammarASTAbstractProdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that abstract nonterminals do not extend and astextend a type.
 *
 * @author KH
 */
public class AbstractNTOnlyExtendOrAstextendNTOrClass implements GrammarASTAbstractProdCoCo {

  public static final String ERROR_CODE = "0xA4030";

  public static final String ERROR_MSG_FORMAT = " The abstract nonterminal %s must not extend and astextend a type.";

  @Override
  public void check(ASTAbstractProd a) {
    if (!a.getSuperRuleList().isEmpty() && !a.getASTSuperClassList().isEmpty()) {
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
              a.get_SourcePositionStart());
    }
  }

}
