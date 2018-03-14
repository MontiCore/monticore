/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTGrammarReference;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that nonterminal names start lower-case.
 *
 * @author KH
 */
public class GrammarInheritanceCycle implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA4023";

  public static final String ERROR_MSG_FORMAT = " The grammar %s introduces an inheritance cycle.";

  @Override
  public void check(ASTMCGrammar a) {
    for(ASTGrammarReference ref : a.getSupergrammarList()) {
      if (Names.getQualifiedName(ref.getNameList()).equals(
          Names.getQualifiedName(a.getPackageList()) +"."+ a.getName())) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, a.getName()),
            a.get_SourcePositionStart());
        return;
      }
    }
  }


}
