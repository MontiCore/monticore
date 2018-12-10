/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTProd;
import de.monticore.grammar.grammar._cocos.GrammarASTProdCoCo;
import de.se_rwth.commons.logging.Log;

import static de.se_rwth.commons.logging.Log.error;
import static java.lang.Character.isLowerCase;
import static java.lang.String.format;

public class ProdStartsWithCapital implements GrammarASTProdCoCo {

  public static final String ERROR_CODE = "0xA4031";

  public static final String ERROR_MSG_FORMAT = " The nonterminal %s should not start with a lower-case letter.";

  @Override
  public void check(ASTProd node) {
    if (isLowerCase(node.getName().charAt(0))) {
      error(format(ERROR_CODE + ERROR_MSG_FORMAT, node.getName()), node.get_SourcePositionStart());
    }
  }
}
