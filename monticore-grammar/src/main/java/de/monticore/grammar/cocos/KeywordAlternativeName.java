/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.MCGrammarSymbolTableHelper;
import de.monticore.grammar.grammar._ast.ASTConstantGroup;
import de.monticore.grammar.grammar._cocos.GrammarASTConstantGroupCoCo;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that alternatives of keywords are named.
 *
 */
public class KeywordAlternativeName implements GrammarASTConstantGroupCoCo {
  
  public static final String ERROR_CODE = "0xA4019";
  
  public static final String ERROR_MSG_FORMAT = " The production %s must not use a ConstantGroup with more than one element without naming it.";
  
  @Override
  public void check(ASTConstantGroup a) {
    if (!a.isPresentUsageName()&& a.getConstantList().size() >1) {
          String rulename = MCGrammarSymbolTableHelper.getEnclosingRule(a).get().getName();
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, rulename),
                  a.get_SourcePositionStart());

    }
  }


}
