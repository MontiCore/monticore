/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import java.util.ArrayList;
import java.util.List;

import de.monticore.grammar.grammar._ast.ASTConstant;
import de.monticore.grammar.grammar._ast.ASTEnumProd;
import de.monticore.grammar.grammar._cocos.GrammarASTEnumProdCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminal names start lower-case.
 *
 * @author KH
 */
public class DuplicatedEnumConstant implements GrammarASTEnumProdCoCo {

  public static final String ERROR_CODE = "0xA4014";

  public static final String ERROR_MSG_FORMAT = " Duplicate enum constant: %s.";
  public static final String HINT =   "\nHint: The constants of enumerations must be unique within an enumeration.";

  @Override
  public void check(ASTEnumProd a) {
    List<String> constants = new ArrayList<>();
    for(ASTConstant c: a.getConstantList()) {
      if(!constants.contains(c.getName())){
        constants.add(c.getName());
      } else {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, c.getName()) + HINT,
                c.get_SourcePositionStart());
      }
    }
  }
}
