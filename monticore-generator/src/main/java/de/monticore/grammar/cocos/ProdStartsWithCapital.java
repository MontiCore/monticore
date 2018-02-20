/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Created by
 *
 * @author KH
 */
public class ProdStartsWithCapital implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA4031";

  public static final String ERROR_MSG_FORMAT = " The nonterminal %s should not start with a lower-case letter.";

  @Override
  public void check(ASTProd node) {
    if(Character.isLowerCase(node.getName().charAt(0))){
      Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName()), node.get_SourcePositionStart());
    }
  }
}
