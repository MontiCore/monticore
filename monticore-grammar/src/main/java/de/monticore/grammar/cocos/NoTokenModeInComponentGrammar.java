/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTLexProd;
import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.se_rwth.commons.logging.Log;

public class NoTokenModeInComponentGrammar implements GrammarASTMCGrammarCoCo {
  public static final String ERROR_CODE = "0xA4068";

  public static final String ERROR_MSG_FORMAT = " The lexical production %s must not define a mode in a Component Grammar.";
  @Override
  public void check(ASTMCGrammar node) {
    if( node.isComponent()){
      for(ASTLexProd prod : node.getLexProdList()){
        if (prod.isPresentMode()){
          Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, prod.getName()),
              node.get_SourcePositionStart());
        }
      }

    }
  }
}
