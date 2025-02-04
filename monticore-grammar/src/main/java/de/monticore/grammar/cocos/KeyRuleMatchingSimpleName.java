/* (c) https://github.com/MontiCore/monticore */

package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.se_rwth.commons.logging.Log;

import static de.monticore.grammar.LexNamer.NAME_PATTERN;

/**
 * Checks that keywords that are replaced by names also match "Name"
 */
public class KeyRuleMatchingSimpleName implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA0145";

  public static final String ERROR_MSG_FORMAT = "The keyword %s does not match the standard name pattern.";

  @Override
  public void check(ASTMCGrammar gr) {
    MCGrammarSymbol grSymbol = gr.getSymbol();
    for (String str: grSymbol.getNoKeywordsList()) {
      if (!NAME_PATTERN.matcher(str).matches()) {
        Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, str), gr.get_SourcePositionStart());
      }
    }
  }

}
