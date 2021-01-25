/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTGrammarReference;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

/**
 * Checks that a grammar does not extend the same grammar multiple times
 */
public class GrammarExtensionOnce implements GrammarASTMCGrammarCoCo {
  
  public static final String ERROR_CODE = "0xA4150";
  
  public static final String ERROR_MSG_FORMAT = "A grammar must not extend another grammar multiple times.";
  
  @Override
  public void check(ASTMCGrammar gr) {
    for (int i = 0; i < gr.getSupergrammarList().size() - 1; i++) {
      for (int j = i + 1; j < gr.getSupergrammarList().size(); j++) {
        if (Names.getQualifiedName(gr.getSupergrammar(i).getNameList()).equals(
            Names.getQualifiedName(gr.getSupergrammar(j).getNameList()))) {
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, gr.getName()),
              gr.get_SourcePositionStart());
        }
      }
    }
  }
}
