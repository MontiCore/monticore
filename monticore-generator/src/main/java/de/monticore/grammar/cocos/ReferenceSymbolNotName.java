package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTNonTerminal;
import de.monticore.grammar.grammar._cocos.GrammarASTNonTerminalCoCo;
import de.se_rwth.commons.logging.Log;

public class ReferenceSymbolNotName implements GrammarASTNonTerminalCoCo {

  public static final String ERROR_CODE = "0xA4039";

  public static final String ERROR_MSG_FORMAT = " You can only refer to other symbols on the nonterminal Name.";

  @Override
  public void check(ASTNonTerminal node) {
    if (node.isPresentReferencedSymbol()) {
      if(!node.getName().equals("Name")){
        Log.error(ERROR_CODE + ERROR_MSG_FORMAT,
            node.get_SourcePositionStart());
      }
    }
  }
}
