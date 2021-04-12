/* (c) https://github.com/MontiCore/monticore */
package de.monticore.grammar.cocos;

import de.monticore.grammar.grammar._ast.ASTMCGrammar;
import de.monticore.grammar.grammar._cocos.GrammarASTMCGrammarCoCo;
import de.monticore.grammar.grammar._symboltable.MCGrammarSymbol;
import de.monticore.grammar.grammar._symboltable.ProdSymbol;
import de.monticore.grammar.grammar._symboltable.RuleComponentSymbol;
import de.se_rwth.commons.logging.Log;

/**
 * checks if a prod defining a symbol has a list of names.
 */
public class SymbolWithManyNames implements GrammarASTMCGrammarCoCo {

  public static final String ERROR_CODE = "0xA0279";

  public static final String ERROR_MSG_FORMAT = "Production %s is a symbol and defines a list of names";

  @Override
  public void check(ASTMCGrammar node) {
    MCGrammarSymbol grammarSymbol = node.getSymbol();
    // for every prod of the current grammar
    for (ProdSymbol prod : grammarSymbol.getProds()) {
      if (prod.isIsSymbolDefinition()) {
        for (RuleComponentSymbol component : prod.getProdComponents()) {
          if ("name".equals(component.getName()) && component.isIsList()) {
            Log.error(String.format(ERROR_CODE + ERROR_MSG_FORMAT, prod.getName()),
                    prod.getAstNode().get_SourcePositionStart());
          }
        }
      }
    }
  }

}
